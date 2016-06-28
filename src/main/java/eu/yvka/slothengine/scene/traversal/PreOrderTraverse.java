package eu.yvka.slothengine.scene.traversal;

import eu.yvka.slothengine.scene.Node;

public class PreOrderTraverse implements GraphTraversal {

	@Override
	public void traverse(Node node, Visitor<Node> visitor, int depth) {
		if (node == null || depth == 0) {
			return;
		}

		visitor.visit(node);
		for (Node child : node.getChildren()) {
			traverse(child, visitor, depth - 1);
		}
	}
}
