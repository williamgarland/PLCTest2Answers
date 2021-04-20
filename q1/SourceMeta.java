import java.nio.file.Path;

public class SourceMeta {

	private Path file;
	private int line;
	private int col;
	
	public SourceMeta(Path file, int line, int col) {
		this.file = file;
		this.line = line;
		this.col = col;
	}

	public Path getFile() {
		return file;
	}

	public int getLine() {
		return line;
	}

	public int getCol() {
		return col;
	}
	
	@Override
	public String toString() {
		return file.getFileName().toString() + ": " + line + ", " + col;
	}
	
}
