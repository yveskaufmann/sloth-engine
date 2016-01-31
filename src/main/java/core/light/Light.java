package core.light;

import static core.shader.ShaderVariable.VAR_PREFIX;
import core.math.Color;
import core.shader.Shader;
import core.shader.Uniform;

/**
 * Base type of light entity.
 */
public abstract class Light {

	public static final String LIGHT_UNIFORM_ARRAY =  VAR_PREFIX +"lights";
	public static final String LIGHT_UNIFORM_TYPE = "type";
	public static final String LIGHT_UNIFORM_COLOR = "color";
	public static final String LIGHT_UNIFORM_POSITION = "position";
	public static final String LIGHT_UNIFORM_ATTENUATION = "attenuation";

	private Color color;
	private float attenuation;
	private LightType type;

	public Light(LightType type) {
		this.attenuation = 1.0f;
		this.color = Color.White;
		this.type = type;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public void setAttenuation(float attenuation) {
		this.attenuation = attenuation;
	}

	public float getAttenuation() {
		return attenuation;
	}

	public LightType getType() {
		return type;
	}

	protected void passToShader(int lightId, Shader shader) {
		getLightUniform(lightId, shader, LIGHT_UNIFORM_COLOR).setValue(color);
		getLightUniform(lightId, shader, LIGHT_UNIFORM_ATTENUATION).setValue(attenuation);
		getLightUniform(lightId, shader, LIGHT_UNIFORM_TYPE).setValue(type.ordinal());
	}

	protected Uniform getLightUniform(int lightId, Shader shader, String name) {
		return shader.getUniform(LIGHT_UNIFORM_ARRAY + "[" + lightId + "]." + name);
	}
}
