package core;

import core.geometry.Mesh;
import core.geometry.MeshRepository;
import core.renderer.Renderer;
import core.renderer.RendererManager;
import core.shader.Shader;
import core.shader.ShaderRepository;
import core.utils.Singleton;
import core.window.Window;
import core.window.WindowManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import java.io.IOException;
import java.util.logging.LogManager;

import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;
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
	private static boolean initialized;

	private Engine() {}

	static
	public  ShaderRepository shaderRepository() {
		return shaderRepository;
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
		return windowManager.getLastActiveWindow();
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
	public void start() {

		if (initialized) {
			throw new IllegalArgumentException("The Engine is already initialized");
		}

		if ( GL_TRUE != glfwInit()) {
			throw new IllegalStateException("GLFW could not initialized");
		}


		windowManager = new WindowManager();
		renderManager = new RendererManager();
		meshRepository = new MeshRepository();
		shaderRepository = new ShaderRepository();

		windowManager.initialize();
		renderManager.initialize();
		shaderRepository.initialize();
		initialized = true;

	}

	public static void shutdown() {
		if (!initialized) {
			throw new IllegalArgumentException("The Engine isn't initialized");
		}

		shaderRepository.shutdown();
		renderManager.shutdown();
		windowManager.shutdown();

		GLFW.glfwTerminate();
		initialized = false;
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
