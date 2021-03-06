package eu.yvka.slothengine.shader.source;

import eu.yvka.slothengine.renderer.Renderer;
import eu.yvka.slothengine.shader.ShaderType;

public class StringShaderSource extends ShaderSource {

	private String source;
	private String name;
	private long modifiedTimestamp = 0;

	public StringShaderSource(String name, ShaderType type, String source) {
		super(type);
		this.source = source;
		this.name = name;
		markAsModified();
	}

	@Override
	public String getSource() {
		return this.source;
	}

	@Override
	public void updateShaderSource(String source) {
		this.source = source;
		markAsModified();
	}

	public void setSource(String source) {
		enableUpdateRequired();
		this.source = source;
	}

	@Override
	public long lastModified() {
		return modifiedTimestamp;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void deleteObject(Renderer renderer) {
		renderer.deleteShaderSource(this);
	}

	@Override
	public void resetObject() {
		enableUpdateRequired();
	}

	private void markAsModified() {
		enableUpdateRequired();
		modifiedTimestamp = System.currentTimeMillis();
	}

}
