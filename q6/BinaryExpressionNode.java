

public class BinaryExpressionNode extends ExpressionNode {

	private Token op;
	private ExpressionNode arg0;
	private ExpressionNode arg1;
	
	public BinaryExpressionNode(Token op, ExpressionNode arg0, ExpressionNode arg1) {
		this.op = op;
		this.arg0 = arg0;
		this.arg1 = arg1;
	}
	
	public Token getOp() {
		return op;
	}
	
	public ExpressionNode getArg0() {
		return arg0;
	}
	
	public ExpressionNode getArg1() {
		return arg1;
	}
	
}
