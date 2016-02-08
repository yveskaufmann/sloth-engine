package core.engine;

/**
 * Interface for a component which requires an updater per frame.
 */
public interface UpdateRequiredComponent extends EngineComponent {

	/**
	 * Method which will be called by the engine at every frame.
	 *
	 * @param time the time since the last frame.
     */
	void update(float time);
}
