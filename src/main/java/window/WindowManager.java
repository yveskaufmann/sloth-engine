package window;

import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.*;
import utils.Cleanable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for creation of Window objects.
 */
public class WindowManager implements Cleanable {

	public static final int DEFAULT_WIDTH = 1024;
	public static final int DEFAULT_HEIGHT = 768;
	public static final String DEFAULT_TITLE = "Untitled";

	private static final WindowManager instance = new WindowManager();
	public static final WindowManager get() {
		return instance;
	}

	private Map<Integer, Integer> windowHints = null;
	private List<Window> windows = null;
	private int height;
	private int width;
	private String title;

	private WindowManager() {

		if ( GL_TRUE != glfwInit()) {
			throw new IllegalStateException("The glfw init failed");
		}
		windowHints = new HashMap<>();
		windows = new ArrayList<>();
		setWidth(DEFAULT_WIDTH);
		setHeight(DEFAULT_HEIGHT);
		setTitle(DEFAULT_TITLE);
		reset();
	}

	private WindowManager reset() {
		windowHints.clear();
		GLFW.glfwDefaultWindowHints();
		setWidth(DEFAULT_WIDTH).
		setHeight(DEFAULT_HEIGHT);
		return this;
	}

	@Override
	public void clean() {
		reset();
		for (Window window : windows) {
			window.clean();
		}
		glfwTerminate();
	}

	public WindowManager setSize(int width, int height) {
		setWidth(width);
		setHeight(height);
		return this;
	}

	public WindowManager setWidth(int width) {
		this.width = width;
		return this;
	}

	public WindowManager setHeight(int height) {
		this.height = height;
		return this;
	}

	public WindowManager setTitle(String title) {
		this.title = title;
		return this;
	}

	public WindowManager enableWindowAutoIconify(boolean enabled) {
		return setWindowHint(GLFW.GLFW_AUTO_ICONIFY, enabled);
	}

	public WindowManager enableWindowFloating(boolean enable) {
		return setWindowHint(GLFW.GLFW_FLOATING, enable);
	}

	public WindowManager setFocused(boolean focused) {
		return setWindowHint(GLFW.GLFW_FOCUSED, focused);
	}

	public WindowManager setResizeable(boolean resizeable) {
		return setWindowHint(GLFW.GLFW_RESIZABLE, resizeable);
	}


	public WindowManager setGLContextVersion(int major, int minor) {
		setWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, major);
		setWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, minor);
		return this;
	}

	public WindowManager setWindowHint(int target, int hint) {
	 	windowHints.put(target, hint);
		return this;
	}

	public WindowManager setWindowHint(int target, boolean hint) {
		windowHints.put(target, hint ? 1 : 0);
		return this;
	}

	public Window getLastActiveWindow() {
		if (windows.size() == 0) return null;
		return windows.get(windows.size() - 1);
	}

	public Window build() {
		windowHints.forEach(GLFW::glfwWindowHint);

		long windowId = GLFW.glfwCreateWindow(width, height, title, NULL, NULL);
		if (windowId == NULL) {
			throw new WindowException("Failed to create a window");
		}

		Window window = new Window(windowId, title);
		windows.add(window);
		reset();

		return window;
	}
}
