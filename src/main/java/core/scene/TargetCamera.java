package core.scene;

import core.math.MathUtils;
import core.math.MatrixUtils;
import org.joml.Matrix3f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import core.Engine;
import core.utils.BufferUtils;
import core.window.Window;

import java.nio.DoubleBuffer;

public class TargetCamera extends BaseCamera {

	private Vector3f target;
	private float zoomAmount;
	private float minZoom;
	private float maxZoom;

	public TargetCamera() {
		super();
		target = new Vector3f(0.0f, 0.0f, 0.0f);
		zoomAmount = 1.0f;
		minZoom = -20.f;
		maxZoom = 20.f;

	}

	public void update(float time) {

		handleMouse(time);
		position.mul(new Matrix3f().rotateX(mouseDirection.x).mul(new Matrix3f().rotateY(mouseDirection.y)));

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

	private DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
	private DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
	private Vector2f lastMousePos = new Vector2f();
	private Vector2f mouseDirection = new Vector2f();

	private void handleMouse(float time) {
		Window window = Engine.getPrimaryWindow();
		xBuffer.clear(); yBuffer.clear();
		GLFW.glfwGetCursorPos(window.getWindowId(), xBuffer, yBuffer);
		float xPosMouse = (float) xBuffer.get();
		float yPosMouse = (float) yBuffer.get();

		if (GLFW.glfwGetMouseButton(window.getWindowId(), GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_PRESS) {
			mouseDirection.set(lastMousePos).sub(xPosMouse, yPosMouse);
			mouseDirection.normalize();
			System.out.println(mouseDirection.x + " " + mouseDirection.y);


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
