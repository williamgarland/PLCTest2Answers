

public class LiteralExpressionNode extends ExpressionNode {

	private Token content;

	public LiteralExpressionNode(Token content) {
		this.content = content;
	}
	
	public Token getContent() {
		return content;
	}
	
}
