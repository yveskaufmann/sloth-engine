package eu.yvka.slothengine.scene;

import eu.yvka.slothengine.geometry.Mesh;
import eu.yvka.slothengine.material.BasicMaterial;
import eu.yvka.slothengine.material.Material;

public class Geometry extends Node {

	private Mesh mesh;
	private Material material;
	private boolean visible;


	public Geometry(String id) {
		super(id);
		try {
			this.material = new BasicMaterial();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		this.visible = true;
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

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
