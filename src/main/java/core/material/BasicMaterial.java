package core.material;

import core.engine.Engine;
import core.renderer.RenderState;
import core.shader.Shader;
import core.texture.Texture;

import java.util.Collections;
import java.util.Map;

public class BasicMaterial implements Material {

	final RenderState state = new RenderState();
	final Shader shader;

	public BasicMaterial() {
		shader = Engine.getShader("Default");
	}

	@Override
	public RenderState getRenderState() {
		return state;
	}

	@Override
	public Shader getShader() {
		return shader;
	}

	@Override
	public Map<Integer, Texture> getTextures() {
		return Collections.emptyMap();
	}
}
