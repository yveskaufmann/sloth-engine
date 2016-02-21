package core.scene.traversal;

import core.scene.Node;

public interface GraphTraversal {
	void traverse(Node node, Visitor<Node> visitor, int depth);
	default void traverse(Node node, Visitor<Node> visitor) {
		traverse(node, visitor, Integer.MAX_VALUE);
	}
}
