package sandbox;

import geometry.Mesh;
import geometry.VertexBuffer;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Rectangle2D;
import math.Color;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.util.stream.RenderStream;
import org.lwjgl.util.stream.StreamHandler;
import org.lwjgl.util.stream.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import renderer.RenderState;
import renderer.Renderer;
import renderer.RendererManager;
import renderer.font.FontRenderer;
import scene.Node;
import scene.TargetCamera;
import shader.Shader;
import shader.ShaderRepository;
import window.Window;
import window.WindowManager;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;
import java.util.logging.LogManager;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static renderer.RenderState.BlendFunc;
import static renderer.RenderState.CullFaceMode;

public class Sandbox {

	/**
	 * Initialize the logging of this application.
	 */
	static {
		System.setProperty( "java.util.logging.config.file", "logging.properties" );
		try {
			LogManager.getLogManager().readConfiguration();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The logger for logger class
	 */
	private static final Logger Log = LoggerFactory.getLogger(Sandbox.class);

	/**
	 * Supplier for the ReadHandler which,
	 * delivers the the rendered picture to an
	 * external thread.
	 */
	private boolean enabledOffscreen = false;
	private Supplier<StreamHandler> readHandlerSupplier;
	private ConcurrentLinkedQueue<Runnable> pendingRunnables;
	private StreamUtil.RenderStreamFactory renderStreamFactory;
	private RenderStream renderStream;
	private CountDownLatch runningLatch;

	private WindowManager windowManager;
	private RendererManager rendererManager;
	private ShaderRepository shaderRepository;
	private Renderer renderer;
	private Window window;
	private GLFWKeyCallback keyCallback;

	private Node sceneRoot;

	private Matrix4f modelMatrix = new Matrix4f();
	private Matrix4f viewMatrix = new Matrix4f();
	private Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f modelViewProjMatrix = new Matrix4f();
	private FontRenderer fontRenderer;
	private TargetCamera camera;

	private Mesh cube;
	private Vector3f cubePos;
	float zoomLevel;

	public Sandbox() {
	}

	private void init() {

		windowManager = EngineContext.windowManager();
		rendererManager = EngineContext.renderManager();
		shaderRepository = EngineContext.shaderRepository();
		camera = new TargetCamera();

		window = EngineContext.windowManager()
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
		rendererManager.getRenderState().apply();
	}



	private void prepare() {
		Shader diffuseShader = EngineContext.getShader("Default");
		diffuseShader.getAttribute(VertexBuffer.Type.Vertex).setName("position");
		diffuseShader.getAttribute(VertexBuffer.Type.Normal).setName("normal");
		diffuseShader.getAttribute(VertexBuffer.Type.TextCoords).setName("uv");
		diffuseShader.getAttribute(VertexBuffer.Type.Color).setName("color");

		cube = EngineContext.getMesh("cube.obj");
		// cube = new Cube();
		cubePos = new Vector3f(0f, 0f, 0f);
		renderer.setClearColor(Color.LightGrey);
		rendererManager.getRenderState().enableFPSCounter().setCullFaceMode(CullFaceMode.Off).enableWireframe(true).apply();
		zoomLevel = 1.0f;
	}

	public void update(float elapsedTime) {
		modelMatrix.identity().translate(cubePos);
		camera.setPosition(0f, 5f, 10f);
		camera.setTarget(cubePos);
		camera.zoom(zoomLevel);
		camera.update(elapsedTime);

		viewMatrix.set(camera.getViewMatrix());
		projectionMatrix.identity().setPerspective(45.0f, (float) (window.getWidth() / window.getHeight()) ,0.01f, 100.0f);
		modelViewProjMatrix.identity().mul(projectionMatrix).mul(viewMatrix).mul(modelMatrix);
	}

	private void initOffRendering(Window window) {
		pendingRunnables = new ConcurrentLinkedQueue<>();
		this.renderStreamFactory = StreamUtil.getRenderStreamImplementation();
		this.renderStream = renderStreamFactory.create(readHandlerSupplier.get(), 1, 2);
	}

	private void drainPendingActionsQueue() {
		Runnable runnable;

		while ( (runnable = pendingRunnables.poll()) != null )
			runnable.run();
	}

	public void render(float elapsedTime) throws IOException {

		renderer.clearBuffers(true, true, false);
		Shader diffuseShader = EngineContext.getShader("Default");
		diffuseShader.getUniform("mvp").setValue(modelViewProjMatrix);

		renderer.setShader(diffuseShader);
		renderer.drawMesh(cube);

	}

	private void renderWithWireframe(Shader diffuseShader) throws IOException {
		// Render the thick wireframe version.
		rendererManager.getRenderState()
			.setBlendMode(BlendFunc.Color)
			.enableWireframe(true)
			.enableSmoothLines()
			.setLineWidth(10.0f)
			.apply();


		diffuseShader.getUniform("isWireframe").setValue(1);
		renderer.setShader(diffuseShader);
		renderer.drawMesh(cube);
		rendererManager.getRenderState()
			.setBlendMode(BlendFunc.Off)
			.enableWireframe(false)
			.enableSmoothLines()
			.setLineWidth(10.0f)
			.apply();
		diffuseShader.getUniform("isWireframe").setValue(0);
	}

	private void runWithOffScreen(CountDownLatch runningLatch, Supplier<StreamHandler> readHandlerSupplier) {
		enabledOffscreen = true;
		this.readHandlerSupplier = readHandlerSupplier;
		this.runningLatch = runningLatch;
		run();
	}

	public void run() {
		try {
			init();
			addInputHandler();
			prepare();
			renderLoop();

		} catch (Exception ex) {
			Log.error("A exception occurred the application will be terminated abnormally. ", ex);
		} finally {
			cleanUp();
		}
	}

	private void cleanUp() {
		rendererManager.getRenderer().cleanUp();
		keyCallback.release();
		windowManager.clean();
	}

	private void renderLoop() throws Exception {
		double lastTime = glfwGetTime();
		while ( !window.shouldClose()) {
			double currentTime = glfwGetTime();
			float elapsedTime =  (float) (lastTime - currentTime);
			handleInputs();
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

	private void handleInputs() {

	}

	private void addInputHandler() {
		glfwSetKeyCallback(window.getWindowId(), keyCallback = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {

				RenderState state = rendererManager.getRenderState();

				if (key == GLFW_KEY_ESCAPE) {
					glfwSetWindowShouldClose(window, GL_TRUE);
				}

				if (key == GLFW_KEY_C && action == GLFW_PRESS ) {
					if (state.getCullFaceMode() == CullFaceMode.Off) {
						state.setCullFaceMode(CullFaceMode.Back);
					} else {
						state.setCullFaceMode(CullFaceMode.Off);
					}
					state.apply();
				}


				if (key == GLFW_KEY_W && action == GLFW_PRESS ) {
					state.enableWireframe(!state.isWireframe());
					state.apply();
				}

				if (key == GLFW_KEY_UP && action == GLFW_REPEAT) {
					cubePos.add(0.0f, 0.2f, 0.0f);
				}

				if (key == GLFW_KEY_DOWN && action == GLFW_REPEAT) {
					cubePos.add(0.0f, -0.2f, 0.0f);
				}

				if (key == GLFW_KEY_LEFT && action == GLFW_REPEAT) {
					cubePos.add(-0.2f, 0.0f, 0.0f);
				}

				if (key == GLFW_KEY_RIGHT && action == GLFW_REPEAT) {
					cubePos.add(0.2f, 0.0f, 0.0f);
				}


				if (key == GLFW_KEY_Q && action == GLFW_REPEAT) {

					zoomLevel -= 0.1;
					System.out.println("Q pressed " + zoomLevel);
				}

				if (key == GLFW_KEY_A && action == GLFW_REPEAT) {
					zoomLevel += 0.1;
					System.out.println("A Pressed " + zoomLevel);
				}

			}
		});
	}


	/**
	 * This method must be started from a external thread in order
	 * to activate the renderer with offscreen support.
	 *  @param runningLatch
	 * 				- the latch is used to determine if the external thread is running alive.
	 * @param readHandlerSupplier
	 * @param viewPortProperty
	 */
	public void start(CountDownLatch runningLatch, Supplier<StreamHandler> readHandlerSupplier, ObjectProperty<Rectangle2D> viewPortProperty) {
		viewPortProperty.addListener((observable, oldValue, newValue) -> {
			window.setWidth((int) newValue.getWidth());
			window.setHeight((int) newValue.getHeight());
			// Don't trigger the update Window size from a external thread
			// It's really silly =)
			window.updateViewportSize();
			projectionMatrix.identity().setPerspective(45.0f, window.getAspectRatio() ,0.01f, 100.0f);
		});
		runWithOffScreen(runningLatch, readHandlerSupplier);

	}

	/**
	 * Run the renderer without offscreen support, exists only for
	 * development and debugging purposes.
	 *
	 * @param args
	 * @throws Exception
     */
	public static void main(String[] args) throws Exception {
		Sandbox box = new Sandbox();
		box.run();
	}


}
