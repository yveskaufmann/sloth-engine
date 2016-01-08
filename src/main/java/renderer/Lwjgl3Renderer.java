package renderer;

import geometry.Mesh;
import math.Color;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shader.Shader;
import shader.source.ShaderSource;
import shader.ShaderType;
import texture.Image;
import texture.Texture;
import geometry.VertexBuffer;
import utils.HardwareObject;


import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL43.*;


public class Lwjgl3Renderer implements Renderer {

	private static final Logger Log = LoggerFactory.getLogger(Lwjgl3Renderer.class);


	private IntBuffer intBuffer = IntBuffer.allocate(1);

	private int viewPortX;
	private int viewportY;
	private int viewPortWidth;
	private int viewPortHeight;
	private boolean isScissorTestEnabled;
	private int scissorX;
	private int scissorY;
	private int scissorWidth;
	private int scissorHeight;
	private GLCapabilities caps;

	private Map<Integer, Shader> shaders;
	private int activeShaderId;
	private boolean validationRequired;

	public Lwjgl3Renderer() {
		initialize();
	}

	private void initialize() {
		validationRequired = true;
		viewPortX = 0;
		viewportY = 0;
		viewPortWidth = -1;
		viewPortHeight = -1;
		shaders = new HashMap<>();
		activeShaderId = HardwareObject.UNSET_ID;
		caps =  GL.getCapabilities();

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
		if ( !isScissorTestEnabled ) {
			isScissorTestEnabled = true;
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
		if (isScissorTestEnabled) {
			isScissorTestEnabled = false;
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
		useShader(shader);
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
	}

	private void useShader(Shader shader) {
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

	}

	@Override
	public void deleteShaderSource(ShaderSource source) {

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

		if (mesh.getId() == Mesh.UNSET_ID) {
			mesh.setId(glGenVertexArrays());
			glBindVertexArray(mesh.getId());
			// storeInFloatAttributeList(0, null);
			glBindVertexArray(0);

		}

		glBindVertexArray(mesh.getId());
		glEnableVertexAttribArray(0);
		glDrawArrays(mesh.getId(), 0, mesh.getVertexCount());
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);


	}

/*
	protected void storeInFloatAttributeList(int index, FloatBuffer vertexBuffer) {
		storeInFloatAttributeList(index, vertexBuffer, com.sun.prism.impl.VertexBuffer.STATIC_DRAW);
	}

	protected void storeInFloatAttributeList(int index, FloatBuffer vertexBuffer) {
		int vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, mesh.getUsage().value());
		glVertexAttribPointer(index, 3, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

	}
*/
	@Override
	public void updateBuffer(VertexBuffer buffer) {

	}

	@Override
	public void deleteBuffer(VertexBuffer buffer) {

	}

	@Override
	public void cleanUp() {

	}

	@Override
	public void resetGLObjects() {

	}

	@Override
	public void onNewFrame() {

	}

	@Override
	public void setRenderState(RenderState state) {

	}

	@Override
	public void invalidateState() {

	}

}
