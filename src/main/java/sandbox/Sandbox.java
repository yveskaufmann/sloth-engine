package sandbox;


import math.Color;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWKeyCallback;
import renderer.RenderState;
import renderer.Renderer;
import renderer.RendererManager;
import renderer.RendererType;
import shader.*;
import shader.source.FileShaderSource;
import utils.BufferUtils;
import window.Window;
import window.WindowManager;

import java.io.File;
import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
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
		diffuseShader.addSource(new FileShaderSource(ShaderType.VERTEX, new File("assets/shaders/TransformVertexShader.vert")));
		diffuseShader.addSource(new FileShaderSource(ShaderType.FRAGMENT, new File("assets/shaders/ColorShader.frag")));

		FloatBuffer triangleVertices = BufferUtils.createBuffer(new float[] {
			-1.0f, -1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			1.0f, -1.0f, 0.0f
		});

		FloatBuffer vertexColors = BufferUtils.createBuffer(new float[] {
			1.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 0.0f, 1.0f
		});



		int vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, triangleVertices, GL_STATIC_READ);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		int vbc = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbc);
		glBufferData(GL_ARRAY_BUFFER, vertexColors, GL_STATIC_READ);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		Matrix4f p = new Matrix4f();
		p.setPerspective(45.0f, (float) (window.getWidth() / window.getHeight()) ,0.01f, 100.0f);

		Matrix4f m = new Matrix4f();
		// model.transform(new Vector4f(0.0f, 0.0f, -20.0f, 1.0f));

		Matrix4f v = new Matrix4f();
		v.lookAt(0.0f, 0.0f, -5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);


		Matrix4f mvp = new Matrix4f();
		mvp.mul(p).mul(v).mul(m);

		renderer.setClearColor(Color.Gray);
		float i = 0;
		long t1 = System.currentTimeMillis();
		RenderState state = new RenderState();
		state.setWireframe(true);
		state.setLineWidth(10.f);
		while ( !window.shouldClose()) {

			float time = (float)(System.currentTimeMillis() - t1);
			renderer.clearBuffers(true, true, true);
			renderer.applyRenderState(state);

			mvp.identity().mul(p).mul(v).mul(m);
			diffuseShader.getUniform("mvp").setValue(mvp);
			diffuseShader.getUniform("time").setValue(time);
			renderer.setShader(diffuseShader);


			glBindBuffer(GL_ARRAY_BUFFER, vbo);
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
			glEnableVertexAttribArray(0);

			glBindBuffer(GL_ARRAY_BUFFER, vbc);
			glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
			glEnableVertexAttribArray(1);


			m.identity().scale(0.2f, 0.2f, 0.2f);
			for (int j = 0; j < 100; j++) {
				m.translate(-1.0f + -(0.5f * j), 0.0f, 0.0f);
				mvp.identity().mul(p).mul(v).mul(m);
				diffuseShader.getUniform("mvp").setValue(mvp);
				renderer.setShader(diffuseShader);



				glDrawArrays(GL_TRIANGLES, 0, 3);
			}

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
