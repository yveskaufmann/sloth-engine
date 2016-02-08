package core.engine;

import core.renderer.Renderer;
import core.renderer.RendererManager;
import core.window.Window;
import core.window.WindowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public abstract class EngineApp {

	/**
	 * Application logger
	 */
	protected static final Logger Log = LoggerFactory.getLogger(EngineApp.class);

	protected WindowManager windowManager;
	protected RendererManager rendererManager;
	protected Renderer renderer;
	protected Window window;

	protected void init() {
		Engine.start();
		windowManager = Engine.windowManager();
		rendererManager = Engine.renderManager();
		window = windowManager
			.setTitle("Window the first")
			.setSize(1024, 768)
			.setResizeable(true)
			.setGLContextVersion(3, 0)
			.build()
			.enable();
		renderer = rendererManager.getRenderer();
	}

	public void start() {
		try {
			init();
			prepare();
			appLoop();
		} catch (Exception ex) {
			Log.error("A exception occurred the application will be terminated abnormally. ", ex);
		} finally {
			cleanUp();
			Engine.shutdown();
		}
	}

	private void appLoop() throws Exception {
		double lastTime = glfwGetTime();
		while ( !window.shouldClose()) {
			double currentTime = glfwGetTime();
			float elapsedTime =  (float) (lastTime - currentTime);

			Engine.onFrameStart();
			Engine.onUpdate(elapsedTime);
			update(elapsedTime);

			Engine.onBeforeRender(elapsedTime);
			render(elapsedTime);
			Engine.onAfterRender(elapsedTime);

			renderer.onNewFrame();
			window.update();
			Engine.onFrameEnd();
			lastTime = currentTime;
		}
	}

	protected abstract void prepare();
	protected abstract void update(float elapsedTime);
	protected abstract void render(float elapsedTime) throws IOException;
	protected abstract void cleanUp();
}
