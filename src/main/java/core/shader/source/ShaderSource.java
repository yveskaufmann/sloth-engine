package core.shader.source;


import core.shader.ShaderType;
import core.utils.HardwareObject;

public abstract class ShaderSource extends HardwareObject {

	private ShaderType type;

	protected ShaderSource(ShaderType type) {
		super(ShaderSource.class);
		this.type = type;
	}

	/**
	 * Retrieves the core.shader source from the
	 * underlying data source.
	 *
	 * @return the source of the core.shader.
     */
	public abstract String getSource();

	/**
	 * Returns the time that the core.shader source was last modified.
	 *
	 * @return A long value representing the time when the core.shader source was last modified.
     */
	public abstract long lastModified();

	/**
	 * Receives the type of this core.shader.
	 *
	 * @return the type of this core.shader.
     */
	public ShaderType getType() {
		return type;
	}

	/**
	 * Receives the name of this core.shader source in order
	 * to identify a core.shader source by a name.
	 *
	 * @return the name of this core.shader source.
     */
	public abstract String getName();

	@Override
	public String toString() {
		return String.format("Shader[type: %s, name: %s]", type.toString(), getName());
	}
}
