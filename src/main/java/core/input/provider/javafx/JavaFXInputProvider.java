package core.input.provider.javafx;

import core.input.event.InputEvent;
import core.input.provider.BaseInputProvider;
import javafx.event.EventHandler;
import javafx.scene.Node;

/**
 * Base class for a javafx based input provider.
 */
public abstract class JavaFXInputProvider<T extends InputEvent, E extends javafx.scene.input.InputEvent> extends BaseInputProvider<T> implements EventHandler<E> {

	/**
	 * Node element from which key events are retrieved
	 */
	protected final Node source;

	public JavaFXInputProvider(Node node) {
		super();
		source = node;
	}

}
