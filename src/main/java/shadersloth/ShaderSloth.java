package shadersloth;

import core.Engine;
import core.geometry.Mesh;
import core.geometry.primitives.Cube;
import core.geometry.primitives.Sphere;
import core.light.LightList;
import core.light.PointLight;
import core.math.Color;
import core.renderer.RenderState;
import core.scene.TargetCamera;
import core.shader.Shader;
import core.texture.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.io.IOException;

import static core.renderer.RenderState.BlendFunc;
import static core.renderer.RenderState.CullFaceMode;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TRUE;

// TODO inputManager
// TODO offscreen rendering support
public class ShaderSloth extends SlothApplication {

	private GLFWKeyCallback keyCallback;

	// TODO Encapsulate in scene
	private Matrix4f modelMatrix = new Matrix4f();
	private Matrix4f normalMatrix = new Matrix4f();
	private Matrix4f viewMatrix = new Matrix4f();
	private Matrix4f modelViewMatrix = new Matrix4f();
	private Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f modelViewProjMatrix = new Matrix4f();


	// TODO: should provided by a special node type: Geometry
	private Mesh rabbit;
	private Vector3f cubePos;

	// TODO: InputHandling should be managed by dedicated class
	private TargetCamera camera;
	float zoomLevel;

	private Shader diffuseShader;
	private Texture rabbitDiffuse;
	private LightList lightList;

	@Override
	protected void prepare() {
		addInputHandler();
		camera = new TargetCamera();
		zoomLevel = 1.0f;

		rabbit = Engine.getMesh("rabbit.obj");
		rabbit = new Sphere(2.0f, 16, 16);
		rabbit = new Cube();
		diffuseShader = Engine.getShader("Default");
		rabbitDiffuse = Engine.getTextureManager().createTexture("rabbitDiffuse", "brick.jpg");
		rabbitDiffuse.setMinFilter(Texture.MinFilter.Trilinear);
		rabbitDiffuse.setMagFilter(Texture.MagFilter.Bilinear);
		rabbitDiffuse.setAnisotropicLevel(16.0f);

		cubePos = new Vector3f(0f, 0f, 0f);
		rendererManager.getRenderState().enableFPSCounter().setCullFaceMode(CullFaceMode.Off).apply();
		renderer.setClearColor(Color.LightGrey);

		lightList = new LightList();
		PointLight point = new PointLight();
		point.setAttenuation(0.8f);
		point.setPosition(new Vector3f(0.0f, -1.0f, 0.2f));
		point.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
		lightList.add(point);

	}

	@Override
	public void update(float elapsedTime) {
		modelMatrix.identity().scale(1.0f).translate(cubePos);
		camera.setPosition(0f, 5f, 2f);
		camera.setTarget(cubePos);
		camera.zoom(zoomLevel);
		camera.update(elapsedTime);

		viewMatrix.set(camera.getViewMatrix());
		modelViewMatrix.identity().set(viewMatrix).mul(modelMatrix, modelViewMatrix);
		projectionMatrix.identity().setPerspective(45.0f, (float) (window.getWidth() / window.getHeight()) ,0.01f, 100.0f);
		modelViewProjMatrix.identity().mul(projectionMatrix).mul(modelViewMatrix);
		viewMatrix.normal(normalMatrix);
	}

	@Override
	public void render(float elapsedTime) throws IOException {

		renderer.clearBuffers(true, true, false);
		diffuseShader.getUniform("mvp").setValue(modelViewProjMatrix);
		diffuseShader.getUniform("modelViewMatrix").setValue(modelViewMatrix);
		diffuseShader.getUniform("normalMatrix").setValue(normalMatrix);
		diffuseShader.getUniform("diffuseTexture").setValue(1);
		lightList.passToShader(diffuseShader);

		renderer.setShader(diffuseShader);
		renderer.setTexture(1, rabbitDiffuse);
		renderer.drawMesh(rabbit);
		// renderWithWireframe(diffuseShader);


	}

	@Override
	protected void cleanUp() {
		keyCallback.release();
	}

	private void renderWithWireframe(Shader diffuseShader) throws IOException {


		// Render the thick wireframe version.
		rendererManager.getRenderState()
			.setBlendMode(BlendFunc.Color)
			.enableWireframe(true)
			.enableSmoothLines()
			.setLineWidth(50.0f)
			.apply();


		diffuseShader.getUniform("isWireframe").setValue(1);
		renderer.setShader(diffuseShader);
		renderer.drawMesh(rabbit);
		rendererManager.getRenderState()
			.setBlendMode(BlendFunc.Alpha)
			.enableWireframe(false)
			.disableSmoothLines()
			.setLineWidth(1.0f)
			.apply();
		diffuseShader.getUniform("isWireframe").setValue(0);
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

				if (key == GLFW_KEY_I && action == GLFW_PRESS) {
					rabbitDiffuse.setMinFilter(Texture.MinFilter.NearestNeighbour);
				}

				if (key == GLFW_KEY_O && action == GLFW_PRESS) {
					rabbitDiffuse.setMinFilter(Texture.MinFilter.Bilinear);
				}

				if (key == GLFW_KEY_P && action == GLFW_PRESS) {
					rabbitDiffuse.setMinFilter(Texture.MinFilter.Trilinear);
				}

			}
		});
	}


	/**
	 * Run the core.renderer without offscreen support, exists only for
	 * development and debugging purposes.
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		SlothApplication box = new ShaderSloth();
		box.run();
	}


}
