package core.input.provider.glfw;

import core.input.event.InputEvent;
import core.input.provider.BaseInputProvider;
import core.window.Window;


public class GLFWInputProvider<T extends InputEvent> extends BaseInputProvider<T>  {
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
