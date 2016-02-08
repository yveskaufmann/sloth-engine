package core.engine;

import core.input.InputManager;
import core.renderer.RendererManager;
import core.scene.Camera;

public interface Context {

	/**
	 * Retrieves the current render manager.
	 *
	 * @return the render manager
     */
	RendererManager getRenderManager();

	/**
	 * Retrieves the current active input manager.
	 *
	 * @return the input manager.
     */
	InputManager inputManager();

	/**
	 * Retrieves the current active camera
	 */
	Camera getCamera();








}
