

public class AssignmentNode extends Node {

	private Token id;
	private ExpressionNode expression;
	
	public AssignmentNode(Token id, ExpressionNode expression) {
		this.id = id;
		this.expression = expression;
	}
	
	public Token getId() {
		return id;
	}
	
	public ExpressionNode getExpression() {
		return expression;
	}
	
}
