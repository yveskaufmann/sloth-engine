package sandbox;

import renderer.Renderer;
import renderer.RendererManager;
import shader.Shader;
import shader.ShaderRepository;
import utils.Singleton;
import window.Window;
import window.WindowManager;


public class EngineContext {

	private ShaderRepository shaderRepository;
	private RendererManager renderManager;
	private WindowManager windowManager;

	private EngineContext() {
		shaderRepository = Singleton.of(ShaderRepository.class);
		renderManager = Singleton.of(RendererManager.class);
		windowManager = Singleton.of(WindowManager.class);
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

	private static EngineContext get() {
		return Singleton.of(EngineContext.class);
	}


}
