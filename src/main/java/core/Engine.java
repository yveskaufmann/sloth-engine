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


public class Engine {

	private ShaderRepository shaderRepository;
	private MeshRepository meshRepository;
	private RendererManager renderManager;
	private WindowManager windowManager;


	private Engine() {
		windowManager = Singleton.of(WindowManager.class);
		renderManager = Singleton.of(RendererManager.class);
		shaderRepository = Singleton.of(ShaderRepository.class);
		meshRepository = Singleton.of(MeshRepository.class);
	}

	public static ShaderRepository shaderRepository() {
		return get().shaderRepository;
	};

	public static Shader getShader(String name) {
		return shaderRepository().getShader(name);
	}

	public static WindowManager windowManager() {
		return get().windowManager;
	};

	public static RendererManager renderManager() {
		return get().renderManager;
	};

	public static Window getPrimaryWindow() {
		return windowManager().getLastActiveWindow();
	}

	public static Renderer getCurrentRenderer() {
		return renderManager().getRenderer();
	}

	private static Engine get() {
		return Singleton.of(Engine.class);
	}


	public static Mesh getMesh(String fileName) {
		return get().meshRepository.getMesh(fileName);
	}
}
