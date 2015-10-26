package sandbox;


import org.lwjgl.glfw.GLFWKeyCallback;
import util.NativeLibraryLoader;
import window.Window;
import window.WindowManager;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Sandbox {

	static {
		NativeLibraryLoader.load();
	}

	private GLFWKeyCallback keyCallback;

	public void run() throws Exception {

		Window window = WindowManager.get()
			.setTitle("Window the first")
			.setSize(1024, 768)
			.setResizeable(true)
			.build();

		glfwSetKeyCallback(window.getWindowId(), keyCallback = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (key == GLFW_KEY_ESCAPE) {
					glfwSetWindowShouldClose(window, GL_TRUE);
				}
			}
		});

		window.enable();
		while (! window.shouldClose()) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			window.update();
		}

		keyCallback.release();
		WindowManager.get().clean();

	}

	public static void main(String[] args) throws Exception {
		Sandbox box = new Sandbox();
		box.run();
	}
}
