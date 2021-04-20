

public class PrefixExpressionNode extends ExpressionNode {

	private Token op;
	private ExpressionNode arg;
	
	public PrefixExpressionNode(Token op, ExpressionNode arg) {
		this.op = op;
		this.arg = arg;
	}
	
	public Token getOp() {
		return op;
	}
	
	public ExpressionNode getArg() {
		return arg;
	}
	
}
