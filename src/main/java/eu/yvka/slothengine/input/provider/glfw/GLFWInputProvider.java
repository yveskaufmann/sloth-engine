package eu.yvka.slothengine.input.provider.glfw;

import eu.yvka.slothengine.input.event.InputEvent;
import eu.yvka.slothengine.input.provider.BaseInputProvider;
import eu.yvka.slothengine.window.Window;


public class GLFWInputProvider<T extends InputEvent> extends BaseInputProvider<T> {
	/**
	 * Window id of the primary window
	 */
	protected long windowId;


	public GLFWInputProvider(Window window) {
		this(window.getWindowId());
	}

	public GLFWInputProvider(long windowId) {
		this.windowId = windowId;
	}

}
