package nifty.jwgl3.input;

import de.lessvoid.nifty.NiftyInputConsumer;
import de.lessvoid.nifty.spi.input.InputSystem;
import de.lessvoid.nifty.tools.resourceloader.NiftyResourceLoader;
import org.lwjgl.glfw.GLFW;


public class Lwjgl3InputSystem implements InputSystem {

	private final long windowId;

	public Lwjgl3InputSystem(long windowId) {
		this.windowId = windowId;
	}


	public void startup() {

	}

	public void shutdown() {

	}

	/**
	 * Gives this InputSystem access to the NiftyResourceLoader.
	 *
	 * @param niftyResourceLoader NiftyResourceLoader
	 */
	@Override
	public void setResourceLoader(NiftyResourceLoader niftyResourceLoader) {

	}

	/**
	 * This method is called by Nifty when it's ready to process input events. The InputSystem implementation should
	 * call the methods on the given NiftyInputConsumer to forward events to Nifty.
	 *
	 * @param inputEventConsumer the NiftyInputConsumer to forward input events to
	 */
	@Override
	public void forwardEvents(NiftyInputConsumer inputEventConsumer) {

	}

	/**
	 * This allows Nifty to set the position of the mouse to the given coordinate with {@code (0, 0)} being the
	 * upper left corner of the screen.
	 *
	 * @param x x coordinate of mouse
	 * @param y y coordinate of mouse
	 */
	@Override
	public void setMousePosition(int x, int y) {
		GLFW.glfwSetCursorPos(windowId, x, y);
	}
}
