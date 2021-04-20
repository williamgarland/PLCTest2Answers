

public class FunctionCallExpressionNode extends ExpressionNode {

	private Token id;

	public FunctionCallExpressionNode(Token id) {
		this.id = id;
	}
	
	public Token getId() {
		return id;
	}
	
}
