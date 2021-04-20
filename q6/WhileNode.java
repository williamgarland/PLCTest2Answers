

public class WhileNode extends Node {

	private ExpressionNode condition;
	private Node content;
	
	public WhileNode(ExpressionNode condition, Node content) {
		this.condition = condition;
		this.content = content;
	}
	
	public ExpressionNode getCondition() {
		return condition;
	}
	
	public Node getContent() {
		return content;
	}
	
}
