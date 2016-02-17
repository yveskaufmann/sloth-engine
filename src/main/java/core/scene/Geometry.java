package core.scene;

import core.geometry.Mesh;
import core.material.BasicMaterial;
import core.material.Material;
import core.renderer.RenderState;
import core.renderer.Renderer;
import org.joml.Vector3f;

public class Geometry extends Node {

	private Mesh mesh;
	private Material material;


	public Geometry(String id) {
		super(id);
		this.material = new BasicMaterial();
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public Mesh getMesh() {
		return mesh;
	}

	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}

}
