import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LexerMain {

	public static void main(String[] args) {
		Path input = Paths.get(args[0]);
		String data = "";
		try {
			data = Files.readString(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Lexer lexer = new Lexer(input, data);
		List<Token> tokens = new ArrayList<>();
		while (lexer.hasNext()) {
			tokens.add(lexer.nextToken());
		}
		System.out.println(tokens);
	}
	
}
