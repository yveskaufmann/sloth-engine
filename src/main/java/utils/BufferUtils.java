package utils;

import static org.lwjgl.BufferUtils.*;
import java.nio.*;

public class BufferUtils {

	/**
	 * Creates a FloatBuffer from a specified float array.
	 *
	 * @param buffer the float array
	 * @return the created FloatBuffer.
     */
	public static FloatBuffer createBuffer(float[] buffer) {
		return (FloatBuffer) createFloatBuffer(buffer.length).put(buffer).flip();
	}

	/**
	 * Creates a IntBuffer from a specified int array.
	 *
	 * @param buffer the int array
	 * @return the created IntBuffer.
	 */
	public static IntBuffer createBuffer(int[] buffer) {
		return (IntBuffer) createIntBuffer(buffer.length).put(buffer).flip();
	}

	/**
	 * Creates a ShortBuffer from a specified short array.
	 *
	 * @param buffer the short array
	 * @return the created ShortBuffer.
	 */
	public static ShortBuffer createBuffer(short[] buffer) {
		return (ShortBuffer) createShortBuffer(buffer.length).put(buffer).flip();
	}

	/**
	 * Creates a ByteBuffer from a specified byte array.
	 *
	 * @param buffer the byte array
	 * @return the created ByteBuffer.
	 */
	public static ByteBuffer createBuffer(byte[] buffer) {
		return (ByteBuffer) createByteBuffer(buffer.length).put(buffer).flip();
	}

	/**
	 * Creates a FloatBuffer with a specified capacity.
	 *
	 * @param capacity the capacity of the new created buffer.
	 * @return the new created float buffer.
     */
	public static FloatBuffer createFloatBuffer(int capacity) {
		return org.lwjgl.BufferUtils.createFloatBuffer(capacity);
	}

	/**
	 * Creates a FloatBuffer with a specified capacity.
	 *
	 * @param capacity the capacity of the new created buffer.
	 * @return the new created float buffer.
	 */
	public static IntBuffer createIntBuffer(int capacity) {
		return org.lwjgl.BufferUtils.createIntBuffer(capacity);
	}
}
