package core.engine;

import core.geometry.Mesh;
import core.geometry.MeshRepository;
import core.input.InputManager;
import core.input.InputManagerFactory;
import core.renderer.Renderer;
import core.renderer.RendererManager;
import core.shader.Shader;
import core.shader.ShaderRepository;
import core.texture.TextureManager;
import core.window.Window;
import core.window.WindowManager;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.opengl.GL11.GL_TRUE;


public class Engine {

	static {
		enableLogging();
	}

	private static ShaderRepository shaderRepository;
	private static MeshRepository meshRepository;
	private static RendererManager renderManager;
	private static WindowManager windowManager;
	private static TextureManager textureManager;
	private static InputManager inputManager;
	private static boolean initialized;
	private static List<EngineComponent> components = new ArrayList<>();
	private static AppSettings appSettings = new AppSettings();

	private Engine() {}

	/**
	 * Starts the engine and all its components.
	 * @param settings
	 */
	static
	public void start(AppSettings settings) {
		appSettings = settings;
		if (initialized) {
			throw new IllegalArgumentException("The Engine is already initialized");
		}

		if ( GL_TRUE != glfwInit()) {
			throw new IllegalStateException("GLFW could not initialized");
		}

		registerDefaultComponents();

		initialized = true;
	}

	/**
	 * Register the default components that the engine provides.
	 */
	private static void registerDefaultComponents() {
		windowManager = register(new WindowManager(), WindowManager.class);
		textureManager = register(new TextureManager(), TextureManager.class);
		renderManager = register(new RendererManager(), RendererManager.class);
		shaderRepository = register(new ShaderRepository(), ShaderRepository.class);
		meshRepository = register(new MeshRepository(), MeshRepository.class);
		onInit();

		JavaFXOffscreenSupport offscreenSupport = appSettings.get(JavaFXOffscreenSupport.JAVAFX_OFFSCREEN_SUPPORT, JavaFXOffscreenSupport.class);
		if (offscreenSupport != null) {
			inputManager = register(InputManagerFactory.createInputManager(offscreenSupport.getNode()), InputManager.class);
		} else {
			inputManager = register(InputManagerFactory.createInputManager(Engine.getPrimaryWindow()), InputManager.class);
		}
		inputManager.initialize();

		if (offscreenSupport != null) {
			register(offscreenSupport);
		}

	}

	/**
	 * Register and initialize a engine component.
	 *
	 * @param component the component add
     */
	public static void register(EngineComponent component) {
		register(component, component.getClass());
		if (!component.isInitialized()) {
			component.initialize();
		}
	}

	private static <T extends EngineComponent> T register(EngineComponent component, Class<T> type) {
		if (! components.contains(component)) {
			components.add(component);
		}
		return type.cast(component);
	}

	/**
	 * Unregister and shutdown a engine component.
	 *
	 * @param component the component to remove
	 */
	public static void unregister(EngineComponent component) {
		// TODO: ensure core components are not removed
		if (components.contains(component) && component.isInitialized()) {
			components.remove(component);
			if (component.isInitialized()) {
				component.shutdown();
			}
		}
	}

	/**
	 * Shutdown the engine and all it's components
	 */
	static
	public void shutdown() {
		if (! initialized) {
			throw new IllegalArgumentException("The Engine isn't initialized");
		}
		onShutdown();
		components.clear();
		GLFW.glfwTerminate();
		initialized = false;
	}

	static
	public  ShaderRepository shaderRepository() {
		return shaderRepository;
	}

	static
	public AppSettings getSettings() {
		return appSettings;
	}

	static
	public Shader getShader(String name) {
		return shaderRepository.getShader(name);
	}

	static
	public  WindowManager windowManager() {
		return windowManager;
	}

	static
	public RendererManager renderManager() {
		return renderManager;
	}

	static
	public Window getPrimaryWindow() {
		return windowManager.getPrimaryWindow();
	}

	static
	public  Renderer getCurrentRenderer() {
		return renderManager.getRenderer();
	}

	static
	public Mesh getMesh(String fileName) {
		return meshRepository.getMesh(fileName);
	}

	static
	public TextureManager textureManager() {
		return textureManager;
	}

	static void onInit() {
		components.stream().filter((c) -> !c.isInitialized()).forEach(EngineComponent::initialize);
	}

	static void onFrameStart() {
		components.stream().filter(EngineComponent::isInitialized).forEach(EngineComponent::onFrameStart);
	}

	static void onUpdate(float elapsedTime) {
		components.stream().filter(EngineComponent::isInitialized).forEach((c) -> c.onUpdate(elapsedTime));
	}

	static void onBeforeRender(float elapsedTime) {
		components.stream().filter(EngineComponent::isInitialized).forEach((c) -> c.onBeforeRender(elapsedTime));
	}

	static void onRender(float elapsedTime) {
		components.stream().filter(EngineComponent::isInitialized).forEach((c) -> c.onRender(elapsedTime));
	}

	static void onAfterRender(float elapsedTime) {
		components.stream().filter(EngineComponent::isInitialized).forEach((c) -> c.onAfterRender(elapsedTime));
	}

	static void onFrameEnd() {
		components.stream().filter(EngineComponent::isInitialized).forEach(EngineComponent::onFrameEnd);
	}

	static void onShutdown() {
		components.stream().filter(EngineComponent::isInitialized).forEach(EngineComponent::shutdown);
	}

	public static boolean shouldExit() {
		return windowManager.getPrimaryWindow().shouldClose();
	}

	public static void requestExit() {
		windowManager.getPrimaryWindow().requestClose();
	}

	public static InputManager getInputManager() {
		return inputManager;
	}

	private static void enableLogging() {
		System.setProperty( "java.util.logging.config.file", "logging.properties" );
		try {
			LogManager.getLogManager().readConfiguration();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
