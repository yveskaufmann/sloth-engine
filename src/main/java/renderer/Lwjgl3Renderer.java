package renderer;

import geometry.Mesh;
import geometry.VertexAttributePointer;
import geometry.VertexBuffer;
import math.Color;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import renderer.font.FontRenderer;
import shader.*;
import shader.source.ShaderSource;
import texture.image.Image;
import texture.Texture;
import utils.HardwareObject;
import utils.HardwareObjectManager;

import java.awt.*;
import java.nio.*;

import static geometry.VertexBuffer.Type;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL43.*;
import static renderer.RenderState.CullFaceMode;
import static renderer.RenderState.TestFunc;

public class Lwjgl3Renderer implements Renderer {

	private static final Logger Log = LoggerFactory.getLogger(Lwjgl3Renderer.class);


	private HardwareObjectManager objectManager;
	private RenderContext ctx;
	private double lastTime;
	private int renderedFrames;
	private FontRenderer fontRenderer;
	private int currentFps;
	private GLCapabilities caps;

	public Lwjgl3Renderer() {
		initialize();
	}

	private void initialize() {
		objectManager = new HardwareObjectManager();
		ctx = new RenderContext();
		lastTime = glfwGetTime();
		fontRenderer = new FontRenderer(new Font("Courier", Font.PLAIN, 32));
		renderedFrames = 0;
		caps = GL.getCapabilities();
	}

	@Override
	public void setClearColor(Color clearColor) {
		glClearColor(
			clearColor.getRed(),
			clearColor.getGreen(),
			clearColor.getBlue(),
			clearColor.getAlpha()
		);
	}

	@Override
	public void clearBuffers(boolean color, boolean depth, boolean stencil) {
		int mask = 0;

		if (color) {
			mask |= GL_COLOR_BUFFER_BIT;
		}

		if (depth) {
			mask |= GL_DEPTH_BUFFER_BIT;
		}

		if (stencil) {
			mask |= GL_STENCIL_BUFFER_BIT;
		}

		glClear(mask);

	}

	@Override
	public void setViewport(int x, int y, int width, int height) {
		if (x != ctx.viewPortX || y != ctx.viewportY || width != ctx.viewPortWidth || height != ctx.viewPortHeight)  {
			ctx.viewPortX = x;
			ctx.viewportY = y;
			ctx.viewPortWidth = width;
			ctx.viewPortHeight = height;
			glViewport(x, y, width, height);
		}

	}

	@Override
	public void setDepthRange(float near, float far) {
		glDepthRange(near, far);
	}

	@Override
	public void setClipRect(int x, int y, int width, int height) {
		if ( !ctx.scissorTest ) {
			ctx.scissorTest = true;
			glEnable(GL_SCISSOR_TEST);
		}

		if (x != ctx.scissorX || y != ctx.scissorY|| width != ctx.scissorWidth || height != ctx.scissorHeight ) {
			ctx.scissorX = x;
			ctx.scissorY = y;
			ctx.scissorWidth = width;
			ctx.scissorHeight = height;
			glScissor(x, y, width, height);
		}
	}

	@Override
	public void clearClipRect() {
		if (ctx.scissorTest) {
			ctx.scissorTest = false;
			glDisable(GL_SCISSOR_TEST);

			ctx.scissorX = 0;
			ctx.scissorY = 0;
			ctx.scissorWidth = 0;
			ctx.scissorHeight = 0;
		}
	}


	@Override
	public void setShader(Shader shader) {
		assert shader != null;

		if (shader.isUpdateRequired()) {
			updateShaderData(shader);
		}

		bindShaderProgram(shader);
		updateShaderUniforms(shader);
	}


	private void updateShaderData(Shader shader) {
		int id  = shader.getId();
		if (id == HardwareObject.UNSET_ID) {
			id = glCreateProgram();

			if (id == 0) {
				throw new RendererExpception(
					"Failed to create a new ShaderProgram: invalid ShaderProgram id ("+ id +") received."
				);
			}
			shader.setId(id);
			objectManager.register(shader);
		}

		for (ShaderSource source : shader.getShaderSources()) {
			if (source.isUpdateRequired()) {
				updateShaderSource(source);
			}
			glAttachShader(id, source.getId());
		}

		boolean linkSuccess;
		String infoLog;

		glLinkProgram(id);
		linkSuccess = glGetProgrami(id, GL_LINK_STATUS) == GL_TRUE;
		infoLog = glGetProgramInfoLog(shader.getId());

		if (linkSuccess) {
			Log.info("Shader link success");
			shader.disableUpdateRequired();
			resetUniformLocation(shader);
		} else {
			Log.error("Failed to link shader program {}\n{}", shader, infoLog);
		}

	}


