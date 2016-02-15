package core.scene;

import core.input.InputManager;
import core.input.provider.MouseInputProvider;
import core.math.MathUtils;
import core.math.MatrixUtils;
import org.joml.Matrix3f;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import core.engine.Engine;
import core.utils.BufferUtils;
import core.window.Window;

import java.nio.DoubleBuffer;

public class TargetCamera extends Camera {

	private Vector3f target;
	private float zoomAmount;
	private float minZoom;
	private float maxZoom;

	private float horizontalAngle;
	private float verticalAngle;
	private float mouseSpeed;

	public TargetCamera() {
		super();
		target = new Vector3f(0.0f, 0.0f, 0.0f);
		zoomAmount = 1.0f;
		minZoom = -20.f;
		maxZoom = 100.f;
		mouseSpeed = 50.0f;
	}

	public void update(float time) {

		handleMouse(time);

		Matrix3f rotationXY = new Matrix3f();
		rotationXY.rotateX(verticalAngle).rotateY(horizontalAngle);
		// new Matrix3f().rotateX(verticalAngle).mul(new Matrix3f().rotateY(horizontalAngle))
		position.mul(rotationXY);

		// Calculate direction vector
		target.sub(position, direction);
		direction.normalize();

		// Calculate right/ side vector
		direction.cross(up, right);
		right.normalize();

		// Calculate the directionUp or y-Axis vector
		right.cross(direction, directionUp);
		directionUp.normalize();

		// Camera zoom position in order to simulate camera zoom
		Vector3f zoomPosition = this.direction.mul(zoomAmount, new Vector3f()).add(position);

		MatrixUtils.from(zoomPosition, right, directionUp, direction, viewMatrix);
	}

	private Vector2f lastMousePos = new Vector2f();
	private Vector2f mouseDirection = new Vector2f();

	private void handleMouse(float time) {
		Window window = Engine.getPrimaryWindow();
		float height = window.getHeight();
		float width = window.getWidth();
		InputManager inputManager = Engine.getInputManager();
		Vector2d mousePos = inputManager.getMousePosition();
		// Convert to dvi coordinates
		float xPosMouse = (float) (mousePos.x / width) * 2 - 1;
		float yPosMouse = (float) (mousePos.y / height) * 2 - 1;

		if (inputManager.isMouseButtonPressed(MouseInputProvider.MouseButton.Middle)) {
			mouseDirection.set(lastMousePos).sub(xPosMouse, yPosMouse);
			// mouseDirection.normalize();
			horizontalAngle -= mouseSpeed * time * mouseDirection.x;
			verticalAngle += mouseSpeed * time * mouseDirection.y;

			/*
			if (mousePos.x  > width) inputManager.setMousePosition(0, (int) mousePos.y);
			if (mousePos.x  < 0) inputManager.setMousePosition((int) width, (int) mousePos.y);
			if (mousePos.y > height) inputManager.setMousePosition((int) mousePos.x, 0);
			if (mousePos.y < 0) inputManager.setMousePosition((int) mousePos.x, (int) height);
			*/
		}
		lastMousePos.set(xPosMouse, yPosMouse);
	}

	public void zoom(float zoomAmount) {
		this.zoomAmount = MathUtils.clamp(zoomAmount, minZoom, maxZoom);
	}

	public void setMinZoom(float minZoom) {
		this.minZoom = minZoom;
	}

	public void setMaxZoom(float maxZoom) {
		this.maxZoom = maxZoom;
	}

	public float getMaxZoom() {
		return maxZoom;
	}

	public float getMinZoom() {
		return minZoom;
	}

	public void setTarget(Vector3f target) {
		this.target.set(target);
	}

	public void setTarget(float x, float y, float z) {
		this.target.set(x, y, z);
	}

	public Vector3f getTarget() {
		return this.target;
	}
}
