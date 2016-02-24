package core.scene.light;

import core.shader.Shader;

import java.util.ArrayList;

import static core.shader.ShaderVariable.VAR_PREFIX;

/**
 * Collection of Lights which an passed to a Shader.
 */
public class LightList extends ArrayList<Light> {

	public static final String LIGHTS_UNIFORM_COUNT = VAR_PREFIX + "light_count";

	public void passToShader(Shader shader) {
		shader.getUniform(LIGHTS_UNIFORM_COUNT).setValue(size());
		for (int i = 0; i < size(); i++) {
			this.get(i).passToShader(i, shader);
		}
	}
}
