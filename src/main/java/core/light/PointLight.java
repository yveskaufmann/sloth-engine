package core.light;


import core.shader.Shader;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Entity Point Light
 */
public class PointLight extends Light{

	private Vector4f position;

	public PointLight() {
		super(LightType.Point);
		position = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
	}

	public Vector3f getPosition() {
		return new Vector3f(position.x, position.y, position.y);
	}

	public void setPosition(Vector3f position) {
		this.position = this.position.set(position, 1.0f);
	}

	@Override
	protected void passToShader(int lightId, Shader shader) {
		super.passToShader(lightId, shader);
		getLightUniform(lightId, shader, LIGHT_UNIFORM_POSITION).setValue(position);
	}
}
