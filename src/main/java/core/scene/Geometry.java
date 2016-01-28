package core.scene;

import core.geometry.Mesh;
import core.renderer.RenderState;
import core.renderer.Renderer;

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
