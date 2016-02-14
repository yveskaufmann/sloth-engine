package core.engine;

import core.input.InputManager;
import core.renderer.Renderer;
import core.renderer.RendererManager;
import core.window.Window;
import core.window.WindowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

/**
 * Base class for a engine application.
 */
public abstract class EngineApp {

	/**
	 * Application logger
	 */
	protected static final Logger Log = LoggerFactory.getLogger(EngineApp.class);

	protected WindowManager windowManager;
	protected RendererManager rendererManager;
	protected InputManager inputManager;
	protected Renderer renderer;
	protected Window window;

	/**
	 * Initialize the AppEngine and all it
	 * engine components.
	 * A inherit class can override this
	 * method but must call super.init() firstly before all other
	 * init routines.
	 */
	protected void init(AppSettings settings) {
		Engine.start(settings);
		windowManager = Engine.windowManager();
		rendererManager = Engine.renderManager();
		window = windowManager.getPrimaryWindow();
		inputManager = Engine.getInputManager();
		renderer = rendererManager.getRenderer();
	}

	public void start(AppSettings settings) {
		try {
			init(settings);
			prepare();
			appLoop();
		} catch (Exception ex) {
			Log.error("A exception occurred the application will be terminated abnormally. ", ex);
		} finally {
			cleanUp();
			Engine.shutdown();
		}
	}

	/**
	 * Render loop which the main loop
	 * for the render app.
	 *
	 * @throws Exception
     */
	private void appLoop() throws Exception {
		double lastTime = glfwGetTime();
		while ( !Engine.shouldExit() ) {
			double currentTime = glfwGetTime();
			float elapsedTime =  (float) (lastTime - currentTime);
			Engine.onFrameStart();
			Engine.onUpdate(elapsedTime);
			update(elapsedTime);
			Engine.onBeforeRender(elapsedTime);
			render(elapsedTime);
			Engine.onRender(elapsedTime);
			Engine.onAfterRender(elapsedTime);
			Engine.onFrameEnd();
			lastTime = currentTime;
		}
	}

	/**
	 * Will be called before the render loop and provides the possibility to initialize required resources.
	 */
	protected abstract void prepare();

	/**
	 * Will be called once per frame and should onFrameEnd the state of the scene.
	 *
	 * @param elapsedTime the time since the last frames in ms
     */
	protected abstract void update(float elapsedTime);

	/**
	 * Will be called once per frame and render one complete frame.
	 *
	 * @param elapsedTime the time since the last frames in ms
	 * @throws IOException if an error occurs
     */
	protected abstract void render(float elapsedTime) throws IOException;

	/**
	 * Called after the render loop is leaved and provide the
	 * possibility to clean up all initializations.
	 */
	protected abstract void cleanUp();

}
