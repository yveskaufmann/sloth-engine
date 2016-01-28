package core.shader;

public enum  ShaderType {
	VERTEX("vert"),
	FRAGMENT("frag"),
	GEOMETRY("geo"),
	COMPUTE("comp");

	private String extension;

	ShaderType(String extension) {
		this.extension = extension;
	}

	@Override
	public String toString() {
		return this.name() + "_SHADER";
	}

	public String getExtension() {
		return this.extension;
	}
}
