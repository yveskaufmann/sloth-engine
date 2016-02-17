package core.scene;

import core.engine.Engine;
import core.input.InputManager;
import core.window.Window;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class FreeCamera extends Camera {

	private float horizontalAngle;
	private float verticalAngle;
	private float mouseSpeed = 2.0f;
	private Vector2f lastMousePos = null;


	@Override
	public void update(float time) {

		Window window = Engine.getPrimaryWindow();
		int height = window.getHeight();
		int width = window.getWidth();

		InputManager inputManager = Engine.getInputManager();
		Vector2d mousePos = inputManager.getMousePosition();
		if (lastMousePos != null ) {
			horizontalAngle -= mouseSpeed * time * (float) (lastMousePos.x - mousePos.x);
			verticalAngle -= mouseSpeed * time * (float)  (lastMousePos.y - mousePos.y);
		} else {
			lastMousePos = new Vector2f();
		}

		Vector3f direction = new Vector3f(
			(float) (Math.cos(verticalAngle) * Math.sin(horizontalAngle)),
			(float) (Math.sin(verticalAngle)),
			(float) (Math.cos(verticalAngle) * Math.cos(horizontalAngle))
		);


		viewMatrix.identity().lookAt(position, new Vector3f(position).add(direction), new Vector3f(0, 1, 0));
		lastMousePos.set(mousePos);
	}
}
