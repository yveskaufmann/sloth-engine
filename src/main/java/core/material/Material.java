package core.material;

import core.renderer.RenderState;
import core.shader.Shader;
import core.texture.Texture;

import java.util.Map;

public interface Material {

	RenderState getRenderState();
	Shader getShader();
	Map<Integer, Texture> getTextures();
}