	private void updateShaderSource(ShaderSource source) {
		int id = source.getId();
		if (id == HardwareObject.UNSET_ID) {
			id = glCreateShader(toShaderTypeConstant(source.getType()));
			if (id == 0) {
				throw new RendererExpception("Failed to created shader invalid id received: %d", id);
			}
			source.setId(id);
			objectManager.register(source);
		}

		glShaderSource(id, source.getSource());
		glCompileShader(id);

		boolean compileSuccess = glGetShaderi(id, GL_COMPILE_STATUS) == GL_TRUE;
		if (!compileSuccess) {
			String infoLog = glGetShaderInfoLog(id);
			Log.error("Failed to compile shader {}\n{}", source.toString(), infoLog);
		} else {
			Log.info("Success to compile shader {}", source.toString());
		}
		source.disableUpdateRequired();

	}

	private void updateShaderUniforms(Shader shader) {
		for (Uniform uniform : shader.getUniforms()) {
			if (uniform.isUpdateRequired()) {
				updateShaderUniform(shader, uniform);
			}
		}
	}

	private void resetUniformLocation(Shader shader) {
		for (Uniform uniform : shader.getUniforms()) {
			uniform.reset();
		}
	}


	private void updateShaderUniform(Shader shader, Uniform uniform) {
		int location = uniform.getLocation();
		ShaderVariable.VariableType type = uniform.getType();

		if (location == ShaderVariable.LOCATION_UNKNOWN) {
			updateUniformLocation(shader, uniform);
			if (uniform.getLocation() == ShaderVariable.LOCATION_NOT_FOUND) {
				// Ensures that and not found uniform variable is handled only once
				uniform.disableUpdateRequired();
				return;
			}
			location = uniform.getLocation();
		}

		if (type == null) {
			Log.error("The {} of the shader {} must have a assigned type.", uniform, shader);
			uniform.disableUpdateRequired();
			return;
		}

		Object value = uniform.getValue();
		FloatBuffer fb;
		switch (type) {
			case Float:
				float f1 = (float) value;
				glUniform1f(location, f1);
				break;
			case Float2:
				float[] fv2 = (float[]) value;
				glUniform2f(location, fv2[0], fv2[1]);
				break;
			case Float3:
				float[] fv3 = (float[]) value;
				glUniform3f(location, fv3[0], fv3[1], fv3[2]);
				break;
			case Float4:
				float[] fv4 = (float[]) value;
				glUniform4f(location, fv4[0], fv4[1], fv4[2], fv4[3]);
				break;

			case Int:
				int int1 = (int) value;
				glUniform1f(location, int1);
				break;
			case Int2:
				int[] int2 = (int[]) value;
				glUniform2f(location, int2[0], int2[1]);
				break;
			case Int3:
				float[] int3 = (float[]) value;
				glUniform3f(location, int3[0], int3[1], int3[2]);
				break;
			case Int4:
				float[] int4 = (float[]) value;
				glUniform4f(location, int4[0], int4[1], int4[2], int4[3]);
				break;

			case Matrix3x3:
				fb = (FloatBuffer) value;
				glUniformMatrix3fv(location, false, fb);
				break;
			case Matrix4x4:
				fb = (FloatBuffer) value;
				glUniformMatrix4fv(location, false, fb);
				break;
		}

		uniform.disableUpdateRequired();
	}

	private void updateUniformLocation(Shader shader, ShaderVariable shaderVariable) {
		String name = shaderVariable.getName();
		int location = glGetUniformLocation(shader.getId(), name);
		if (location == ShaderVariable.LOCATION_NOT_FOUND) {
			Log.warn("Could not find the uniform variable {} in the shader {}", name, shader);
		}
		shaderVariable.setLocation(location);
	}

	private void bindShaderProgram(Shader shader) {
		int shaderId = shader.getId();
		if (ctx.boundShader == null || ctx.boundShader.getId() != shaderId) {
			glUseProgram(shaderId);
			ctx.boundShader = shader;
		}
	}

