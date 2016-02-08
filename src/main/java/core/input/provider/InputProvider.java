package core.input.provider;

import core.engine.EngineComponent;
import core.input.InputReceiver;

/**
 * A input provider ia a abstraction of a input device
 * api in order to support different input sources.
 *
 * For example it is possible to receive input events from a javafx
 * Node or from a native library such as glfw.
 */
public interface InputProvider extends EngineComponent {

	/**
	 * Queries the input state from the native devices
	 * and delegate it to a InputHandler.
	 */
	void update();

	/**
	 * Add a input provided handler which should receive
	 * the input events.
	 *
	 * @param inputReceiver which receives the input events of this provider.
	 */
	void setInputReceiver(InputReceiver inputReceiver);

}
