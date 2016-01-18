package renderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Singleton;

public class RendererManager {

	/**
	 * Thy currently supported types of renderer types.
	 */
	public enum RendererType {
		Lwjgl3,
		None
	}

	private static RendererType currentRenderType = RendererType.Lwjgl3;
	private static final Logger Log = LoggerFactory.getLogger(RendererManager.class);

	private RenderState currentState = new RenderState();

	private RendererManager() {
	}

	/**
	 * Returns the render instance by a specified render type.
	 *
	 * @param type the desired type of the specified render type.
	 * @return the instance of the desired render type.
	 * @throws RendererExpception if the specified render type is unsupported or unknown.
     */
	public Renderer getRenderer(RendererType type) {
		assert type != null;
		switch (type) {
			case Lwjgl3:
				currentRenderType = type;
				return Singleton.of(Lwjgl3Renderer.class);
			default:
				throw new RendererExpception("Unsupported renderer type specified");
		}
	}

	/**
	 * Get the current singleton renderer instance which has the same
	 * type as the previous call to <code>getRenderer(RendererType type)</code> or
	 * the defined fallback renderer <code>Lwjgl3Renderer</code>.
	 *
	 * @return the singleton instance of the current used renderer
     */
	public  Renderer getRenderer() {
		if (currentRenderType != null) {
			return getRenderer(currentRenderType);
		} else {
			return getRenderer(RendererType.Lwjgl3);
		}
	}

	/**
	 * Get the render state of the active renderer.
	 *
	 * @return the state of the current active renderer.
     */
	public RenderState getRenderState() {
		return currentState;
	}
}
