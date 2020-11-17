package alr1;

import java.io.IOException;

public abstract class Parser {

	TS ts;
	Lexer3 lexer;
	Token t;	
	
	public Parser(TS ts, Lexer3 lexer, Token t) {
		super();
		this.ts = ts;
		this.lexer = lexer;
		this.t = t;
	}

	
	public abstract void parse();
	
	
	t = lexer.buscaToken();
	
	
}
