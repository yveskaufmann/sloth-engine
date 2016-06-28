package eu.yvka.slothengine.math;

public class MathUtils {

	public static final float PI = (float)Math.PI;
	public static final float PI_2 = PI / 2.0f;
	public static final float PI_180 = PI / 180;
	public static final float PI_DIV_180 = 180 / PI;

	/**
	 * Take a float number and clamp it between min
	 * and max.
	 *
	 * @param number the number to be clamped
	 * @param min
	 * @param max
	 * @return clamped number
	 */
	public static float clamp(float number, float min, float max) {
		return (number < min) ? min : (number > max) ? max : number;
	}

	public static float toRadians(float degrees) {
		return degrees * PI_180;
	}

	public static float toDegrees(float radians) {
		return radians * PI_DIV_180;
	}
}
