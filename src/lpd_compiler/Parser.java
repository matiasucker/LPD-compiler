package lpd_compiler;

import java.io.IOException;
import java.util.Stack;

public abstract class Parser {
	
	TS ts;
	Stack<String> pilha;
	Lexer lexer;
	Token token;
	String escopo;
	boolean errorFree;
	
	public Parser(String arquivo) throws IOException {
		ts = new TS();
		pilha = new Stack<String>();
		lexer = new Lexer(arquivo);
		errorFree = true;
	}
	
	public abstract void parse();
	
	public Token buscaToken() throws IOException {
		token = lexer.buscaToken();
		return token;
	}
	
	public boolean erroToken(Token token, String simbolo) {
		System.out.println("LPD ERROR token: " + token.lexema + " | Linha: " + token.linha + " | Coluna: " + token.coluna);
		System.out.println("Tipo do token esperado: " + simbolo);
		System.out.println("Tipo do token usado: " + token.tipo.toString() + "\n");
		return false;
	}
	
	public boolean erroDuplic(Token token) {
		System.out.println("LPD ERROR token: " + token.lexema + " | Linha: " + token.linha + " | Coluna: " + token.coluna);
		System.out.println("Declaração duplicada do token: " + token.lexema + " | Tipo: " + token.tipo.toString() + "\n");
		return false;
	}

	public boolean erroDeclar(Token token) {
		System.out.println("LPD ERROR token: " + token.lexema + " | Linha: " + token.linha + " | Coluna: " + token.coluna);
		System.out.println("Não foi declarado o token: " + token.lexema + " | Tipo: " + token.tipo.toString() + "\n");	
		return false;
	}
}
