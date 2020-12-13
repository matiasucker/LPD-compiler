package lpd_compiler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public abstract class Parser {
	
	ArrayList<Token> lv;       // Lista de variáveis para atribuição do tipo inteiro ou booleano
	Stack<String> pilha;       // pilha para o escopo
	TS ts;                     // Tabela de Símbolos
	Lexer lexer;               // Análise Léxica
	Token token;               // Token conforme as regras da análise léxica
	Token variavelEsqAtrib;    // variável para atribuição de valor <variavel := valor>
	String escopo;             // escopo do token
	boolean errorFree;         // compilação
	
	public Parser(String arquivo) throws IOException {
		ts = new TS();
		pilha = new Stack<String>();
		lexer = new Lexer(arquivo);
		lv = new ArrayList<Token>();
		errorFree = true;
	}
	
	public abstract void parse();
	
	public Token buscaToken() throws IOException {
		token = lexer.buscaToken();
		return token;
	}
	
	public boolean erroToken(Token token, String simbolo) {
		System.out.println("LPD ERROR token: " + token.lexema + " | Linha: " + token.linha + " | Coluna: " + token.coluna);
		System.out.println("Simbolo de token esperado: " + simbolo);
		System.out.println("Simbolo de token usado: " + token.simbolo.toString() + "\n");
		return false;
	}
	
	
	public boolean erroTipo(Token token, String simbolo) {
		System.out.println("LPD ERROR tipo: " + token.lexema + " | Linha: " + token.linha + " | Coluna: " + token.coluna);
		System.out.println("Tipo de token esperado: " + simbolo);
		System.out.println("Tipo de token usado: " + token.simbolo.toString() + "\n");
		return false;
	}
	
	
	public boolean erroDuplic(Token token) {
		System.out.println("LPD ERROR token: " + token.lexema + " | Linha: " + token.linha + " | Coluna: " + token.coluna);
		System.out.println("Declaração duplicada do token: " + token.lexema + " | Simbolo: " + token.simbolo.toString() + "\n");
		return false;
	}

	public boolean erroDeclar(Token token) {
		System.out.println("LPD ERROR token: " + token.lexema + " | Linha: " + token.linha + " | Coluna: " + token.coluna);
		System.out.println("Não foi declarado do token: " + token.lexema + " | Simbolo: " + token.simbolo.toString() + "\n");	
		return false;
	}
}
