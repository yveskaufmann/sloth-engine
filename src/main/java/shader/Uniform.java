package shader;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import utils.BufferUtils;

import java.nio.FloatBuffer;

public class Uniform {

	public static final int LOCATION_NOT_FOUND = -1;
	public static final int LOCATION_UNKNOWN = -2;



	public enum VariableType {
		Float,
		Float2,
		Float3,
		Float4,

		Int,
		Int2,
		Int3,
		Int4,

		Matrix3x3,
		Matrix4x4,
	}

	protected int location;
	private VariableType type;
	private String name;
	private Object value;
	private boolean updateRequired = false;

	public Uniform() {
		reset();
	}

	public void setValue(float value) {
		setValue(VariableType.Float, value);
	}

	public void setValue(float v1, float v2) {
		setValue(VariableType.Float2, new float[] {v1, v2});
	}

	public void setValue(float v1, float v2, float v3) {
		setValue(VariableType.Float3, new float[] {v1, v2, v3});
	}

	public void setValue(float v1, float v2, float v3, float v4) {

		setValue(VariableType.Float4, new float[] {v1, v2, v3, v4});
	}

	public void setValue(int v1) {
		setValue(VariableType.Int, v1);
	}

	public void setValue(int v1, int v2) {
		setValue(VariableType.Int2, new int[] {v1, v2});
	}

	public void setValue(int v1, int v2, int v3) {
		setValue(VariableType.Int3, new int[] {v1, v2, v3});
	}

	public void setValue(int v1, int v2, int v3, int v4) {
		setValue(VariableType.Int4, new int[] {v1, v2, v3, v4});
	}

	public void setValue(Matrix4f value) {
		setValue(VariableType.Matrix4x4, value);
	}

	public void setValue(VariableType type, Object value) {
		switch (type) {
			case Float:
			case Float2:
			case Float3:
			case Float4:
			case Int:
			case Int2:
			case Int3:
			case Int4:
				this.value = value;
				break;
			case Matrix3x3:
				Matrix3f matrix3f = (Matrix3f) value;
				if (value != null) {
					this.value = BufferUtils.createFloatBuffer(9);
				}
				matrix3f.get((FloatBuffer) this.value);
				break;
			case Matrix4x4:
				Matrix4f matrix4f = (Matrix4f) value;
				if (value != null) {
					this.value = BufferUtils.createFloatBuffer(16);
				}
				matrix4f.get((FloatBuffer) this.value);
				break;

		}

		this.type = type;
		this.updateRequired = true;
	}

	public Object getValue() {
		return value;
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public VariableType getType() {
		return type;
	}


	public boolean isUpdateRequired() {
		return this.updateRequired;
	}

	public void disableUpdateRequired() {
		this.updateRequired = false;
	}

	public void reset() {
		location = LOCATION_UNKNOWN;
		updateRequired = true;
	}

}
