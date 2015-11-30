package renderer;

import geometry.VertexBuffer;
import geometry.Mesh;
import shader.Shader;
import shader.ShaderSource;
import texture.Image;
import texture.Texture;

/**
 * Interface for as simple Flexible pipeline parser
 */
public interface Renderer {

	void setRenderState(RenderState state);
	void invalidateState();

	void setClearColor(int r, int h, int b );
	void clearBuffers(boolean color, boolean stencil, boolean depth);

	void setViewport(int x, int y, int width, int height);
	void setDepthRange(float near, float far);
	void setClipRect(int x, int y, int width, int height);
	void clearClipRect();

	void setShader(Shader shader);
	void deleteShader(Shader shader);
	void deleteShaderSource(ShaderSource source);

	void setTexture(int unit, Texture texture);
	void deleteTexture(Texture texture);
	void deleteImage(Image image);

	void drawMesh(Mesh mesh, int lod, int count);
	void updateBuffer(VertexBuffer buffer);
	void deleteBuffer(VertexBuffer buffer);

	void cleanUp();
	void resetGLObjects();

	void OnNewFrame();
}
