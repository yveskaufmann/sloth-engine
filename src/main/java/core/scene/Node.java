package core.scene;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Node extends Spatial {

	private List<Node> children = new ArrayList<>();

	public Node(String id) {
		super(id);
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
		node.setParent(this);
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

}
