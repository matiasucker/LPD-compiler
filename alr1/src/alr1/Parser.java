package alr1;

import java.io.IOException;

public abstract class Parser {

	TS ts;
	Lexer lexer;
	Token token;	
	
	public Parser() {
		ts = new TS();
		lexer = new Lexer();
	}

	public abstract void parse();
	
	public Token buscaToken() throws IOException {
		token = lexer.buscaToken();
		return token;
	}
	
	
}
