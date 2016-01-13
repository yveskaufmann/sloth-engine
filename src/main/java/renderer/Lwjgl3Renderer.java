package renderer;

import geometry.Mesh;
import geometry.VertexAttributePointer;
import math.Color;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shader.*;
import shader.source.ShaderSource;
import texture.Image;
import texture.Texture;
import geometry.VertexBuffer;
import utils.HardwareObject;

import static geometry.VertexBuffer.Type;

import java.nio.*;
import java.util.Iterator;
import java.util.function.Predicate;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL43.*;

import static org.lwjgl.glfw.GLFW.*;



public class Lwjgl3Renderer implements Renderer {

	private static final Logger Log = LoggerFactory.getLogger(Lwjgl3Renderer.class);


	private IntBuffer intBuffer = IntBuffer.allocate(1);
	private RenderContext ctx;

	private int viewPortX;
	private int viewportY;
	private int viewPortWidth;
	private int viewPortHeight;
	private int scissorX;
	private int scissorY;
	private int scissorWidth;
	private int scissorHeight;
	private GLCapabilities caps;

	private boolean validationRequired;

	private double lastTime;
	private int renderedFrames;


	public Lwjgl3Renderer() {
		initialize();
	}

	private void initialize() {
		validationRequired = true;
		viewPortX = 0;
		viewportY = 0;
		viewPortWidth = -1;
		viewPortHeight = -1;
		caps =  GL.getCapabilities();
		ctx = new RenderContext();

		lastTime = glfwGetTime();
		renderedFrames = 0;



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
		if (x != viewPortX || y != viewportY || width != viewPortWidth || height != viewPortHeight)  {
			viewPortX = x;
			viewportY = y;
			viewPortWidth = width;
			viewPortHeight = height;
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

		if (x != scissorX || y != scissorY|| width != scissorWidth || height != scissorHeight ) {
			scissorX = x;
			scissorY = y;
			scissorWidth = width;
			scissorHeight = height;
			glScissor(x, y, width, height);
		}
	}

	@Override
	public void clearClipRect() {
		if (ctx.scissorTest) {
			ctx.scissorTest = false;
			glDisable(GL_SCISSOR_TEST);

			scissorX = 0;
			scissorY = 0;
			scissorWidth = 0;
			scissorHeight = 0;
		}
	}

	@Override
	public void setShader(Shader shader) {
		assert shader != null;

		if (shader.isUpdateRequired()) {
			updateShaderData(shader);
		}

		updateShaderUniforms(shader);
		bindShaderProgram(shader);
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
		}

		for (ShaderSource source : shader.getShaderSources()) {
			if (source.isUpdateRequired()) {
				updateShaderSource(source);
			}
			glAttachShader(id, source.getId());
		}

		boolean linkSuccess = false;
		boolean validateSuccess = true;
		String infoLog = null;

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
		boolean newShader = false;
		int id = source.getId();
		if (id == HardwareObject.UNSET_ID) {
			id = glCreateShader(toShaderTypeConstant(source.getType()));
			newShader = true;
			if (id == 0) {
				throw new RendererExpception("Failed to created shader invalid id received: %d", id);
			}
			source.setId(id);
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

	}

	private void updateShaderUniforms(Shader shader) {
		Iterator<Uniform> it = shader.getUniformIterator();
		while(it.hasNext()) {
			Uniform uniform = it.next();
			if (uniform.isUpdateRequired()) {
				updateShaderUniform(shader, uniform);
			}
		}
	}

	private void resetUniformLocation(Shader shader) {
		Iterator<Uniform> it = shader.getUniformIterator();
		while(it.hasNext()) {
			Uniform uniform = it.next();
			uniform.reset();
		}
	}


	private void updateShaderUniform(Shader shader, Uniform uniform) {
		int location = uniform.getLocation();
		ShaderVariable.VariableType type = uniform.getType();

		// bindShaderProgram() required ?
		if (location == ShaderVariable.LOCATION_NOT_FOUND) return;
		if (location == ShaderVariable.LOCATION_UNKNOWN) {
			updateUniformLocation(shader, uniform);
			if (uniform.getLocation() == ShaderVariable.LOCATION_NOT_FOUND) {
				uniform.disableUpdateRequired();
				return;
			}
			location = uniform.getLocation();
		}

		if (type == null) return;

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

	}

	@Override
	public void deleteTexture(Texture texture) {

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

		mesh.bufferStream()
			.filter(whenNot(Type.Index))
			.filter(whenNot(Type.Interleaved))
			.filter(whenNot(Type.CpuOnly))
			.forEach(buffer -> {
				setVertexAttributes(buffer, interleavedBuffer);
			});

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

		int indexId = indices.getId();
		assert indexId != 1;

		glDrawElements(
			convertToMode(mesh.getMode()),
			indices.getBuffer().limit(),
			convertToFormat(indices.getPointer().getFormat()),
			0
		);

	}

	private void clearVertexAttributes() {
		// TODO
	}


	private void setVertexAttributes(VertexBuffer buffer, VertexBuffer interleavedBuffer) {
		assert ctx.boundShader != null;

		Attribute attribute = ctx.boundShader.getAttribute(buffer.getType());
		int location = attribute.getLocation();
		if (location == -1) {
			if (attribute.getName() == null) {
				throw new RendererExpception("An attribute requires a name, please consider to set a name for each attribute");
			}
			location = glGetAttribLocation(ctx.boundShader.getId(), attribute.getName());
			if (location == -1) {
				Log.warn("The attribute {} isn't an active attribute in the shader {}.\nThe attribute could not be bounded to the shader.", attribute.getName());
				return;
			}
			attribute.setLocation(location);
		}


		if (interleavedBuffer == null) {
			updateBuffer(buffer);
		} else {
			updateBuffer(interleavedBuffer);

		}

		glBindBuffer(GL_ARRAY_BUFFER, (interleavedBuffer != null) ? interleavedBuffer.getId() : buffer.getId());
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
		glDrawArrays(convertToMode(mesh.getMode()), 0, mesh.getVertexCount() + 1);
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

	private Predicate<VertexBuffer> whenNot(Type desiredType) {
		return (buffer) -> buffer.getType() != desiredType;
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
			glPolygonOffset(1.0f, 2.0f);
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			ctx.wireframe = true;
		} else if (!state.isWireframe() && ctx.wireframe) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
			ctx.wireframe = false;
		}

		if (state.getDepthTestMode() != ctx.depthTestMode) {
			if (toggleEnable(GL_DEPTH_TEST, state.getDepthTestMode() != RenderContext.TestFunc.Off)) {
				glDepthFunc(toTestFunction(state.getDepthTestMode()));
			}
		}

		if (state.getCullFaceMode() != ctx.cullFaceMode) {
			if (toggleEnable(GL_CULL_FACE, state.getCullFaceMode() != RenderContext.CullFaceMode.Off)) {
				glCullFace(toCullFaceMode(state.getCullFaceMode()));
			}
			ctx.cullFaceMode = state.getCullFaceMode();
		}

		ctx.fpsCounterEnabled = state.isFPSCounterEnabled();

	}

	private boolean toggleEnable(int cap, boolean enable) {
		if (enable) {
			glEnable(cap);
		} else {
			glDisable(cap);
		}
		return enable;
	}

	private int toTestFunction(RenderContext.TestFunc testFunc) {
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

	private int toCullFaceMode(RenderContext.CullFaceMode cullFaceMode) {
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
	}

	@Override
	public void resetGLObjects() {

	}

	@Override
	public void onNewFrame() {
		if (ctx.fpsCounterEnabled) {
			fpsCounter();
		}
	}

	public void fpsCounter() {
		double currentTime = glfwGetTime();
		renderedFrames++;

		if (currentTime - lastTime >= 1.0) {
            Log.info("Milli Seconds per Frame :" + 1000 / (double) renderedFrames);
            Log.info("Milli Seconds per Frame :" + renderedFrames);
            renderedFrames = 0;
            lastTime += 1.0;
        }
	}
}
