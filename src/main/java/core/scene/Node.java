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
	private Vector3f position = new Vector3f().zero();
	private Quaternionf rotation = new Quaternionf().identity();
	private Matrix4f transformMatrix = new Matrix4f();
	boolean transformChanged = false;

	public Node(String id) {
		resetTransform();
		setId(id);
	}



	public void traversePreOrder(Consumer<Node> nodeVisitor) {
		traversePreOrder(this, nodeVisitor, Integer.MAX_VALUE);
	}

	private void traversePreOrder(Node node, Consumer<Node> nodeVisitor, int depth) {
		if (node == null || depth == 0) {
			return;
		}

		nodeVisitor.accept(node);
		for (Node child : node.children) {
			traversePreOrder(child, nodeVisitor, depth - 1);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
		transformChanged = true;
	}


	public Vector3f getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale.set(scale);
		transformChanged = true;
	}

	public void setScale(Vector3f scaleVector) {
		scale.set(scaleVector);
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f translate) {
		this.position.set(translate);
		transformChanged = true;
	}

	public void resetTransform() {
		scale.set(1.0f);
		position.zero();
		rotation.identity();
	}

	public Matrix4f getTransformMatrix() {
		if (true) {
			transformMatrix.identity();
			transformMatrix.scale(scale).translation(position).rotate(rotation);


			// transformMatrix.translationRotateScale(position, rotation, scale);
			transformChanged = false;
		}
		return transformMatrix;
	}

}
