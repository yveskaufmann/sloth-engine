package sandbox;

import com.sun.media.sound.ModelSource;
import geometry.Mesh;
import geometry.VertexBuffer;
import javafx.scene.shape.TriangleMesh;
import math.Color;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFWKeyCallback;
import renderer.*;
import shader.Shader;
import shader.ShaderType;
import shader.source.FileShaderSource;
import utils.BufferUtils;
import window.Window;
import window.WindowManager;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;

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



		Renderer renderer = RendererManager.getRenderer(RendererType.Lwjgl3);

		Shader diffuseShader = new Shader();
		diffuseShader.addSource(new FileShaderSource(ShaderType.VERTEX, new File("assets/shaders/TransformVertexShader.vert")));
		diffuseShader.addSource(new FileShaderSource(ShaderType.FRAGMENT, new File("assets/shaders/ColorShader.frag")));
		diffuseShader.getAttribute(VertexBuffer.Type.Vertex).setName("vertexPosition");
		diffuseShader.getAttribute(VertexBuffer.Type.Color).setName("vertexColor");



		Mesh cube = createCubeMesh();



		Matrix4f v = new Matrix4f()
			.lookAt(0.0f, 5.0f, 10.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
		Matrix4f m = new Matrix4f();
		Matrix4f p = new Matrix4f()
			.setPerspective(45.0f, (float) (window.getWidth() / window.getHeight()) ,0.01f, 100.0f);
		Matrix4f mvp =new Matrix4f().mul(p).mul(v).mul(m);


		RenderState state = new RenderState();
		state.setWireframe(false);
		state.setFpsCounterEnabled(true);
		state.setCullFaceMode(RenderContext.CullFaceMode.Off);
		renderer.setClearColor(Color.LightGrey);
		renderer.applyRenderState(state);

		Vector3d cameraPos = new Vector3d(0f, 0f, 0f);

		glfwSetKeyCallback(window.getWindowId(), keyCallback = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (key == GLFW_KEY_ESCAPE) {
					glfwSetWindowShouldClose(window, GL_TRUE);
				}

				if (key == GLFW_KEY_C && action == GLFW_PRESS ) {
					if (state.getCullFaceMode() == RenderContext.CullFaceMode.Off) {
						state.setCullFaceMode(RenderContext.CullFaceMode.Back);
					} else {
						state.setCullFaceMode(RenderContext.CullFaceMode.Off);
					}
					renderer.applyRenderState(state);
				}


				if (key == GLFW_KEY_W && action == GLFW_PRESS ) {
					state.setWireframe(!state.isWireframe());
					renderer.applyRenderState(state);
				}

				if (key == GLFW_KEY_UP && action == GLFW_REPEAT) {
					cameraPos.add(0.0f, 0.2f, 0.0f);
				}

				if (key == GLFW_KEY_DOWN && action == GLFW_REPEAT) {
					cameraPos.add(0.0f, -0.2f, 0.0f);
				}

				if (key == GLFW_KEY_LEFT && action == GLFW_REPEAT) {
					cameraPos.add(-0.2f, 0.0f, 0.0f);
				}

				if (key == GLFW_KEY_RIGHT && action == GLFW_REPEAT) {
					cameraPos.add(0.2f, 0.0f, 0.0f);
				}

			}
		});

		double lastTime = glfwGetTime();
		while ( !window.shouldClose()) {
			renderer.onNewFrame();
			double currentTime = glfwGetTime();
			float elapsedTime = (float) (currentTime - lastTime);
			lastTime = currentTime;

			v.identity().lookAt((float) cameraPos.x, (float) cameraPos.y, 10.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
			mvp.identity().mul(p).mul(v).mul(m);


			renderer.clearBuffers(true, true, false);

			diffuseShader.getUniform("mvp").setValue(mvp);
			diffuseShader.getUniform("time").setValue(elapsedTime);

			renderer.setShader(diffuseShader);
			renderer.drawMesh(cube, 0, 0);

			window.update();
		}

		renderer.cleanUp();
		keyCallback.release();
		WindowManager.get().clean();
	}

	public void createPlane() {
		Mesh cube = new Mesh();
		cube.setBuffer(VertexBuffer.Type.Vertex, 3, new float[] {
			 -1.0f, 1.0f, 0.0f,
			-1.0f, -1.0f, 0.0f,
			1.0f, -1.0f, 0.0f,

			1.0f, -1.0f, 0.0f,
			1.0f, 1.0f, 0.0f,
			-1.0f, 1.0f, 0.0f
		});

		cube.setBuffer(VertexBuffer.Type.Color, 3, new float[] {
			1.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 1.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
		});
	}

	public Mesh createCubeMesh() {
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
		FloatBuffer interleavedBuffer = BufferUtils.createBuffer(new float[] {

			-1.0f,  1.0f, 1.0f, 1.0f, 0.0f, 0.0f, // Front Top Left     = A
			-1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, // Front Bottom Left	= B
			 1.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, // Front Bottom Right = C
			 1.0f,  1.0f, 1.0f, 1.0f, 1.0f, 0.0f,  // Front Top Right 	= D

			-1.0f,  1.0f, -1.0f, 1.0f, 0.0f, 0.0f, // Back Top Left     = A'
			-1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, // Back Bottom Left	= B'
			1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 1.0f, // Back Bottom Right  = C'
			1.0f,  1.0f, -1.0f, 1.0f, 1.0f, 0.0f  // Back Top Right 	= D'
		});

		final int topLeft = 0;
		final int bottomLeft = 1;
		final int bottomRight = 2;
		final int topRight = 3;
		final int topLeftBack = 4;
		final int bottomLeftBack = 5;
		final int bottomRightBack = 6;
		final int topRightBack = 7;

		IntBuffer indicesBuffer = BufferUtils.createBuffer(new int[] {
			/**
			 * Front Face
			*/
			topRight, topLeft, bottomLeft,
			bottomLeft, bottomRight, topRight,

			/**
			 * Back Face
			*/
			bottomLeftBack, topLeftBack, topRightBack,
			topRightBack, bottomRightBack, bottomLeftBack,

			/**
			 * Top Face
			*/
			topRightBack, topLeftBack, topLeft,
			topLeft, topRight, topRightBack,

			/**
			 * Bottom Face
			*/

			bottomLeft, bottomLeftBack, bottomRightBack,
			bottomRightBack, bottomRight, bottomLeft,

			/**
			 * Left Face
			*/
			bottomLeft, topLeft, topLeftBack,
			topLeftBack, bottomLeftBack, bottomLeft,

			/**
			 * Right Face
			*/
			topRightBack, topRight, bottomRight,
			bottomRight, bottomRightBack, topRightBack
		});


		Mesh cube = new Mesh();
		cube.setMode(Mesh.Mode.TRIANGLES);
		cube.setBuffer(VertexBuffer.Type.Interleaved, 3, interleavedBuffer);
		cube.setBuffer(VertexBuffer.Type.Index, 3, indicesBuffer);

		cube.setBuffer(VertexBuffer.Type.Vertex, 3, interleavedBuffer);
		cube.setBuffer(VertexBuffer.Type.Color, 3, interleavedBuffer);

		cube.getBuffer(VertexBuffer.Type.Vertex).getPointer().setStride(6 * 4);
		cube.getBuffer(VertexBuffer.Type.Vertex).getPointer().setOffset(0);
		cube.getBuffer(VertexBuffer.Type.Color).getPointer().setStride(6 * 4);
		cube.getBuffer(VertexBuffer.Type.Color).getPointer().setOffset(3 * 4);
		return cube;
	}

	public static void main(String[] args) throws Exception {
		Sandbox box = new Sandbox();
		box.run();
	}
}
