package core.renderer;


import core.engine.EngineComponent;

public class RendererManager implements EngineComponent {

	private RenderState currentState;
	private Renderer renderer;
	private boolean initialized = false;

	public Renderer getRenderer() {
		return renderer;
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

	@Override
	public void initialize() {
		if (initialized) {
			throw new IllegalArgumentException("The RenderManger is already initialized");
		}

		currentState = new RenderState();
		if (renderer == null) {
			renderer = new Lwjgl3Renderer();
			renderer.applyRenderState(currentState);
		}

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

	@Override
	public boolean isInitialized() {
		return initialized;
	}

	@Override
	public void onFrameStart() {
		if (renderer != null) {
			renderer.onNewFrame();
		}
	}
}
