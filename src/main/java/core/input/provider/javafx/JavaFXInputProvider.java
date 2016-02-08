package core.input.provider.javafx;

import core.input.InputReceiver;
import core.input.event.InputEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Base class for a javafx based input provider.
 */
public abstract class JavaFXInputProvider<T extends InputEvent, E extends javafx.scene.input.InputEvent> implements EventHandler<E> {

	/**
	 * Event queue
	 */
	protected final Queue<T> eventQueue;

	/**
	 * Node element from which key events are retrieved
	 */
	protected final Node source;

	/**
	 * Indicates if this provider was already initialized
	 */
	protected boolean initialized = false;
	/**
	 * A receiver which receives the retrieved input events.
	 */
	protected InputReceiver receiver;

	public JavaFXInputProvider(Node node) {
		source = node;
		eventQueue = new ConcurrentLinkedQueue<>();
		initialized = false;
	}

	/**
	 * Retrieves if the InputProver was already initialized.
	 *
	 * @return true if already initialized
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Add a input provided handler which should receive
	 * the input events.
	 *
	 * @param inputReceiver which receives the input events of this provider.
	 */
	public void setInputReceiver(InputReceiver inputReceiver) {
		receiver = inputReceiver;
	}
}
