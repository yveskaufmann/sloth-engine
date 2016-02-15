package core.input.provider.javafx;

import core.input.provider.MouseInputProvider;
import core.input.event.MouseEvent;
import core.math.MathUtils;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;

import java.awt.*;

/**
 * A JavaFXMouseProvider retrieves mouse events from a specified node
 * object and delegate it to registered receivers.
 *
 */
public class JavaFXMouseProvider extends JavaFXInputProvider<MouseEvent, javafx.scene.input.MouseEvent> implements MouseInputProvider {

	/**
	 * The amount how far the mouse wheel was rotated.
	 */
	private double mouseWheelAmount;

	/**
	 * Indicates if the mouse wheel was moved.
	 */
	private boolean mouseWheelMoved;

	/**
	 * Previous x cursor position
	 */
	private int previousX;

	/**
	 * Previous y cursor position
	 */
	private int previousY;



	/**
	 * Creates a JavaFXMouseProvider.
	 *
	 * @param node the node which acts as source for mouse events.
	 */
	public JavaFXMouseProvider(Node node) {
		super(node);
	}

	/**
	 * Moves the pointer to the specified position,
	 * the position is relative to the viewport.
	 *
	 * @param x the x position of the cursor
	 * @param y the y position of the cursor
	 */
	@Override
	public void moveCursor(int x, int y) {
		System.out.println(x);
		System.out.println(y);
		Point2D sourceNodePos = source.localToScreen(x, y);
		System.out.println(sourceNodePos);
		try {
			Robot robot = new Robot();
			robot.mouseMove((int) (sourceNodePos.getX()), (int) sourceNodePos.getY());
		} catch (AWTException e) {
			e.printStackTrace();
		}

	}

	/**
	 * initializes the native side of the input provider.
	 */
	@Override
	public void initialize() {
		if (!initialized) {
			Platform.runLater(() -> {
				source.addEventHandler(javafx.scene.input.MouseEvent.ANY, this);
				source.addEventHandler(javafx.scene.input.ScrollEvent.SCROLL, this::handleWheelEvents);
			});
			initialized = true;
		}
	}

	/**
	 * Destroy the native handler.
	 */
	@Override
	public void shutdown() {
		if (initialized) {
			Platform.runLater(() -> {
				source.removeEventHandler(javafx.scene.input.MouseEvent.ANY, this);
				source.removeEventHandler(javafx.scene.input.ScrollEvent.SCROLL, this::handleWheelEvents);
			});
			eventQueue.clear();
			initialized = false;
		}
	}

	/**
	 * Queries the input state from the native devices
	 * and delegate it to a InputHandler.
	 */
	@Override
	public void update() {
		MouseEvent event;
		if (mouseWheelMoved) {
			event = new MouseEvent(MouseButton.None, false, previousX, previousY, mouseWheelAmount);
			receiver.onMouseEvent(event);
			mouseWheelMoved = false;
		}

		synchronized (eventQueue) {
			while ((event = eventQueue.poll()) != null) {
				receiver.onMouseEvent(event);
			}
			eventQueue.clear();
		}
	}

	/**
	 * Invoked when a specific event of the type for which this handler is
	 * registered happens.
	 *
	 * @param event the event which occurred
	 */
	@Override
	public void handle(javafx.scene.input.MouseEvent event) {
		MouseButton mouseButton = toInputMouseEventButton(event.getButton());
		boolean isPressed = event.getEventType().equals(javafx.scene.input.MouseEvent.MOUSE_PRESSED)
			|| event.getEventType().equals(javafx.scene.input.MouseEvent.MOUSE_DRAGGED);

		MouseEvent mouseEvent = new MouseEvent(
			toInputMouseEventButton(event.getButton()),
			isPressed,
			(int) event.getX(),
			(int) event.getY(),
			mouseWheelAmount
		);

		previousX = (int) event.getX();
		previousY = (int) event.getY();

		synchronized (eventQueue) {
			eventQueue.add(mouseEvent);
		}
		event.consume();
	}

	private MouseButton toInputMouseEventButton(javafx.scene.input.MouseButton button) {
		switch (button) {
			case NONE:
				return MouseButton.None;
			case MIDDLE:
				return MouseButton.Middle;
			case PRIMARY:
				return MouseButton.Second;
			case SECONDARY:
				return MouseButton.Primary;
		}
		return MouseButton.None;
	}


	private void handleWheelEvents(javafx.scene.input.ScrollEvent event) {
		mouseWheelAmount = event.getDeltaY() / event.getMultiplierY();
		mouseWheelMoved = true;
		System.out.println(mouseWheelAmount);
		event.consume();
	}
}
