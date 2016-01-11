package shader;

/**
 * Abstraction for a shader variable like an uniform or a attribute.
 */
public class ShaderVariable {

	public static final int LOCATION_NOT_FOUND = -1;
	public static final int LOCATION_UNKNOWN = -2;

	protected int location;

	protected boolean updateRequired = false;
	protected String name;

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

	public boolean isUpdateRequired() {
		return this.updateRequired;
	}

	public void disableUpdateRequired() {
		this.updateRequired = false;
	}


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
}
