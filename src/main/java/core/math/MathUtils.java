package core.math;

public class MathUtils {

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
}
