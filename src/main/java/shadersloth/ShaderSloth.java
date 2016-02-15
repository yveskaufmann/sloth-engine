package shadersloth;

import core.engine.AppSettings;
import core.engine.Engine;
import core.engine.EngineApp;
import core.geometry.Mesh;
import core.geometry.primitives.Cube;
import core.geometry.primitives.Sphere;
import core.input.InputListener;
import core.input.event.KeyEvent;
import core.input.event.MouseEvent;
import core.light.LightList;
import core.light.PointLight;
import core.math.Color;
import core.renderer.FPSCounter;
import core.renderer.RenderState;
import core.scene.TargetCamera;
import core.shader.Shader;
import core.texture.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;

import static core.renderer.RenderState.BlendFunc;
import static core.renderer.RenderState.CullFaceMode;

public class ShaderSloth extends EngineApp implements InputListener {

	// TODO Encapsulate in scene
	private Matrix4f modelMatrix = new Matrix4f();
	private Matrix4f normalMatrix = new Matrix4f();
	private Matrix4f viewMatrix = new Matrix4f();
	private Matrix4f modelViewMatrix = new Matrix4f();
	private Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f modelViewProjectionMatrix = new Matrix4f();


	// TODO: should provided by a special node type: Geometry
	private Mesh rabbit;
	private Vector3f cubePos;
	private TargetCamera camera;
	float zoomLevel;

	private Shader diffuseShader;
	private Texture rabbitDiffuse;
	private LightList lightList;

	@Override
	protected void prepare() {
		Engine.register(new FPSCounter());
		inputManager.addListener(this);

		camera = new TargetCamera();
		zoomLevel = 1.0f;

		rabbit = Engine.getMesh("rabbit.obj");
		rabbit = new Sphere(2.0f, 16, 16);
		rabbit = new Cube();
		diffuseShader = Engine.getShader("Default");
		rabbitDiffuse = Engine.textureManager().createTexture("rabbitDiffuse", "brick.jpg");
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

		zoomLevel -= 30.0 * elapsedTime * inputManager.getMouseWheelAmount();

		modelMatrix.identity().scale(1.0f).translate(cubePos);
		camera.setPosition(0f, 5f, 2f);
		camera.setTarget(cubePos);
		camera.zoom(zoomLevel);
		camera.update(elapsedTime);

		viewMatrix.set(camera.getViewMatrix());
		modelViewMatrix.identity().set(viewMatrix).mul(modelMatrix, modelViewMatrix);
		projectionMatrix.identity().setPerspective(45.0f, (float) (window.getWidth() / window.getHeight()) ,0.01f, 100.0f);
		modelViewProjectionMatrix.identity().mul(projectionMatrix).mul(modelViewMatrix);
		viewMatrix.normal(normalMatrix);
	}

	@Override
	public void render(float elapsedTime) throws IOException {
		renderer.clearBuffers(true, true, false);
		rendererManager.getRenderState().apply();

		diffuseShader.getUniform("mvp").setValue(modelViewProjectionMatrix);
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

	@Override
	public void onMouseEvent(MouseEvent event) {
	}

	@Override
	public void onKeyEvent(KeyEvent keyEvent) {
		RenderState state = rendererManager.getRenderState();
		if (keyEvent.isPressed()) {
			switch (keyEvent.getKeyButton()) {
				case Esc: Engine.requestExit();
				case P: rabbitDiffuse.setMinFilter(Texture.MinFilter.Trilinear); break;
				case O: rabbitDiffuse.setMinFilter(Texture.MinFilter.Bilinear); break;
				case I: rabbitDiffuse.setMinFilter(Texture.MinFilter.NearestNeighbour); break;
				case A: zoomLevel += 0.1; break;
				case Q: zoomLevel -= 0.1; break;
				case C:
					if (state.getCullFaceMode() == CullFaceMode.Off) {
						state.setCullFaceMode(CullFaceMode.Back);
					} else {
						state.setCullFaceMode(CullFaceMode.Off);
					}
					state.apply();
					break;
				case W: state.enableWireframe(!state.isWireframe()).apply(); break;
				case Up: cubePos.add(0.0f, 0.2f, 0.0f); break;
				case Left: cubePos.add(-0.2f, 0.0f, 0.0f); break;
				case Right: cubePos.add(0.2f, 0.0f, 0.0f); break;
				case Down: cubePos.add(0.0f, -0.2f, 0.0f); break;


			}
		}
	}

	/**
	 * Run the core.renderer without offscreen support, exists only for
	 * development and debugging purposes.
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		EngineApp sandbox = new ShaderSloth();
		sandbox.start(new AppSettings());
	}



}
