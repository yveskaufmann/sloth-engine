package core.renderer;


import core.EngineComponent;

public class RendererManager implements EngineComponent {



	/**
	 * Thy currently supported types of core.renderer types.
	 */
	public enum RendererType {
		Lwjgl3
	}

	private RendererType currentRenderType = RendererType.Lwjgl3;
	private RenderState currentState;
	private Renderer renderer;
	private boolean initialized = false;

	@Override
	public void initialize() {
		if (initialized) {
			throw new IllegalArgumentException("The RenderManger is already initialized");
		}
		this.currentState = new RenderState();
		initialized = true;
	}

	@Override
	public void shutdown() {
		if (!initialized) {
			throw new IllegalArgumentException("The RenderManager isn't initialized");
		}

		if (renderer != null) {
			renderer.cleanUp();
		}

		initialized = false;
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

		if (renderer == null) {
			switch (type) {
				case Lwjgl3:
					currentRenderType = type;
					renderer = new Lwjgl3Renderer();
					break;
				default:
					throw new RendererExpception("Unsupported core.renderer type specified");
			}

			renderer.applyRenderState(currentState.reset());
		}

		return renderer;
	}

	public  Renderer getRenderer() {
		return getRenderer(RendererType.Lwjgl3);
	}

	/**
	 * Get the render state of the active core.renderer.
	 *
	 * @return the state of the current active core.renderer.
     */
	public RenderState getRenderState() {
		return currentState;
	}

	public void updateViewportSize(int x, int y, int width, int height) {
		if (renderer != null) {
			renderer.setViewport(x, y, width, height);
		}
	}
}
