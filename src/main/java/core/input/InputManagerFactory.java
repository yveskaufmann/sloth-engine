package core.input;

import core.input.provider.glfw.GLFWKeyInputProvider;
import core.input.provider.glfw.GLFWMouseInputProvider;
import core.input.provider.javafx.JavaFXKeyProvider;
import core.input.provider.javafx.JavaFXMouseProvider;
import core.window.Window;
import javafx.scene.Node;


public class InputManagerFactory {

	/**
	 * Creates a input manager which uses a javafx node as
	 * source for input events.
	 *
	 * @param node the source of input events.
	 *
	 * @return the created input manager.
	 */
	public static InputManager createInputManager(Node node) {
		return new InputManager(new JavaFXMouseProvider(node), new JavaFXKeyProvider(node));
	}

	/**
	 * Creates a input manager which uses a glfw window as
	 * source for input events.
	 *
	 * @param window the source of input events.
	 *
	 * @return the created input manager.
	 */
	public static InputManager createInputManager(Window window) {
		return new InputManager(new GLFWMouseInputProvider(window), new GLFWKeyInputProvider(window));
	}
}
