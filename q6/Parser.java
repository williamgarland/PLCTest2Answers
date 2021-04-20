

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

public class Parser {

	private Lexer lexer;
	private Token current;
	
	public Parser(Lexer lexer) {
		this.lexer = lexer;
	}
	
	private Token lh() {
		return current == null ? current = lexer.nextToken() : current;
	}
	
	private void advance() {
		current = lexer.nextToken();
	}
	
	private Token match(TokenType type) {
		Token t = lh();
		if (t.getType() != type)
			throw new IllegalStateException("Expected " + type + ", received " + t);
		advance();
		return t;
	}
	
	public WhileNode parseWhileStatement() {
		match(TokenType.KW_WHILE);
		match(TokenType.SM_LPAREN);
		ExpressionNode condition = parseExpression();
		match(TokenType.SM_RPAREN);
		Node content = parseContent();
		return new WhileNode(condition, content);
	}
	
	public IfNode parseIfStatement() {
		match(TokenType.KW_IF);
		match(TokenType.SM_LPAREN);
		ExpressionNode condition = parseExpression();
		match(TokenType.SM_RPAREN);
		Node content = parseContent();
		List<ElseIfNode> elseIfNodes = new ArrayList<>();
		Optional<Node> elseNode = Optional.empty();
		while (lh().getType() == TokenType.KW_ELSE) {
			advance();
			if (lh().getType() == TokenType.KW_IF) {
				advance();
				match(TokenType.SM_LPAREN);
				ExpressionNode elseIfCondition = parseExpression();
				match(TokenType.SM_RPAREN);
				Node elseIfContent = parseContent();
				elseIfNodes.add(new ElseIfNode(elseIfCondition, elseIfContent));
			} else {
				elseNode = Optional.of(parseContent());
				break;
			}
		}
		return new IfNode(condition, content, elseIfNodes, elseNode);
	}
	
	private Node parseContent() {
		TokenType t = lh().getType();
		if (t == TokenType.KW_IF)
			return parseIfStatement();
		else if (t == TokenType.KW_WHILE)
			return parseWhileStatement();
		else if (t == TokenType.SM_LBRACE)
			return parseBlock();
		else
			return parseAssignmentStatement();
	}
	
	private BlockNode parseBlock() {
		match(TokenType.SM_LBRACE);
		List<Node> content = new ArrayList<>();
		while (lexer.hasNext() && lh().getType() != TokenType.SM_RBRACE) {
			content.add(parseContent());
		}
		match(TokenType.SM_RBRACE);
		return new BlockNode(content);
	}
	
	public AssignmentNode parseAssignmentStatement() {
		// Assuming the identifier for the question is supposed to be the same one from question 1:
		Token id = parseId();
		match(TokenType.SM_ASSIGN);
		ExpressionNode expression = parseExpression();
		match(TokenType.SM_SEMICOLON);
		return new AssignmentNode(id, expression);
	}
	
	private Token parseId() {
		TokenType t = lh().getType();
		switch (t) {
		case ID_PRIVATE_ARRAY:
		case ID_PRIVATE_HASH:
		case ID_PRIVATE_SCALAR:
		case ID_PUBLIC_ARRAY:
		case ID_PUBLIC_HASH:
		case ID_PUBLIC_SCALAR:
			return match(t);
		default:
			throw new IllegalStateException("Expected identifier, received " + lh());
		}
	}
	
	public ExpressionNode parseExpression() {
		return logicalOrExpression();
	}
	
	private ExpressionNode logicalOrExpression() {
		ExpressionNode arg0 = logicalAndExpression();
		while (lh().getType() == TokenType.SM_LOGICAL_OR) {
			Token op = match(TokenType.SM_LOGICAL_OR);
			ExpressionNode arg1 = logicalAndExpression();
			arg0 = new BinaryExpressionNode(op, arg0, arg1);
		}
		return arg0;
	}
	
	private ExpressionNode logicalAndExpression() {
		ExpressionNode arg0 = additiveExpression();
		while (lh().getType() == TokenType.SM_LOGICAL_AND) {
			Token op = match(TokenType.SM_LOGICAL_AND);
			ExpressionNode arg1 = additiveExpression();
			arg0 = new BinaryExpressionNode(op, arg0, arg1);
		}
		return arg0;
	}
	
	private ExpressionNode additiveExpression() {
		ExpressionNode arg0 = multiplicativeExpression();
		while (isAdditiveOperator(lh().getType())) {
			Token op = match(TokenType.SM_LOGICAL_OR);
			ExpressionNode arg1 = multiplicativeExpression();
			arg0 = new BinaryExpressionNode(op, arg0, arg1);
		}
		return arg0;
	}
	
	private ExpressionNode multiplicativeExpression() {
		ExpressionNode arg0 = prefixExpression();
		while (isMultiplicativeOperator(lh().getType())) {
			Token op = match(TokenType.SM_LOGICAL_OR);
			ExpressionNode arg1 = prefixExpression();
			arg0 = new BinaryExpressionNode(op, arg0, arg1);
		}
		return arg0;
	}
	
	private boolean isAdditiveOperator(TokenType t) {
		return t == TokenType.SM_PLUS || t == TokenType.SM_MINUS;
	}
	
	private boolean isMultiplicativeOperator(TokenType t) {
		return t == TokenType.SM_ASTERISK || t == TokenType.SM_SLASH || t == TokenType.SM_PERCENT;
	}
	
	private ExpressionNode prefixExpression() {
		Deque<Token> ops = new ArrayDeque<>();
		while (lh().getType() == TokenType.SM_LOGICAL_NOT)
			ops.push(match(TokenType.SM_LOGICAL_NOT));
		ExpressionNode arg = primaryExpression();
		while (!ops.isEmpty())
			arg = new PrefixExpressionNode(ops.pop(), arg);
		return arg;
	}
	
	private ExpressionNode primaryExpression() {
		TokenType t = lh().getType();
		if (t == TokenType.STRING_LITERAL || t == TokenType.INTEGER_LITERAL || t == TokenType.FLOATING_POINT_LITERAL) {
			advance();
			return new LiteralExpressionNode(lh());
		} else if (isId(t)) {
			Token id = match(t);
			if (lh().getType() == TokenType.SM_LPAREN) {
				advance();
				match(TokenType.SM_RPAREN);
				return new FunctionCallExpressionNode(id);
			} else
				return new LiteralExpressionNode(id);
		} else {
			match(TokenType.SM_LPAREN);
			ExpressionNode result = parseExpression();
			match(TokenType.SM_RPAREN);
			return result;
		}
	}
	
	private boolean isId(TokenType t) {
		switch (t) {
		case ID_PRIVATE_ARRAY:
		case ID_PRIVATE_HASH:
		case ID_PRIVATE_SCALAR:
		case ID_PUBLIC_ARRAY:
		case ID_PUBLIC_HASH:
		case ID_PUBLIC_SCALAR:
			return true;
		default:
			return false;
		}
	}
	
}
