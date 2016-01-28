package core.scene;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Node {

	private String id;
	private List<Node> children = new ArrayList<>();
	private Node parent = null;

	private Vector3f scale = new Vector3f(1f, 1f, 1f);
	private Vector3f translate = new Vector3f().zero();
	private Quaternionf rotation = new Quaternionf().identity();
	private Matrix4f transformMatrix = new Matrix4f();

	public Node(String id) {
		resetTransform();
		setId(id);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void traversePreOrder(Consumer<Node> nodeVisitor) {
		traversePreOrder(nodeVisitor, -1);
	}

	public void traversePreOrder(Consumer<Node> nodeVisitor, int limit) {
		traversePreOrder(this, nodeVisitor, limit);
	}

	private void traversePreOrder(Node node, Consumer<Node> nodeVisitor, int depth) {
		if (node == null || depth == 0) {
			return;
		}

		nodeVisitor.accept(this);
		for (Node child : children) {
			traversePreOrder(child, nodeVisitor, depth -1);
		}
	}

	public Node getChild(String id) {
		for (Node child : children) {
			if (id.equals(child.getId())) {
				return child;
			}

			Node desiredChild = child.getChild(id);
			if (desiredChild != null) {
				return desiredChild;
			}
		}
		return null;
	}

	public List<Node> getChildren() {
		return children;
	}

	public void addChild(Node node) {
		if (node == null) {
			throw new IllegalArgumentException("Child must be not null");
		}

		if (node == this) {
			throw new IllegalArgumentException("A node cannot become it's  own child");
		}

		if (node.hasParent()) {
			node.getParent().removeChild(node);
		}
		node.setParent(node);
		children.add(node);
	}

	public int removeChild(Node child) {
		if (child.getParent() == this) {
			int childIndex = children.indexOf(child);
			if (childIndex != -1) {
				child.setParent(null);
				children.remove(childIndex);
				return childIndex;
			}
		}
		return -1;
	}

	public Node getParent() {
		return parent;
	}

	public boolean hasParent() {
		return parent != null;
	}

	protected void setParent(Node parent) {
		this.parent = parent;
	}

	public Quaternionf getRotation() {
		return rotation;
	}

	public void setRotation(Quaternionf rotation) {
		this.rotation.set(rotation);
	}


	public Vector3f getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale.set(scale);
	}

	public void setScale(Vector3f scaleVectot) {
		scale.set(scaleVectot);
	}

	public Vector3f getTranslate() {
		return translate;
	}

	public void setTranslate(Vector3f translate) {
		this.translate.set(translate);
	}

	public void resetTransform() {
		scale.set(1.0f);
		translate.zero();
		rotation.identity();
	}

	public Matrix4f getTransformMatrix() {
		if (true) {
			transformMatrix.identity();
			transformMatrix.scale(scale);
			transformMatrix.rotate(rotation);
			transformMatrix.translate(translate);
		}
		return transformMatrix;
	}

}