	private int toShaderTypeConstant(ShaderType type) {
		switch (type) {
			case VERTEX:
				return GL_VERTEX_SHADER;
			case FRAGMENT:
				return GL_FRAGMENT_SHADER;
			case GEOMETRY:
				return GL_GEOMETRY_SHADER;
			case COMPUTE:
				return GL_COMPUTE_SHADER;
		}
		throw new IllegalArgumentException("Invalid Shader type specified");
	}

	@Override
	public void deleteShader(Shader shader) {
		if (shader.getId() == HardwareObject.UNSET_ID) {
			Log.warn("Shader Program is not uploaded to GPU, cannot be deleted");
			return;
		}

		for (ShaderSource source : shader.getShaderSources()) {
			if (source.getId() != HardwareObject.UNSET_ID) {
				glDetachShader(shader.getId(), source.getId());
				deleteShaderSource(source);
			}
		}

		glDeleteProgram(shader.getId());
		shader.resetObject();
	}

	@Override
	public void deleteShaderSource(ShaderSource source) {
		if (source.getId() == HardwareObject.UNSET_ID) {
			Log.warn("Shader is not uploaded to GPU, cannot be deleted");
			return;
		}

		glDeleteShader(source.getId());
		source.enableUpdateRequired();
		source.resetObject();
	}

	@Override
	public void setTexture(int unit, Texture texture) {
		if (texture.isUpdateRequired()) {
			updateTextureData(texture, unit);
		}
		bindTexture(texture, unit);
	}

