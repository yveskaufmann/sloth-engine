package renderer;

import geometry.VertexBuffer;
import geometry.Mesh;
import math.Color;
import org.lwjgl.opengl.GL41;
import shader.Shader;
import shader.source.ShaderSource;
import texture.Image;
import texture.Texture;

import java.io.IOException;

/**
 * Interface for as simple Flexible pipeline parser
 */
public interface Renderer {

	void setRenderState(RenderState state);
	void invalidateState();

	/**
	 * Defines the color buffer clear color.
	 *
	 * @param clearColor the color which should be used to clear
	 *                   color buffer.
     */
	void setClearColor(Color clearColor);

	/**
	 * Clear the color, depth and/or the stencil buffer.
	 *
	 * @param color if true the color buffer will be cleared.
	 * @param depth if true the depth buffer will be cleared.
	 * @param stencil if true the stencil will be cleared.
     */
	void clearBuffers(boolean color, boolean depth, boolean stencil);

	/**
	 *
	 * Specifies the viewport transformation parameters for all viewports.
	 *
	 * <p>The location of the viewport's bottom-left corner, given by {@code (x, y)}, are clamped to be within the implementation-dependent viewport bounds range.
	 * The viewport bounds range {@code [min, max]} tuple may be determined by calling {@link #glGetFloat GetFloat} with the symbolic
	 * constant {@link GL41#GL_VIEWPORT_BOUNDS_RANGE VIEWPORT_BOUNDS_RANGE}. Viewport width and height are clamped to implementation-dependent maximums when specified. The maximum
	 * width and height may be found by calling {@link #glGetFloat GetFloat} with the symbolic constant {@link #GL_MAX_VIEWPORT_DIMS MAX_VIEWPORT_DIMS}. The
	 * maximum viewport dimensions must be greater than or equal to the larger of the visible dimensions of the display being rendered to (if a display
	 * exists), and the largest renderbuffer image which can be successfully created and attached to a framebuffer object.</p>
	 *
	 * <p>In the initial state, {@code w} and {@code h} for each viewport are set to the width and height, respectively, of the window into which the GL is to do
	 * its rendering. If the default framebuffer is bound but no default framebuffer is associated with the GL context, then {@code w} and {@code h} are
	 * initially set to zero.</p>
	 *
	 * @param x the left viewport coordinate
	 * @param y the bottom viewport coordinate
	 * @param w the viewport width
	 * @param h the viewport height
	 */
	void setViewport(int x, int y, int width, int height);
	void setDepthRange(float near, float far);
	void setClipRect(int x, int y, int width, int height);
	void clearClipRect();

	void setShader(Shader shader) throws IOException;
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

	void onNewFrame();
}
