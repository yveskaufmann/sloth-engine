package de.yvka.slothengine.scene.camera;

import de.yvka.slothengine.engine.Engine;
import de.yvka.slothengine.input.InputManager;
import de.yvka.slothengine.input.provider.KeyInputProvider;
import de.yvka.slothengine.math.MathUtils;
import de.yvka.slothengine.window.Window;
import de.yvka.slothengine.input.provider.KeyInputProvider;
import org.joml.Vector2d;
import org.joml.Vector3f;

public class FreeCamera extends Camera {

	private float horizontalAngle;
	private float verticalAngle;
	private float mouseSpeed = 5.0f;

	public FreeCamera() {
		horizontalAngle = (float) Math.PI;
		position.set(0, 0, 0);
		viewMatrix.identity().lookAt(position, new Vector3f(position).add(direction), up);
		// Engine.register(new FPSCounter());
	}

	@Override
	public void update(float time) {
		InputManager inputManager = Engine.getInputManager();
		Window window = Engine.getPrimaryWindow();
		int centerX = window.getWidth() >> 1;
		int centerY = window.getHeight() >> 1;

		Vector2d mousePos = inputManager.getMousePosition();

		float offsetX = (float) (centerX - mousePos.x);
		float offsetY = (float) (centerY - mousePos.y);

		offsetX /= mouseSpeed;
		offsetY /= mouseSpeed;


		horizontalAngle += mouseSpeed * time * offsetX;
		verticalAngle += mouseSpeed * time * offsetY;

		// Prevents the Camera from vertically flipping
		verticalAngle = MathUtils.clamp(verticalAngle, -1.5f, 1.5f);

		//horizontalAngle = 0;
		//verticalAngle = 0;

		direction.set(
			(float) (Math.cos(verticalAngle) * Math.sin(horizontalAngle)),
			(float) (Math.sin(verticalAngle)),
			(float) (Math.cos(verticalAngle) * Math.cos(horizontalAngle))
		).normalize();


		direction.cross(up, right);
		right.cross(direction, directionUp);

		viewMatrix.identity().lookAt(position, new Vector3f(position).add(direction), up);
		// Capture the cursor in the primary window

		inputManager.setMousePosition(centerX, centerY);
		inputHandling(time);
	}

	private void inputHandling(float time) {
		InputManager inputManager = Engine.getInputManager();
		if (inputManager.isKeyPressed(KeyInputProvider.KeyButton.W)) {
			walk(mouseSpeed, time);
		}

		if (inputManager.isKeyPressed(KeyInputProvider.KeyButton.S)) {
			walk(-mouseSpeed, time);
		}

		if (inputManager.isKeyPressed(KeyInputProvider.KeyButton.A)) {
			strafe(-mouseSpeed, time);
		}

		if (inputManager.isKeyPressed(KeyInputProvider.KeyButton.D)) {
			strafe(mouseSpeed, time);
		}

		if (inputManager.isKeyPressed(KeyInputProvider.KeyButton.R)) {
			lift(mouseSpeed, time);
		}

		if (inputManager.isKeyPressed(KeyInputProvider.KeyButton.F)) {
			lift(-mouseSpeed, time);
		}
	}

	public void walk(float speed, float time) {
		Vector3f dir = new Vector3f(direction).mul(time * speed);
		position.add(dir);
	}

	public void strafe(float speed, float time) {
		Vector3f dir = new Vector3f(right).mul(time * speed);
		position.add(dir);
	}

	public void lift(float speed, float time) {
		Vector3f dir = new Vector3f(directionUp).mul(time * speed);
		position.add(dir);
	}

	public void setTarget(Vector3f direction) {
		Vector3f dir = new Vector3f(position).sub(direction).normalize();
		// Converts vector into unit sphere coordinate
		horizontalAngle = (float) (Math.atan2(dir.x, dir.z) + Math.PI);
		verticalAngle = (float) (-Math.atan(dir.y / Math.sqrt(dir.z * dir.z + dir.x * dir.x)));

	}

	public static void main(String[] args) {
		System.out.println(Math.sin(90 * Math.PI / 180));
	}
}
