package core.input.provider;

/**
 * Interface for a mouse input provider.
 */
public interface  MouseInputProvider extends InputProvider {

	/**
	 * Possible pressed/pressed button.
	 */
	enum MouseButton {
		Primary,
		Second,
		Middle,
		None
	}

	/**
	 * Moves the pointer to the specified position,
	 * the position is relative to the viewport.
	 *
	 * @param x
	 * @param y
	 */
	void moveCursor(int x, int y);
}
