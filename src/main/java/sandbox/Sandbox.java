package sandbox;


import math.Color;
import org.lwjgl.glfw.GLFWKeyCallback;
import renderer.Renderer;
import renderer.RendererManager;
import renderer.RendererType;
import shader.*;
import shader.source.FileShaderSource;
import window.Window;
import window.WindowManager;

import java.io.File;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Sandbox {

	public void run() throws Exception {

		GLFWKeyCallback keyCallback;

		Window window = WindowManager.get()
			.setTitle("Window the first")
			.setSize(1024, 768)
			.setResizeable(true)
			.setGLContextVersion(3, 0)
			.build()
			.enable();

		glfwSetKeyCallback(window.getWindowId(), keyCallback = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (key == GLFW_KEY_ESCAPE) {
					glfwSetWindowShouldClose(window, GL_TRUE);
				}
			}
		});

		Renderer renderer = RendererManager.getRenderer(RendererType.Lwjgl3);
		Shader diffuseShader = new Shader();
		diffuseShader.addSource(new FileShaderSource(ShaderType.VERTEX, new File("assets/shaders/StandardShading.vert")));
		diffuseShader.addSource(new FileShaderSource(ShaderType.FRAGMENT, new File("assets/shaders/StandardShading.frag")));


		renderer.setClearColor(Color.LightGrey);
		renderer.clearBuffers(true, false, false);
		while ( !window.shouldClose()) {
			renderer.setShader(diffuseShader);
			renderer.setClearColor(Color.Blue);
			renderer.setViewport(50, 50, 100, 100);
			renderer.onNewFrame();
			renderer.clearBuffers(true, true, false);

			window.update();
		}

		renderer.cleanUp();
		keyCallback.release();
		WindowManager.get().clean();
	}

	public static void main(String[] args) throws Exception {
		Sandbox box = new Sandbox();
		box.run();
	}
}
