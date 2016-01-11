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
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
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

		/***
		 *  A---D    A---D      D
		 *	|  /|    |  /      /|
		 *	| / | -> | /  +   / |
		 *	|/  |    |/      /  |
		 *	B---C    B      B---C
		 *
		 *    A'--D'
		 *   /|  /|
		 *  A---D |
		 *	| / | C'
		 *	|/  |/
		 *	B---C
		 *
		 */
		FloatBuffer triangleBuffer = BufferUtils.createBuffer(new float[] {

			-0.5f,  0.5f, 0.5f, 0.0f, 0.5f, 0.0f, // Front Top Left     = A
			-0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 0.0f, // Front Bottom Left	= B
			 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.0f, // Front Bottom Right = C
			 0.5f,  0.5f, 0.5f, 0.5f, 0.0f, 0.0f  // Front Top Right 	= D

			-0.5f,  0.5f, -0.5f, 0.0f, 0.5f, 0.0f, // Back Top Left     = A'
			-0.5f, -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, // Back Bottom Left	= B'
			0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.0f, // Back Bottom Right  = C'
			0.5f,  0.5f, -0.5f, 0.5f, 0.0f, 0.0f  // Back Top Right 	= D'
		});

		final int topLeft = 0;
		final int bottomLeft = 1;
		final int bottomRight = 2;
		final int topRight = 3;

		final int topLeftB = 4;
		final int bottomLeftB = 5;
		final int bottomRightB = 6;
		final int topRightB = 7;
		ByteBuffer indexBuffer = BufferUtils.createBuffer(new byte[] {
			// Front Face
			topLeft, bottomLeft, topRight,
			topRight, bottomLeft, bottomRight,

			// Face Right
			topRight, bottomRight, bottomRightB,
			topRightB, topRight, bottomRight,


			// Face Bottom
			bottomLeftB, bottomLeft, bottomRightB,
			bottomRightB, bottomLeft, bottomLeftB
		});


		/**
		 * int subdivideX = 2;
		 int subdivideY = 2;
		 float radius = 10.0f;
		 float colX = radius / (subdivideX);
		 float colY = radius / (subdivideY);
		 int index = 0;
		 int components = 3;
		 int bufferSize = subdivideX * triangleVertices.capacity() * subdivideY;

		 FloatBuffer gridVertexBuffer = BufferUtils.createFloatBuffer(bufferSize);
		 FloatBuffer gridColorBuffer = BufferUtils.createFloatBuffer(bufferSize);

		 for (int x = 0; x < subdivideX; x++) {
		 for(int y = 0; y < subdivideY; y++) {

		 for (int j = 0; j < triangleVertices.capacity(); j+=3) {

		 gridColorBuffer.put(vertexColors.get(j));
		 gridColorBuffer.put(vertexColors.get(j + 1));
		 gridColorBuffer.put(vertexColors.get(j + 2));

		 gridVertexBuffer.put(triangleVertices.get(j ) * x * colX);
		 gridVertexBuffer.put(triangleVertices.get(j + 1) * y * colY);
		 gridVertexBuffer.put(triangleVertices.get(j + 2));


		 }

		 }
		 }

		 gridColorBuffer.clear();
		 gridVertexBuffer.clear();
		 *
		 *
		 *
		 *
		 *
		 */

		indexBuffer.clear();
		triangleBuffer.clear();

		int vboIndex = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndex);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

		int vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, triangleBuffer, GL_STATIC_READ);
		glBindBuffer(GL_ARRAY_BUFFER, 0);


		Matrix4f p = new Matrix4f();
		p.setPerspective(45.0f, (float) (window.getWidth() / window.getHeight()) ,0.01f, 100.0f);

		Matrix4f m = new Matrix4f();

		Matrix4f v = new Matrix4f();
		v.identity().lookAt(0.0f, 0.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);


		Matrix4f mvp = new Matrix4f();
		mvp.mul(p).mul(v).mul(m);

		renderer.setClearColor(Color.Gray);
		double t1 = glfwGetTime();
		RenderState state = new RenderState();
		state.setWireframe(false);

		renderer.setShader(diffuseShader);
		diffuseShader.getUniform("mvp").setValue(mvp);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);

		while ( !window.shouldClose()) {
			float time = (float)(glfwGetTime() - t1);
			m.identity().rotateY(time * 2);
			mvp.identity().mul(p).mul(v).mul(m);

			renderer.clearBuffers(true, true, false);
			renderer.applyRenderState(state);
			// diffuseShader.getUniform("time").setValue((float) time);
			diffuseShader.getUniform("mvp").setValue(mvp);
			renderer.setShader(diffuseShader);

			glBindBuffer(GL_ARRAY_BUFFER, vbo);
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * 4, 0);
			glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * 4, 3 * 4);
			glEnableVertexAttribArray(0);
			glEnableVertexAttribArray(1);

			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndex);
			glDrawElements(GL_TRIANGLES, indexBuffer.capacity(), GL_UNSIGNED_BYTE, 0);

			glDisableVertexAttribArray(0);
			glDisableVertexAttribArray(1);

			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

			window.update();
			renderer.onNewFrame();
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
