

import java.util.List;

public class BlockNode extends Node {

	private List<Node> content;

	public BlockNode(List<Node> content) {
		this.content = content;
	}

	public List<Node> getContent() {
		return content;
	}
	
}
