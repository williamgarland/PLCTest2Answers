

public class Token {

	private TokenType type;
	private String data;
	private SourceMeta meta;
	
	public Token(TokenType type, String data, SourceMeta meta) {
		this.type = type;
		this.data = data;
		this.meta = meta;
	}

	public TokenType getType() {
		return type;
	}

	public String getData() {
		return data;
	}

	public SourceMeta getMeta() {
		return meta;
	}
	
	@Override
	public String toString() {
		return type.toString() + "[" + meta + "]: " + data;
	}
	
}
