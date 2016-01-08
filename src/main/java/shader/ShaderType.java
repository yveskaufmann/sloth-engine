package shader;

public enum  ShaderType {
	VERTEX,
	FRAGMENT,
	GEOMETRY,
	COMPUTE;

	@Override
	public String toString() {
		return this.name() + "_SHADER";
	}
}
