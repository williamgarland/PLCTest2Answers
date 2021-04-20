

import java.util.List;
import java.util.Optional;

public class IfNode extends Node {

	private ExpressionNode condition;
	private Node content;
	private List<ElseIfNode> elseIfNodes;
	private Optional<Node> elseNode;
	
	public IfNode(ExpressionNode condition, Node content, List<ElseIfNode> elseIfNodes, Optional<Node> elseNode) {
		this.condition = condition;
		this.content = content;
		this.elseIfNodes = elseIfNodes;
		this.elseNode = elseNode;
	}
	
	public ExpressionNode getCondition() {
		return condition;
	}
	
	public Node getContent() {
		return content;
	}
	
	public List<ElseIfNode> getElseIfNodes() {
		return elseIfNodes;
	}
	
	public Optional<Node> getElseNode() {
		return elseNode;
	}
	
}
