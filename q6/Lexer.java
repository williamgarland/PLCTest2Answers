

import java.nio.file.Path;

public class Lexer {

	private CharSequence input;
	private int current;
	private int line;
	private int col;
	private Path file;
	
	public Lexer(Path file, CharSequence input) {
		this.file = file;
		this.input = input;
		this.line = 1;
		this.col = 1;
	}
	
	public Path getFile() {
		return file;
	}
	
	public boolean hasNext() {
		return current < input.length();
	}
	
	private char get() {
		return hasNext() ? input.charAt(current) : (char) -1;
	}
	
	private void advance() {
		current++;
		col++;
	}
	
	private void retract() {
		current--;
		col--;
	}
	
	private SourceMeta genMeta() {
		return new SourceMeta(file, line, col);
	}
	
	private boolean isWhitespace(char c) {
		return c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == '\013' || c == '\f';
	}
	
	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
	
	private boolean isIdentifierPart(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_' || isDigit(c);
	}
	
	private boolean isSymbolChar(char c) {
		return c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == ';'
		|| c == '&' || c == '|' || c == '{' || c == '}' || c == '[' || c == ']' || c == '(' || c == ')';
	}

	private boolean isUppercaseLetter(char c) {
		return c >= 'A' && c <= 'Z';
	}

	private boolean isLowercaseLetter(char c) {
		return c >= 'a' && c <= 'z';
	}

