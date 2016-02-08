package shadersloth;

import core.engine.Engine;
import core.renderer.Renderer;
import core.renderer.RendererManager;
import core.window.Window;
import core.window.WindowManager;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Rectangle2D;
import org.lwjgl.util.stream.RenderStream;
import org.lwjgl.util.stream.StreamHandler;
import org.lwjgl.util.stream.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public abstract class SlothApplication {
	/**
	 * The logger
	 */
	private static final Logger Log = LoggerFactory.getLogger(ShaderSloth.class);
	/**
	 * Supplier for the ReadHandler which,
	 * delivers the the rendered picture to an
	 * external thread.
	 */

	protected WindowManager windowManager;
	protected RendererManager rendererManager;
	protected Renderer renderer;
	protected Window window;

	private boolean enabledOffscreen;
	private Supplier<StreamHandler> readHandlerSupplier;
	private ConcurrentLinkedQueue<Runnable> pendingRunnables;
	private StreamUtil.RenderStreamFactory renderStreamFactory;
	private RenderStream renderStream;
	private CountDownLatch runningLatch;

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

		if (enabledOffscreen) {
			initOffRendering(window);
			window.hide();
		}
		renderer = rendererManager.getRenderer();


	}

	protected void run() {
		try {
			init();
			prepare();
			renderLoop();
		} catch (Exception ex) {
			Log.error("A exception occurred the application will be terminated abnormally. ", ex);
		} finally {
			cleanUp();
			Engine.shutdown();
		}
	}

	private void renderLoop() throws Exception {
		double lastTime = glfwGetTime();
		while ( !window.shouldClose()) {
			double currentTime = glfwGetTime();
			float elapsedTime =  (float) (lastTime - currentTime);
			update(elapsedTime);
			if ( enabledOffscreen ) {
				renderStream.bind();
			}
			render(elapsedTime);
			renderer.onNewFrame();
			window.update();
			lastTime = currentTime;

			if ( enabledOffscreen ) {
				try {
					renderStream.swapBuffers();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				drainPendingActionsQueue();
				if (runningLatch.getCount() <= 0) {
					window.requestClose();
				}
			}

		}
	}

	/**
	 * This method must be started from a external thread in order
	 * to activate the core.renderer with offscreen support.
	 *  @param runningLatch
	 * 				- the latch is used to determine if the external thread is running alive.
	 * @param readHandlerSupplier
	 * @param viewPortProperty
	 */
	public void start(CountDownLatch runningLatch, Supplier<StreamHandler> readHandlerSupplier, ObjectProperty<Rectangle2D> viewPortProperty) {
		viewPortProperty.addListener((observable, oldValue, newValue) -> {
			window.setWidth((int) newValue.getWidth());
			window.setHeight((int) newValue.getHeight());
			// TODO: Don't trigger the onUpdate Window size from a external thread
			window.updateViewportSize();
		});
		// TODO need implementation which works on lower the ope GL 3.0.
		runWithOffScreen(runningLatch, readHandlerSupplier);

	}

	protected void initOffRendering(Window window) {
		pendingRunnables = new ConcurrentLinkedQueue<>();
		this.renderStreamFactory = StreamUtil.getRenderStreamImplementation();
		this.renderStream = renderStreamFactory.create(readHandlerSupplier.get(), 1, 2);
	}

	private void drainPendingActionsQueue() {
		Runnable runnable;

		while ( (runnable = pendingRunnables.poll()) != null )
			runnable.run();
	}

	private void runWithOffScreen(CountDownLatch runningLatch, Supplier<StreamHandler> readHandlerSupplier) {
		enabledOffscreen = true;
		this.readHandlerSupplier = readHandlerSupplier;
		this.runningLatch = runningLatch;
		run();
	}

	protected abstract void prepare();
	protected abstract void update(float elapsedTime);
	protected abstract void render(float elapsedTime) throws IOException;
	protected abstract void cleanUp();
}