	private void updateTextureData(Texture texture, int unit) {
		boolean created = false;
		int id = texture.getId();
		if (id == HardwareObject.UNSET_ID) {
			id = glGenTextures();
			texture.setId(id);
			objectManager.register(texture);
			created = true;
		}

		unit = Math.max(0, unit);
		if (unit > GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS - 1) {
			String description = "Texture unit limit reached";
			Log.error("{} must be between {} and {}", description, 0, GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS);
			throw new RendererExpception(description);
		}


		// check gpu caps set settings if required
		// loadImageData
		int target = bindTexture(texture, unit);
		Image image = texture.getImage();
		if (image != null && image.isUpdateRequired()) {
			ByteBuffer imageData = image.getReader().getBuffer();
			if (created) {
				switch (target) {
					case GL_TEXTURE_1D:
						glTexImage1D(target,0, GL_RGBA, image.getWidth() * image.getHeight(),0, GL_RGBA, GL_UNSIGNED_INT, imageData);
						break;
					case GL_TEXTURE_2D:
						glTexImage2D(target, 0, GL_RGBA, image.getWidth(), image.getHeight(),0, GL_RGBA, GL_UNSIGNED_BYTE, imageData);
						break;
				}
			} else {
				switch (target) {
					case GL_TEXTURE_1D:
						glTexSubImage1D(target, GL_RGBA,0, image.getWidth() * image.getHeight(), GL_RGBA, GL_UNSIGNED_INT, imageData);
						break;
					case GL_TEXTURE_2D:
						glTexSubImage2D(target, 0, 0, 0, image.getWidth(), image.getHeight(), GL_RGBA, GL_UNSIGNED_BYTE, imageData);
						break;
				}
			}
		}

		// TODO: implementation must depend on texture
		// NODE: only for the first time
		glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(target, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(target, GL_TEXTURE_WRAP_T, GL_REPEAT);

		if (texture.getAnisotropicLevel() >= 1.0) {
			if (caps.GL_EXT_texture_filter_anisotropic) {
				glTexParameterf(target, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, texture.getAnisotropicLevel());
			}
		}

		texture.disableUpdateRequired();
	}

	private int bindTexture(Texture texture, int unit) {
		int id = texture.getId();
		if(ctx.activeTextureUnit != unit) {
			glActiveTexture(GL_TEXTURE0 + unit);
			ctx.activeTextureUnit = unit;
		}

		int target = convertTypeToTarget(texture.getType());
		if (ctx.boundTextures[unit] != id) {
			glBindTexture(target, id);
			ctx.boundTextures[unit] = id;
		}

		return target;
	}

	private int convertTypeToTarget(Texture.Dimension type) {
		switch (type) {
			case DIMENSION_1D: return GL_TEXTURE_1D;
			case DIMENSION_2D: return GL_TEXTURE_2D;
			default: throw new RendererExpception("Unsupported texture type detected: {}", type.name());
		}
	}

	@Override
	public void deleteTexture(Texture texture) {
		int id = texture.getId();
		glDeleteTextures(id);
		texture.resetObject();
		deleteImage(texture.getImage());
	}

	@Override
	public void deleteImage(Image image) {

	}

	@Override
	public void drawMesh(Mesh mesh, int lod, int count) {
		if (mesh.getVertexCount() <= 0) {
			return;
		}

		if (mesh.getLineWidth() != ctx.lineWith) {
			if (mesh.getLineWidth() <= .0f) throw new RendererExpception("Line width must be greater than zero");
			glLineWidth(mesh.getLineWidth());
			ctx.lineWith = mesh.getLineWidth();
		}

		if (mesh.getPointSize() != ctx.pointSize) {
			if (mesh.getPointSize() <= .0f) throw new RendererExpception("Point size must be greater than zero");
			glPointSize(mesh.getPointSize());
			ctx.pointSize = mesh.getPointSize();
		}

		renderMesh(mesh, lod, count);
	}

	private void renderMesh(Mesh mesh, int lod, int count) {
		if (ctx.boundShader == null) {
			throw new RendererExpception("In order to render a mesh a shader must first bound to the renderer");
		}

		VertexBuffer interleavedBuffer = mesh.getBuffer(VertexBuffer.Type.Interleaved);
		if (interleavedBuffer != null && interleavedBuffer.isUpdateRequired()) {
			updateBuffer(interleavedBuffer);
		}

		for (VertexBuffer buffer : mesh.getBuffers()) {
			Type type = buffer.getType();
			if (Type.Index.equals(type) ||
				Type.Interleaved.equals(type) ||
				Type.CpuOnly.equals(type)) continue;

			setVertexAttributes(buffer, interleavedBuffer);
		}


		VertexBuffer indices = mesh.getBuffer(Type.Index);
		if (indices != null) {
			drawTrianglesWithIndices(indices, mesh, count);
		} else {
			drawTriangles(mesh, count);
		}

		clearVertexAttributes();
	}




	private void drawTrianglesWithIndices(VertexBuffer indices, Mesh mesh, int count) {
		if (indices.getType() != Type.Index) {
			throw new IllegalArgumentException("An index buffer is required for the indices parameter");
		}


		if (indices.isUpdateRequired()) {
			updateBuffer(indices);
		}

		glDrawElements(
			convertToMode(mesh.getMode()),
			indices.getBuffer().limit(),
			convertToFormat(indices.getPointer().getFormat()),
			0
		);

	}

	private void clearVertexAttributes() {
		if (ctx.boundShader != null) {
			for(Attribute attribute : ctx.boundShader.getAttributes()) {
				glDisableVertexAttribArray(attribute.getLocation());
			}
		}
	}


	private void setVertexAttributes(VertexBuffer buffer, VertexBuffer interleavedBuffer) {
		assert ctx.boundShader != null;

		Attribute attribute = ctx.boundShader.getAttribute(buffer.getType());
		int location = attribute.getLocation();
		if (location == Attribute.LOCATION_UNKNOWN) {
			if (attribute.getName() == null) {
				throw new RendererExpception("An attribute requires a name, please consider to set a name for each attribute");
			}
			location = glGetAttribLocation(ctx.boundShader.getId(), attribute.getName());
			if (location == Attribute.LOCATION_NOT_FOUND) {
				Log.warn("The attribute {} isn't an active attribute in the shader {}.\nThe attribute could not be bounded to the shader.", attribute.getName());
				attribute.disableUpdateRequired();
				return;
			}
			attribute.setLocation(location);
		}

		if (interleavedBuffer == null) {
			updateBuffer(buffer);
		} else {
			updateBuffer(interleavedBuffer);
		}

		glEnableVertexAttribArray(location);
		glVertexAttribPointer(
			location,
			buffer.getPointer().getComponents(),
			convertToFormat(buffer.getPointer().getFormat()),
			buffer.getPointer().getNormalized(),
			buffer.getPointer().getStride(),
			buffer.getPointer().getOffset());

	}

	private void drawTriangles(Mesh mesh, int count) {

		if (mesh.getBuffer(Type.Vertex) != null) {
			count = mesh.getVertexCount();
		}

		glDrawArrays(convertToMode(mesh.getMode()), 0, count);
	}

	private int convertToMode(Mesh.Mode mode) {
		switch (mode) {
			case POINTS: return GL_POINT;
			case LINES: return GL_LINES;
			case LINE_LOOP: return GL_LINE_LOOP;
			case LINE_STRIP: return GL_LINE_STRIP;
			case TRIANGLES: return GL_TRIANGLES;
			case TRIANGLE_FAN: return GL_TRIANGLE_FAN;
			case TRIANGLE_STRIP: return GL_TRIANGLE_STRIP;
			default:
				throw new IllegalArgumentException(
					String.format("Unsupported mesh mode specified %s", mode.name())
				);
		}
	}

	private int convertToFormat(VertexAttributePointer.Format format) {
		switch (format) {
			case Byte: return GL_BYTE;
			case Int: return GL_INT;
			case Short: return GL_SHORT;
			case Float: return  GL_FLOAT;
			case Double: return  GL_DOUBLE;
			case Unsigned_Int: return GL_UNSIGNED_INT;
			case Unsigned_Short: return GL_UNSIGNED_SHORT;
			case Unsingned_Byte: return GL_UNSIGNED_BYTE;
			default:
				throw new IllegalArgumentException(String.format("Unsupported format specified %s", format));

		}
	}



	@Override
	public void updateBuffer(VertexBuffer buffer) {
		int target;
		int bufferId = buffer.getId();
		boolean bufferCreated  = false;

		if (bufferId == HardwareObject.UNSET_ID) {
			bufferId = glGenBuffers();
			if (bufferId == GL_INVALID_VALUE) {
				throw new RendererExpception("Invalid buffer object name returned, creation of buffer object failed");
			}
			buffer.setId(bufferId);
			bufferCreated = true;
			objectManager.register(buffer);
		}


		if (buffer.getType() == Type.Index) {
			target = GL_ELEMENT_ARRAY_BUFFER;
			if (ctx.boundElementArrayVboBuffer != bufferId) {
				glBindBuffer(target, bufferId);
				ctx.boundElementArrayVboBuffer = bufferId;
			}
		} else {
			target = GL_ARRAY_BUFFER;
			if (ctx.boundVboBuffer != bufferId) {
				glBindBuffer(target, bufferId);
				ctx.boundVboBuffer = bufferId;
			}
		}

		int usage = convertToUsageConstant(buffer.getUsage());
		if (bufferCreated) {
			switch (buffer.getPointer().getFormat()) {
				case Byte:
				case Unsingned_Byte:
					glBufferData(target, (ByteBuffer) buffer.getBuffer(), usage);
					break;
				case Short:
				case Unsigned_Short:
					glBufferData(target, (ShortBuffer) buffer.getBuffer(), usage);
					break;
				case Int:
				case Unsigned_Int:
					glBufferData(target, (IntBuffer) buffer.getBuffer(), usage);
					break;
				case Float:
					glBufferData(target, (FloatBuffer) buffer.getBuffer(), usage);
					break;
				case Double:
					glBufferData(target, (DoubleBuffer) buffer.getBuffer(), usage);
					break;
				default:
					throw new RendererExpception("Unknown buffer format");

			}
		} else if(buffer.isUpdateRequired()) {
			switch (buffer.getPointer().getFormat()) {
				case Byte:
				case Unsingned_Byte:
					glBufferSubData(target, 0,(ByteBuffer) buffer.getBuffer());
					break;
				case Short:
				case Unsigned_Short:
					glBufferSubData(target, 0,(ShortBuffer) buffer.getBuffer());
					break;
				case Int:
				case Unsigned_Int:
					glBufferSubData(target, 0, (IntBuffer) buffer.getBuffer());
					break;
				case Float:
					glBufferSubData(target, 0,  (FloatBuffer) buffer.getBuffer());
					break;
				case Double:
					glBufferSubData(target, 0,  (DoubleBuffer) buffer.getBuffer());
					break;
				default:
					throw new RendererExpception("Unknown buffer format");

			}
		}
		buffer.disableUpdateRequired();
	}

	private int convertToUsageConstant(VertexBuffer.Usage usage) {
		switch (usage) {
			case DYNAMIC_COPY: return GL_DYNAMIC_COPY;
			case DYNAMIC_DRAW: return GL_DYNAMIC_DRAW;
			case DYNMAIC_READ: return GL_DYNAMIC_READ;
			case STATIC_COPY:  return GL_STATIC_COPY;
			case STATIC_DRAW:  return GL_STATIC_DRAW;
			case STATIC_READ:  return GL_STATIC_READ;
			case STREAM_COPY:  return GL_STREAM_COPY;
			case STREAM_DRAW:  return GL_STATIC_DRAW;
			case STREAM_READ:  return GL_STATIC_READ;
			default:
				throw new RendererExpception("Unrecognized usage specified.");
		}

	}

	@Override
	public void deleteBuffer(VertexBuffer buffer) {
		int id = buffer.getId();
		if (id != HardwareObject.UNSET_ID) {
			glDeleteBuffers(id);
			buffer.resetObject();
		}

	}

	@Override
	public void applyRenderState(RenderState state) {

		if (state.isWireframe() && !ctx.wireframe) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			ctx.wireframe = true;
		} else if (!state.isWireframe() && ctx.wireframe) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
			ctx.wireframe = false;
		}

