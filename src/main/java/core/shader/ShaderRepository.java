package core.shader;

import core.shader.source.FileShaderSource;
import core.shader.source.ShaderSource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ShaderRepository {

	private final String ASSET_PATH = "assets/shaders/";

	private Map<String, Shader> shaderMap;
	private ShaderRepository() {
		shaderMap = new HashMap<>();
	}

	public Shader getShader(String shader) {
		return getShader(shader, shader);
	}

	public Shader getShader(String vertexShader, String fragmentShader) {
		String id = vertexShader + "_" + fragmentShader;
		Shader shader = shaderMap.get(id);

		if (shader == null) {
			shader = new Shader(id);
			shader.addSource(getSource(ShaderType.VERTEX, vertexShader));
			shader.addSource(getSource(ShaderType.FRAGMENT, fragmentShader));

			shaderMap.put(id, shader);
		}

		return shader;
	}

	public ShaderSource getSource(ShaderType type, String name) {
		FileShaderSource fileShaderSource = new FileShaderSource(type,
			new File(ASSET_PATH + name + "." + type.getExtension()));

		return fileShaderSource;
	}


}
