package eu.yvka.slothengine.shader;

/**
 * @author Yves Kaufmann
 * @since 18.07.2016
 */
public interface ShaderErrorListener {
	public void onCompileError(Shader shader, String error);
	public void onLinkError(Shader shader, String error);
}
