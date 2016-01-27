package scene;

import geometry.Mesh;
import renderer.RenderState;
import renderer.Renderer;

public class Geometry extends Node {

	private final Mesh mesh;
	private final RenderState renderState;

	public Geometry(String id, Mesh mesh) {
		super(id);
		this.mesh = mesh;
		renderState = new RenderState();
	}

	public void preRender(Renderer renderer) {}
	public void render(Renderer renderer) {};
	public void postRender(Renderer renderer) {};
}
