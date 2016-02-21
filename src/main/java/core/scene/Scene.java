package core.scene;

import core.light.LightList;
import core.scene.camera.Camera;
import core.scene.camera.FreeCamera;
import core.scene.traversal.DepthFirstTraverse;
import core.scene.traversal.Visitor;

public class Scene {

	public static final String ROOT_NODE = "Root Node";

	private DepthFirstTraverse depthFirstTraverser = new DepthFirstTraverse();

	/**
	 * Root Node of this scene
	 */
	private Node rootNode = new Node(ROOT_NODE);

	/**
	 * Indicates if this node is active and
	 * should be rendered.
	 */
	private boolean active = true;

	/**
	 * Current Camera of this scene
	 */
	private Camera camera;
	private LightList lights;

	/**
	 * Creates a empty active scene.
	 */
	public Scene() {
		camera = new FreeCamera();
	}

	/**
	 * @return the Root node of this scene
     */
	public Node getRootNode() {
		return rootNode;
	}

	/**
	 * Determines if this scene is active.
	 *
	 * @return true if the scene is active.
     */
	public boolean isActive() {
		return active;
	}

	/**
	 * Set this Scene active (true) or non active (false).
	 *
	 * @param active true activate this scene and false activate it.
     */
	public void setActive(boolean active) {
		this.active = active;
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public void setLightList(LightList lightList) {
		this.lights = lightList;
	}

	public LightList getLightList() {
		return this.lights;
	}

	public void traverse(Visitor<Node> visitor) {
		depthFirstTraverser.traverse(rootNode, visitor);
	}

}
