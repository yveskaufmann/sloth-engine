package shadersloth;

import core.engine.AppSettings;
import core.engine.Engine;
import core.engine.EngineApp;
import core.geometry.primitives.Cube;
import core.geometry.primitives.Sphere;
import core.input.InputListener;
import core.input.event.KeyEvent;
import core.input.event.MouseEvent;
import core.light.LightList;
import core.light.PointLight;
import core.material.BasicMaterial;
import core.math.Color;
import core.renderer.FPSCounter;
import core.renderer.RenderState;
import core.scene.Geometry;
import core.scene.TargetCamera;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static core.renderer.RenderState.CullFaceMode;

public class ShaderSloth extends EngineApp implements InputListener {

	float zoomLevel;
	private LightList lightList;
	private BasicMaterial material;
	List<Geometry> boxes = new ArrayList<>();
	private float rotX = 0.0f;

	@Override
	protected void prepare() {

		zoomLevel = 1.0f;

		Engine.register(new FPSCounter());
		inputManager.addListener(this);

		Random rnd = new Random();
		rnd.setSeed(System.nanoTime());

		material = new BasicMaterial();
		Sphere cube = new Sphere(1.0f, 32, 32);

		float radius = 5.0f;
		int boxCount = 25;


		float R = (1.0f / boxCount);
		for (int i = 0; i <= boxCount; i++) {
			Geometry box = new Geometry("Box " + i);
			box.setMaterial(material);
			box.setScale(0.5f);
			box.setMesh(cube);
			box.setPosition(new Vector3f((float) (Math.cos(2.0 * Math.PI * i * R) * radius),  (float) Math.sin(2.0 * Math.PI * i * R) * radius ,  (float) Math.sin(2.0 * Math.PI * i * R) * 20));
			boxes.add(box);
			scene.getRootNode().addChild(box);

		}


		scene.getCamera().setPosition(0f, 5f, 10f);
		// ((TargetCamera)scene.getCamera()).setTarget(0.0f, 0.0f, -20.0f);



		lightList = new LightList();
		PointLight point = new PointLight();
		point.setAttenuation(1.0f);
		point.setPosition(new Vector3f(0.0f, 5.0f, -10.f));
		point.setColor(new Color(0.8f, 0.8f, 0.8f, 1.0f));
		lightList.add(point);
		point = new PointLight();
		point.setAttenuation(1.0f);
		point.setPosition(new Vector3f(0.0f, 2.0f, -5.f));
		point.setColor(new Color(0.3f, 0.8f, 0.0f, 1.0f));
		lightList.add(point);

		scene.setLightList(lightList);


	}

	@Override
	public void update(float elapsedTime) {
		zoomLevel -= 30.0 * elapsedTime * inputManager.getMouseWheelAmount();
		// ((TargetCamera)scene.getCamera()).zoom(zoomLevel);
		// TODO the scene class should handle this
		scene.getCamera().update(elapsedTime);
	}

	@Override
	protected void cleanUp() {
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
				case A: zoomLevel += 0.1; break;
				case Q: zoomLevel -= 0.1; break;
				case C:
					if (material.getRenderState().getCullFaceMode() == CullFaceMode.Off) {
						material.getRenderState().setCullFaceMode(CullFaceMode.Back);
					} else {
						material.getRenderState().setCullFaceMode(CullFaceMode.Off);
					}
					state.apply();
					break;
				case W: material.getRenderState().toggleWireframe(); break;
				case Up: boxes.forEach((box) -> box.getPosition().add(0.0f, 0.2f, 0.0f)); break;
				case Left: boxes.forEach((box) -> box.getPosition().add(-0.2f, 0.0f, 0.0f)); break;
				case Right:boxes.forEach((box) ->  box.getPosition().add(0.2f, 0.0f, 0.0f)); break;
				case Down: boxes.forEach((box) -> box.getPosition().add(0.0f, -0.2f, 0.0f)); break;
				case PageUp: boxes.forEach((box) -> box.getPosition().add(0.0f, 0.0f, -0.2f)); break;
				case PageDown: boxes.forEach((box) -> box.getPosition().add(0.0f, 0.0f, 0.2f)); break;
				case End: rotX += 0.1f; boxes.forEach((box) -> box.getRotation().rotationXYZ(rotX, -rotX, rotX)); break;


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
