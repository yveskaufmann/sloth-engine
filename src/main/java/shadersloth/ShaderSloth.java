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
import core.material.BasicMaterial;
import core.material.Material;
import core.math.Color;
import core.renderer.FPSCounter;
import core.renderer.RenderState;
import core.scene.camera.FreeCamera;
import core.scene.Geometry;
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
	private Geometry room;

	@Override
	protected void prepare() {

		zoomLevel = 1.0f;

		Engine.register(new FPSCounter());
		inputManager.addListener(this);

		Random rnd = new Random();
		rnd.setSeed(System.nanoTime());

		material = new BasicMaterial();
		Mesh cube = new Sphere(1, 20, 20);

		room = new Geometry("Room");
		room.setPosition(new Vector3f(0.0f, 0.0f, 0.0f));
		room.setScale(10.0f);
		room.setVisible(true);
		room.setMesh(new Cube());
		Material mat = new BasicMaterial();
		mat.getRenderState().setFrontFaceWinding(RenderState.FaceWinding.GL_CCW);
		mat.getRenderState().setCullFaceMode(CullFaceMode.Front);
		room.setMaterial(mat);
		scene.getRootNode().addChild(room);

		scene.getCamera().setPosition(0f, 5f, 10f);
		((FreeCamera)scene.getCamera()).setTarget(new Vector3f(10.0f, 2.0f, -10.0f));

		// ((TargetCamera)scene.getCamera()).setTarget(0.0f, 0.0f, -20.0f);

		float radius = 2.0f;
		int boxCount = 5;
		float PI2 = (float) (Math.PI * 2);
		float R = (1.0f / boxCount);
		for (int i = 0; i <= boxCount; i++) {
			Geometry box = new Geometry("Box " + i);
			box.setMaterial(material);
			box.setScale(1.0f);
			box.setMesh(cube);
			box.setPosition(new Vector3f((float) (Math.cos(R * i * PI2) * radius), 0, (float) (Math.sin(R * i * PI2) * radius)));
			boxes.add(box);
			room.addChild(box);

		}

		Geometry g = new Geometry("g");
		g.setMesh(new Cube());
		g.setPosition(-9.0f, 0.0f, 0.0f);
		g.setMaterial(new BasicMaterial());
		room.addChild(g);


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
					rendererManager.getRenderer().applyRenderState(material.getRenderState());
					break;
				case E: material.getRenderState().toggleWireframe(); break;
				case Up:    room.getPosition().add(0.0f, 0.2f, 0.0f); break;
				case Left:  room.getPosition().add(-0.2f, 0.0f, 0.0f); break;
				case Right: room.getPosition().add(0.2f, 0.0f, 0.0f); break;
				case Down:  room.getPosition().add(0.0f, -0.2f, 0.0f); break;
				case PageUp: boxes.forEach((box) -> box.getPosition().add(0.0f, 0.0f, -0.2f)); break;
				case PageDown: boxes.forEach((box) -> box.getPosition().add(0.0f, 0.0f, 0.2f)); break;
				case End: rotX += 0.1f; boxes.forEach((box) -> box.getRotation().rotationXYZ(rotX, -rotX, rotX)); break;
				case H: room.setVisible(! room.isVisible()); break;
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
