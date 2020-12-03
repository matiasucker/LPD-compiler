package lpd_compiler;

import java.io.IOException;

public abstract class Parser {

	TS ts;
	Lexer lexer;
	Token token;	
	
	public Parser() {
		ts = new TS();
		lexer = new Lexer(ts);
	}

	public abstract void parse();
	
	public Token buscaToken() throws IOException {
		token = lexer.buscaToken();
		return token;
	}
	
	public boolean erroToken(Token token, String simbolo) {
		System.out.println("ERRO no token: " + token.lexema + " | Linha: " + token.linha + " | Coluna: " + token.coluna);
		System.out.println("Simbolo esperado: " + simbolo);
		return false;
	}
	
	public boolean erroDuplic(Token token) {
		System.out.println("ERRO no token: " + token.lexema + " | Linha: " + token.linha + " | Coluna: " + token.coluna);
		System.out.println("ERRO, declaração duplicada: " + token.tipo.toString());
		return false;
	}

	public boolean erroDeclar(Token token) {
		System.out.println("ERRO no token: " + token.lexema + " | Linha: " + token.linha + " | Coluna: " + token.coluna);
		System.out.println("ERRO, token não declarado: " + token.tipo.toString());	
		return false;
	}
}
