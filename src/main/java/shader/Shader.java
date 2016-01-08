package shader;

import renderer.Renderer;
import shader.source.ShaderSource;
import utils.HardwareObject;

import java.util.*;

public class Shader extends HardwareObject {

	private List<ShaderSource> shaderSources = null;
	private Map<String, Uniform> uniforms = null;
	private Map<Integer, Attribute> attributes = null;


	public Shader() {
		super(Shader.class);
		shaderSources = new ArrayList<>();
		uniforms = new HashMap<>();
		attributes = new HashMap<>();
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

	public Iterator<Uniform> getUniformIterator() {
		return uniforms.values().iterator();
	}

	public Uniform removeUniform(String name) {
		return uniforms.remove(name);
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
		shaderSources.forEach(source -> shaderSources.remove(source));
		enableUpdateRequired();
	}


}
