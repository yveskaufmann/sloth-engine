package core.input;

import core.input.event.KeyEvent;
import core.input.event.MouseEvent;

/**
 * A interface for receiving input data from a InputProvider.
 */
public interface InputReceiver {

	/**
	 * Invoked on a mouse event is provided.
	 *
	 * @param event provided mouse event
	 */
	void onMouseEvent(MouseEvent event);


	/**
	 * Invoked on a key event is provided
	 *
	 * @param keyEvent provided key event
	 */
	void onKeyEvent(KeyEvent keyEvent);
}
