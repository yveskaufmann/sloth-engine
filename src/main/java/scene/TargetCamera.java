package scene;

import math.MathUtils;
import org.joml.Vector3f;

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

		/**
		 * Performs the matrix multiplication:
		 *   r[x,y,z] = Side-Vector
		 *   u[x,y,z] = Normal Vector of the direction / Upward
		 *   d[x,y,z] = Direction Vector
		 *   __             __     __            __
		 *  |  rx  ry  rz  0  |   |  1  0  0  -px |
		 *  |  ux  uy  uz  0  |   |  0  1  0  -py |
		 *  | -dx -dy -dy  0  | X |  0  0  1  -pz |
		 *  |  0   0   0   1  |   |  0  0  0   1  |
		 *  |__             __|   |__           __|
		 */
		viewMatrix.set(
			right.x, directionUp.x, -direction.x, 0.0f,
			right.y, directionUp.y, -direction.y, 0.0f,
			right.z, directionUp.z, -direction.z, 0.0f,
			-right.dot(zoomPosition), -directionUp.dot(zoomPosition), direction.dot(zoomPosition), 1.0f
		);
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