		if (state.getDepthTestMode() != ctx.depthTestMode) {
			if (toggleEnable(GL_DEPTH_TEST, state.getDepthTestMode() != TestFunc.Off)) {
				glDepthFunc(toTestFunction(state.getDepthTestMode()));
			}
		}

		if (state.getCullFaceMode() != ctx.cullFaceMode) {
			if (toggleEnable(GL_CULL_FACE, state.getCullFaceMode() != CullFaceMode.Off)) {
				glCullFace(toCullFaceMode(state.getCullFaceMode()));
			}
			ctx.cullFaceMode = state.getCullFaceMode();
		}

		ctx.fpsCounterEnabled = state.isFPSCounterEnabled();

		if (state.getBlendMode() != ctx.blendMode) {
			switch (state.getBlendMode()) {
				case Off:
					glDisable(GL_BLEND);
					break;
				case Default:
					glEnable(GL_BLEND);
					glBlendFunc(GL_ONE, GL_ZERO);
					break;
				case Additive:
					glEnable(GL_BLEND);
					glBlendFunc(GL_ONE, GL_ONE);
					break;
				case Alpha:
					glEnable(GL_BLEND);
					glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
					break;
				case Color:
					glEnable(GL_BLEND);
					glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_COLOR);

			}
			ctx.blendMode = state.getBlendMode();
		}

		if (state.getPointSize() != ctx.pointSize) {
			glPointSize(state.getPointSize());
			ctx.pointSize = state.getLineWidth();
		}

		if (toggleEnable(GL_LINE_SMOOTH, state.isSmoothLinesEnabled() != ctx.lineSmooth)) {
			ctx.lineSmooth = state.isSmoothLinesEnabled();
		}

		if (state.getLineWidth() != ctx.lineWith) {
			glLineWidth(state.getLineWidth());
			ctx.lineWith = state.getLineWidth();
		}
	}

	private boolean toggleEnable(int cap, boolean enable) {
		if (enable) {
			glEnable(cap);
		} else {
			glDisable(cap);
		}
		return enable;
	}

	private int toTestFunction(TestFunc testFunc) {
		switch (testFunc) {
			case Always: return GL_ALWAYS;
			case Equal: return GL_EQUAL;
			case Greater: return GL_GREATER;
			case GreaterOrEqual: return GL_GEQUAL;
			case Less: return GL_LESS;
			case LessOrEqual: return GL_LEQUAL;
			case Never: return GL_NEVER;
			case NotEqual: return GL_NOTEQUAL;
			default : throw new IllegalArgumentException("Unsupported test function specified");
		}
	}

	private int toCullFaceMode(CullFaceMode cullFaceMode) {
		switch (cullFaceMode) {
			case Back: return GL_BACK;
			case Front: return GL_FRONT;
			case FrontAndBack: return GL_FRONT_AND_BACK;
			case Off: throw new IllegalArgumentException("Unsupported cull face mode specified");
		}
		return 0;
	}

	@Override
	public void invalidateState() {
		ctx.reset();
	}

	@Override
	public void cleanUp() {
		invalidateState();
		objectManager.deleteAllObjects();
		Log.info("Clean up all renderer resources");
	}

	@Override
	public void resetGLObjects() {
		objectManager.resetAllObjects();
	}

	@Override
	public void onNewFrame() {
		if (ctx.fpsCounterEnabled) {
			fpsCounter();
		}
		objectManager.deleteAllUnused();

	}

	public void fpsCounter() {
		double currentTime = glfwGetTime();
		renderedFrames++;
		if (currentTime - lastTime >= 1.0) {
			currentFps = renderedFrames;
			renderedFrames = 0;
			lastTime += 1.0;

		}
		fontRenderer.drawString("FPS " + currentFps, 0, 0, 1.0f, Color.Red);


	}


}
