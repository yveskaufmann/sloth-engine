package core.engine;

/**
 * Interface for a engine component.
 */
public interface EngineComponent {

	/**
	 * Initialize this component must
	 * be called before the component could be used.
	 */
	void initialize();

	/**
	 * Shutdown this component and
	 * free all hold resources.
	 * Must be called before this component is removed from the heap.
	 */
	void shutdown();

	/**
	 * Retrieves if the Component was already initialized.
	 *
	 * @return true if already initialized
	 */
	boolean isInitialized();
}
