package core.shader;

import core.geometry.VertexBuffer;
import core.renderer.Renderer;
import core.shader.source.ShaderSource;
import core.utils.BufferUtils;
import core.utils.HardwareObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shader extends HardwareObject {

	private List<ShaderSource> shaderSources = null;
	private Map<String, Uniform> uniforms = null;
	private Map<VertexBuffer.Type, Attribute> attributes = null;
	private String shaderName = null;

	public Shader(String name) {
		super(Shader.class);
		shaderSources = new ArrayList<>();
		uniforms = new HashMap<>();
		attributes = new HashMap<>();
		this.shaderName = name;
	}

	public void addSource(ShaderSource source) {
		shaderSources.add(source);
		enableUpdateRequired();
	}

	public void removeShader(ShaderSource source) {
		shaderSources.remove(source);
		enableUpdateRequired();
	}

	public Iterable<ShaderSource> getShaderSources() {
		return shaderSources;
	}

	public Uniform getUniform(String name) {
		Uniform uniform = uniforms.get(name);

		if (uniform == null) {
			uniform = new Uniform();
			uniform.setName(name);
			uniforms.put(name, uniform);
		}

		return uniform;
	}

	public Iterable<Uniform> getUniforms() {
		return uniforms.values();
	}

	public Iterable<Attribute> getAttributes() {
		return attributes.values();
	}

	public ShaderVariable removeUniform(String name) {
		return uniforms.remove(name);
	}

	public Attribute getAttribute(VertexBuffer.Type type) {
		Attribute attribute = attributes.get(type);
		if (attribute == null) {
			attribute = new Attribute();
			attributes.put(type, attribute);
		}
		return attribute;
	}

	public Attribute removeAttribute(VertexBuffer.Type type) {
		return attributes.remove(type);
	}

	public String getShaderName() {
		return this.shaderName;
	}

	@Override
	public boolean isUpdateRequired() {
		return shaderSources.stream().anyMatch(HardwareObject::isUpdateRequired);
	}

	@Override
	public void deleteObject(Renderer renderer) {
		renderer.deleteShader(this);
	}

	@Override
	public void resetObject() {
		shaderSources.forEach(ShaderSource::resetObject);
		enableUpdateRequired();
	}

	@Override
	public String toString() {
		return String.format("Shader-" + shaderName);
	}
}

