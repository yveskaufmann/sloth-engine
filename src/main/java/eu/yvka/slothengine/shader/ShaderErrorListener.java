package eu.yvka.slothengine.shader;

import eu.yvka.slothengine.shader.source.ShaderSource;

/**
 * @author Yves Kaufmann
 * @since 18.07.2016
 */
public interface ShaderErrorListener {
	public void onCompileError(Shader shader, ShaderSource source, String error);
	public void onLinkError(Shader shader, String error);
	public void onResolved(Shader shader, ShaderSource source);
}
