package core.input.provider;

/**
 * Interface for a key input provider.
 */
public interface KeyInputProvider extends InputProvider {
	/**
	 * KeyButton constants
	 */
	enum KeyButton {
		Esc,
		F1,
		F2,
		F3,
		F4,
		F5,
		F6,
		F7,
		F8,
		F9,
		F10,
		F11,
		F12,
		BackQuote,
		Digit0,
		Digit1,
		Digit2,
		Digit3,
		Digit4,
		Digit5,
		Digit6,
		Digit7,
		Digit8,
		Digit9,
		Minus,
		Equals,
		BackSpace,
		Tab,
		Q,
		W,
		E,
		R,
		T,
		Y,
		U,
		I,
		O,
		P,
		OpenBracket,
		ClosedBracket,
		CapsLock,
		A,
		S,
		D,
		F,
		G,
		H,
		J,
		K,
		L,
		Semicolon,
		SingleQuote,
		BackSlash,
		Enter,
		Shift,
		Z,
		X,
		C,
		V,
		B,
		N,
		M,
		Comma,
		Point,
		Slash,
		Ctrl,
		Meta,
		Alt,
		Space,
		AltGraph,
		Left,
		Up,
		Right,
		Down,
		Insert,
		Delete,
		Home,
		End,
		PageUp,
		PageDown,
		NumLock,
		NumPad_Divide,
		NumPad_Multiply,
		NumPad_Minus,
		NumPad_7,
		NumPad_8,
		NumPad_9,
		NumPad_Plus,
		NumPad_4,
		NumPad_5,
		NumPad_6,
		NumPad_1,
		NumPad_2,
		NumPad_3,
		NumPad_0,
		NumPad_Comma,
		Pause,
		SCROLL_LOCK,
		Print,
		None;

	}

	/**
	 * Determines if the specified keyButton is pressed down.
	 * The method must be implemented thread safe.
	 *
	 * @param keyButton the specified keyButton.
	 *
	 * @return true when the keyButton is actually pressed otherwise false if the keyButton is released.
	 */
	boolean isKeyPressed(KeyButton keyButton);
}
