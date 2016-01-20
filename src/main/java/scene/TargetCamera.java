package scene;

import org.joml.Vector3f;

public class TargetCamera extends BaseCamera {

	private Vector3f target;

	public TargetCamera() {
		super();
		target = new Vector3f(0.0f, 0.0f, 0.0f);
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
			-right.dot(position), -directionUp.dot(position), direction.dot(position), 1.0f
		);
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