	private boolean isHexDigit(char c) {
		return (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F') || (c >= '0' && c <= '9');
	}
	
	private boolean isOctalDigit(char c) {
		return c >= '0' && c <= '7';
	}
	
	private boolean isBinaryDigit(char c) {
		return c == '0' || c == '1';
	}
	
	private boolean isIntSuffix(char c) {
		return c == 'u' || c == 'U' || c == 'l' || c == 'L';
	}
	
	private boolean isFloatSuffix(char c) {
		return c == 'f' || c == 'F' || c == 'l' || c == 'L';
	}
	
	private TokenType getSymbolType(String str) {
		switch (str) {
		case "(":
			return TokenType.SM_LPAREN;
		case ")":
			return TokenType.SM_RPAREN;
		case "[":
			return TokenType.SM_LBRACKET;
		case "]":
			return TokenType.SM_RBRACKET;
		case "{":
			return TokenType.SM_LBRACE;
		case "}":
			return TokenType.SM_RBRACE;
		case "+":
			return TokenType.SM_PLUS;
		case "-":
			return TokenType.SM_MINUS;
		case "*":
			return TokenType.SM_ASTERISK;
		case "/":
			return TokenType.SM_SLASH;
		case "%":
			return TokenType.SM_PERCENT;
		case "&&":
			return TokenType.SM_LOGICAL_AND;
		case "||":
			return TokenType.SM_LOGICAL_OR;
		case "!":
			return TokenType.SM_LOGICAL_NOT;
		case "=":
			return TokenType.SM_ASSIGN;
		case ";":
			return TokenType.SM_SEMICOLON;
		default:
			return null;
		}
	}
	
	private TokenType getType(String str) {
		switch (str) {
		case "String":
			return TokenType.TYPE_STRING;
		case "Integer":
			return TokenType.TYPE_INTEGER;
		case "Character":
			return TokenType.TYPE_CHARACTER;
		case "Float":
			return TokenType.TYPE_FLOAT;
		case "Void":
			return TokenType.TYPE_VOID;
		default:
			throw new IllegalStateException("Invalid type " + str);
		}
	}
	
	public Token nextToken() {
		while (hasNext() && isWhitespace(get()))
			advance();
		
		if (!hasNext())
			return null;
		
		char c = get();
		
		if (c == '$')
			return scalarIdentifier();
		else if (c == '@')
			return arrayIdentifier();
		else if (c == '%')
			return hashIdentifier();
		else if (isDigit(c) || c == '.')
			return number();
		else if (c == '"')
			return stringLiteral();
		else if (c == '\'')
			return charLiteral();
		else if (isSymbolChar(c))
			return symbol();
		else if (isUppercaseLetter(c))
			return type();
		else if (isLowercaseLetter(c))
			return keyword();
		else
			throw new IllegalStateException("Invalid input " + c);
	}
	
	private Token keyword() {
		SourceMeta m = genMeta();
		StringBuilder sb = new StringBuilder();
		sb.append(get());
		advance();
		char c;
		while (isLowercaseLetter(c = get())) {
			sb.append(c);
			advance();
		}
		TokenType type = getKeywordType(sb.toString());
		return new Token(type, sb.toString(), m);
	}
	
	private TokenType getKeywordType(String str) {
		switch (str) {
		case "while":
			return TokenType.KW_WHILE;
		case "if":
			return TokenType.KW_IF;
		case "else":
			return TokenType.KW_ELSE;
		default:
			throw new IllegalStateException("Invalid keyword " + str);
		}
	}
	
	private String hexExponent() {
		StringBuilder sb = new StringBuilder();
		char c = get();
		if (c != 'p' && c != 'P')
			throw new IllegalStateException("Invalid hexadecimal floating-point literal exponent");
		sb.append(c);
		advance();
		c = get();
		if (c == '+' || c == '-') {
			sb.append(c);
			advance();
		}
		sb.append(digits());
		return sb.toString();
	}
	
	private String exponent() {
		StringBuilder sb = new StringBuilder();
		sb.append(get());
		advance();
		char c = get();
		if (c == '+' || c == '-') {
			sb.append(c);
			advance();
		}
		sb.append(digits());
		return sb.toString();
	}
	
	private String digits() {
		StringBuilder sb = new StringBuilder();
		char c = get();
		if (!isDigit(c))
			throw new IllegalStateException("Invalid integer literal");
		sb.append(c);
		advance();
		while (isDigit(c = get())) {
			sb.append(c);
			advance();
		}
		return sb.toString();
	}
	
	private String intSuffix() {
		StringBuilder sb = new StringBuilder();
		char c = get();
		if (c == 'u' || c == 'U') {
			sb.append(c);
			advance();
			if (c == 'l') {
				sb.append(c);
				advance();
				c = get();
				if (c == 'l') {
					sb.append(c);
					advance();
				}
			} else if (c == 'L') {
				sb.append(c);
				advance();
				c = get();
				if (c == 'L') {
					sb.append(c);
					advance();
				}
			}
		} else if (c == 'l') {
			sb.append(c);
			advance();
			c = get();
			if (c == 'l') {
				sb.append(c);
				advance();
				c = get();
				if (c == 'u' || c == 'U') {
					sb.append(c);
					advance();
				}
			} else if (c == 'u' || c == 'U') {
				sb.append(c);
				advance();
			}
		} else {
			sb.append(c);
			advance();
			c = get();
			if (c == 'L') {
				sb.append(c);
				advance();
				c = get();
				if (c == 'u' || c == 'U') {
					sb.append(c);
					advance();
				}
			} else if (c == 'u' || c == 'U') {
				sb.append(c);
				advance();
			}
		}
		
		return sb.toString();
	}

	private Token number() {
		SourceMeta m = genMeta();
		char first = get();
		if (first == '.') {
			advance();
			return decimalFloatWithInitialDot(m);
		} else if (first == '0') {
			advance();
			char c = get();
			if (c == 'x' || c == 'X') {
				advance();
				return hexOrHexFloat("0" + c, m);
			} else if (c == 'b' || c == 'B') {
				advance();
				return binaryLiteral("0" + c, m);
			} else {
				return octalOrDecimalFloat("0", m);
			}
		} else {
			return decimalOrDecimalFloat(m);
		}
	}

	private Token decimalOrDecimalFloat(SourceMeta m) {
		StringBuilder sb = new StringBuilder();
		sb.append(get());
		advance();

		char c;
		while (isDigit(c = get())) {
			sb.append(c);
			advance();
		}

		if (c == 'e' || c == 'E') {
			sb.append(exponent());
			c = get();
			if (isFloatSuffix(c)) {
				sb.append(c);
				advance();
			}

			return new Token(TokenType.FLOATING_POINT_LITERAL, sb.toString(), m);
		} else if (c == '.') {
			sb.append(c);
			advance();
			while (isDigit(c = get())) {
				sb.append(c);
				advance();
			}

			c = get();
			if (c == 'e' || c == 'E')
				sb.append(exponent());
			
			c = get();
			if (isFloatSuffix(c)) {
				sb.append(c);
				advance();
			}

			return new Token(TokenType.FLOATING_POINT_LITERAL, sb.toString(), m);
		} else if (isIntSuffix(c)) {
			sb.append(intSuffix());
			return new Token(TokenType.INTEGER_LITERAL, sb.toString(), m);
		} else
			return new Token(TokenType.INTEGER_LITERAL, sb.toString(), m);
	}

	private Token binaryLiteral(String initial, SourceMeta m) {
		StringBuilder sb = new StringBuilder();
		sb.append(initial);
		char c = get();
		if (!isBinaryDigit(c))
			throw new IllegalStateException("Invalid binary integer literal");
		sb.append(c);
		advance();
		while (isBinaryDigit(c = get())) {
			sb.append(c);
			advance();
		}
		if (isIntSuffix(c))
			sb.append(intSuffix());
		return new Token(TokenType.INTEGER_LITERAL, sb.toString(), m);
	}

	private Token octalOrDecimalFloat(String initial, SourceMeta m) {
		StringBuilder sb = new StringBuilder();
		sb.append(initial);
		char c;
		while (isOctalDigit(c = get())) {
			sb.append(c);
			advance();
		}

		if (isDigit(c)) {
			while (isDigit(c = get())) {
				sb.append(c);
				advance();
			}

			if (c == '.') {
				sb.append(c);
				advance();
				while (isDigit(c = get())) {
					sb.append(c);
					advance();
				}
			}

			c = get();
			if (c == 'e' || c == 'E')
				sb.append(exponent());
			
			c = get();
			if (isFloatSuffix(c)) {
				sb.append(c);
				advance();
			}

			return new Token(TokenType.FLOATING_POINT_LITERAL, sb.toString(), m);
		} else if (c == '.') {
			sb.append(c);
			advance();
			while (isDigit(c = get())) {
				sb.append(c);
				advance();
			}

			c = get();
			if (c == 'e' || c == 'E')
				sb.append(exponent());
			
			c = get();
			if (isFloatSuffix(c)) {
				sb.append(c);
				advance();
			}

			return new Token(TokenType.FLOATING_POINT_LITERAL, sb.toString(), m);
		} else if (c == 'e' || c == 'E') {
			sb.append(exponent());

			c = get();
			if (isFloatSuffix(c)) {
				sb.append(c);
				advance();
			}

			return new Token(TokenType.FLOATING_POINT_LITERAL, sb.toString(), m);
		} else if (isIntSuffix(c)) {
			sb.append(intSuffix());
			return new Token(TokenType.INTEGER_LITERAL, sb.toString(), m);
		} else {
			return new Token(TokenType.INTEGER_LITERAL, sb.toString(), m);
		}
	}

	private Token decimalFloatWithInitialDot(SourceMeta m) {
		StringBuilder sb = new StringBuilder();
		sb.append("." + digits());
		char c = get();
		if (c == 'e' || c == 'E')
			sb.append(exponent());
		c = get();
		if (isFloatSuffix(c)) {
			sb.append(c);
			advance();
		}
		return new Token(TokenType.FLOATING_POINT_LITERAL, sb.toString(), m);
	}

	private Token hexOrHexFloat(String initial, SourceMeta m) {
		StringBuilder sb = new StringBuilder();
		char c = get();
		if (isHexDigit(c)) {
			sb.append(c);
			advance();
		} else if (c == '.') {
			advance();
			return hexFloatWithInitialDot(initial, m);
		} else
			throw new IllegalStateException("Invalid hexadecimal integer literal");

		while (isHexDigit(c = get())) {
			sb.append(c);
			advance();
		}

		if (isIntSuffix(c)) {
			sb.append(intSuffix());
			return new Token(TokenType.INTEGER_LITERAL, sb.toString(), m);
		} else if (c == 'p' || c == 'P') {
			sb.append(hexExponent());
			if (isFloatSuffix(c = get())) {
				sb.append(c);
				advance();
			}
			return new Token(TokenType.FLOATING_POINT_LITERAL, sb.toString(), m);
		} else if (c == '.') {
			sb.append(c);
			advance();
			while (isHexDigit(c = get())) {
				sb.append(c);
				advance();
			}
			sb.append(hexExponent());
			if (isFloatSuffix(c = get())) {
				sb.append(c);
				advance();
			}
			return new Token(TokenType.FLOATING_POINT_LITERAL, sb.toString(), m);
		} else
			return new Token(TokenType.INTEGER_LITERAL, sb.toString(), m);
	}

	private Token hexFloatWithInitialDot(String initial, SourceMeta m) {
		StringBuilder sb = new StringBuilder();
		sb.append(initial + ".");
		char c = get();
		if (!isHexDigit(c))
			throw new IllegalStateException("Invalid hexadecimal floating-point literal");
		sb.append(c);
		advance();
		while (isHexDigit(c = get())) {
			sb.append(c);
			advance();
		}
		sb.append(hexExponent());
		c = get();
		if (isFloatSuffix(c)) {
			sb.append(c);
			advance();
		}
		return new Token(TokenType.FLOATING_POINT_LITERAL, sb.toString(), m);
	}

	private Token scalarIdentifier() {
		SourceMeta m = genMeta();
		StringBuilder sb = new StringBuilder();
		sb.append(get());
		advance();
		if (!isIdentifierPart(get()))
			throw new IllegalStateException("Invalid scalar identifier");
		sb.append(get());
		advance();
		char c;
		while (isIdentifierPart(c = get())) {
			sb.append(c);
			advance();
		}
		String content = sb.toString();
		TokenType type = content.charAt(1) == '_' ? TokenType.ID_PRIVATE_SCALAR : TokenType.ID_PUBLIC_SCALAR;
		return new Token(type, content, m);
	}

	private Token arrayIdentifier() {
		SourceMeta m = genMeta();
		StringBuilder sb = new StringBuilder();
		sb.append(get());
		advance();
		if (!isIdentifierPart(get()))
			throw new IllegalStateException("Invalid array identifier");
		sb.append(get());
		advance();
		char c;
		while (isIdentifierPart(c = get())) {
			sb.append(c);
			advance();
		}
		String content = sb.toString();
		TokenType type = content.charAt(1) == '_' ? TokenType.ID_PRIVATE_ARRAY : TokenType.ID_PUBLIC_ARRAY;
		return new Token(type, content, m);
	}

	private Token hashIdentifier() {
		SourceMeta m = genMeta();
		StringBuilder sb = new StringBuilder();
		sb.append(get());
		advance();
		if (!isIdentifierPart(get()))
			throw new IllegalStateException("Invalid hash identifier");
		sb.append(get());
		advance();
		char c;
		while (isIdentifierPart(c = get())) {
			sb.append(c);
			advance();
		}
		String content = sb.toString();
		TokenType type = content.charAt(1) == '_' ? TokenType.ID_PRIVATE_HASH : TokenType.ID_PUBLIC_HASH;
		return new Token(type, content, m);
	}

	private Token type() {
		SourceMeta m = genMeta();
		StringBuilder sb = new StringBuilder();
		sb.append(get());
		advance();
		char c;
		while (isLowercaseLetter(c = get())) {
			sb.append(c);
			advance();
		}
		TokenType type = getType(sb.toString());
		if (type == null)
			throw new IllegalStateException("Invalid type " + sb.toString());
		return new Token(type, sb.toString(), m);
	}
	
	private Token symbol() {
		SourceMeta meta = genMeta();
		StringBuilder sb = new StringBuilder();
		sb.append(get());
		advance();
		
		char c;
		while (isSymbolChar(c = get())) {
			sb.append(c);
			advance();
		}
		TokenType type = getSymbolType(sb.toString());
		while (type == null) {
			retract();
			sb.replace(sb.length() - 1, sb.length(), "");
			type = getSymbolType(sb.toString());
		}
		return new Token(type, sb.toString(), meta);
	}

	private Token charLiteral() {
		SourceMeta m = genMeta();
		advance();
		String content = stringLiteralContent();
		if (get() != '\'')
			throw new IllegalStateException("Invalid character literal");
		advance();
		return new Token(TokenType.CHARACTER_LITERAL, content, m);
	}
	
	private Token stringLiteral() {
		SourceMeta m = genMeta();
		advance();
		StringBuilder sb = new StringBuilder();
		while (get() != '"')
			sb.append(stringLiteralContent());
		advance();
		return new Token(TokenType.STRING_LITERAL, sb.toString(), m);
	}
	
	private String unicodeEscapeSequence(int numDigits) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < numDigits; i++) {
			char c = get();
			if (!isHexDigit(c))
				throw new IllegalStateException("Invalid unicode escape sequence");
			sb.append(c);
			advance();
		}
		return numDigits == 4 ? "\\u" + sb.toString() : "\\U" + sb.toString();
	}

	private String hexEscapeSequence() {
		StringBuilder sb = new StringBuilder();
		sb.append("\\x");
		if (!isHexDigit(get()))
			throw new IllegalStateException("Invalid hexadecimal escape sequence");
		sb.append(get());
		advance();
		char c;
		while (isHexDigit(c = get())) {
			sb.append(c);
			advance();
		}
		return sb.toString();
	}

	private String octalEscapeSequence() {
		int count = 1;
		char c;
		StringBuilder sb = new StringBuilder();
		sb.append("\\" + get());
		advance();
		while (count < 3 && isOctalDigit(c = get())) {
			sb.append(c);
			advance();
			count++;
		}
		return sb.toString();
	}
	
	private String stringEscapeSequence() {
		char c = get();
		if (c == 'b') {
			advance();
			return "\\b";
		} else if (c == 'f') {
			advance();
			return "\\f";
		} else if (c == 'n') {
			advance();
			return "\\n";
		} else if (c == 'r') {
			advance();
			return "\\r";
		} else if (c == 't') {
			advance();
			return "\\t";
		} else if (c == 'v') {
			advance();
			return "\\v";
		} else if (c == '\\') {
			advance();
			return "\\\\";
		} else if (c == '?') {
			advance();
			return "\\?";
		} else if (c == 'a') {
			advance();
			return "\\a";
		} else if (c == '\'') {
			advance();
			return "\\'";
		} else if (c == '"') {
			advance();
			return "\\\"";
		} else if (c == 'x') {
			advance();
			return hexEscapeSequence();
		} else if (c == 'u') {
			advance();
			return unicodeEscapeSequence(4);
		} else if (c == 'U') {
			advance();
			return unicodeEscapeSequence(8);
		} else if (isOctalDigit(c)) {
			return octalEscapeSequence();
		} else
			throw new IllegalStateException("Invalid escape sequence '\\" + c + "'");
	}
	
	private String stringLiteralContent() {
		char c = get();
		if (c == '\\') {
			advance();
			return stringEscapeSequence();
		} else {
			advance();
			return String.valueOf(c);
		}
	}
	
}
