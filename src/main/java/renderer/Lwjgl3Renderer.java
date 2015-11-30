package renderer;

import geometry.Mesh;
import shader.Shader;
import shader.ShaderSource;
import texture.Image;
import texture.Texture;
import geometry.VertexBuffer;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;


public class Lwjgl3Renderer implements Renderer {

	@Override
	public void setRenderState(RenderState state) {

	}

	@Override
	public void invalidateState() {

	}

	@Override
	public void setClearColor(int r, int h, int b) {

	}

	@Override
	public void clearBuffers(boolean color, boolean stencil, boolean depth) {

	}

	@Override
	public void setViewport(int x, int y, int width, int height) {
		glViewport(x, y, width, height);
	}

	@Override
	public void setDepthRange(float near, float far) {
		glDepthRange(near, far);
	}

	@Override
	public void setClipRect(int x, int y, int width, int height) {
	}

	@Override
	public void clearClipRect() {
	}

	@Override
	public void setShader(Shader shader) {

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
	public void OnNewFrame() {

	}
}
