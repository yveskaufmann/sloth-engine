package window;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import renderer.RendererManager;
import sandbox.EngineContext;
import utils.Cleanable;

import java.nio.IntBuffer;

/**
 * The window class which encapsulates the
 * window which is used to show the rendering result.
 */
public class Window implements Cleanable {

	private static final Logger Log = LoggerFactory.getLogger(Window.class);

	private long windowId;
	private String title;
	private GLFWWindowSizeCallback sizeCallback;

	private State state;

	private enum State {
		ENABLED,
		DISABLED,
		CLEANED
	}

	Window(long windowId, String title) {
		this.windowId = windowId;
		this.title = title;
		this.state = State.DISABLED;
	}

	public Window setTitle(String title) {
		this.title = title;
		GLFW.glfwSetWindowTitle(windowId, title);
		return this;
	}

	public long getWindowId() {
		return windowId;
	}

	public int getWidth() {
		IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetWindowSize(windowId, widthBuffer, null);
		return widthBuffer.get();
	}

	public Window setWidth(int width) {
		setSize(width, getHeight());
		return this;
	}

	public int getHeight() {
		IntBuffer heightBuffer =  BufferUtils.createIntBuffer(1);
		GLFW.glfwGetWindowSize(windowId, null, heightBuffer);
		return heightBuffer.get();
	}

	public Window setHeight(int height) {
		setSize(getWidth(), height);
		return this;
	}

	public Window setSize(int width, int height) {

		width = Math.abs(width);
		height = Math.abs(height);

		width = (width < 1) ? 1 : width;
		height = (height < 1) ? 1 : height;

		GLFW.glfwSetWindowSize(windowId, width, height);
		return this;
	}

	public boolean shouldClose() {
		return GLFW.glfwWindowShouldClose(windowId) == GLFW.GLFW_TRUE;
	}

	public Window enable() {

		if (state != State.DISABLED) {
			throw new IllegalStateException("This window was already enabled");
		}

		state = State.ENABLED;
		GLFW.glfwMakeContextCurrent(windowId);
		GL.createCapabilities();
		GLFW.glfwSetWindowSizeCallback(windowId, sizeCallback = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				updateViewportSize();
			}
		});
		GLFW.glfwShowWindow(windowId);
		updateViewportSize();
		Log.info("Enabled and show window {} and make it to the current OpenGL context.", windowId);

		return this;
	}

	public Window disable() {
		if (state == State.DISABLED) {
			throw new IllegalStateException("This window was already disabled");
		}

		GLFW.glfwSetWindowSizeCallback(windowId, null);
		GLFW.glfwMakeContextCurrent(0L);

		return this;
	}

	public Window update() {
		GLFW.glfwSwapBuffers(windowId);
		GLFW.glfwPollEvents();
		return this;
	}

	@Override
	public void clean() {
		if (state == State.CLEANED) {
			throw new IllegalArgumentException("The Window was already cleaned");
		}

		if (state == State.DISABLED) {
			disable();
		}

		GLFW.glfwDestroyWindow(windowId);
		state = State.CLEANED;
	}

	private void updateViewportSize() {
		EngineContext
			.getCurrentRenderer()
			.setViewport(0, 0, getWidth(), getHeight());
	}



}
