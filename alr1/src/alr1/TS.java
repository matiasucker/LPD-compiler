package alr1;

import java.util.Hashtable;

public class TS {
	
	/**
	 * Tabela de simbolos implementado em Hashtable <chave, valor>
	 */
	Hashtable<Chave, Token> ts = new Hashtable<Chave, Token>();
	
	// Metodo adiciona token
	public void addToken (Chave chave, Token token) {
		
		ts.put(chave, token);
	}
	
	// Metodo busca token
	public Token getToken (Chave chave) {
			
		return ts.get(chave);
	}
	
	// Metodo set atributo
	public void setAtributo (Chave chave, String atributo, String valor) {
		
		Token token = ts.get(chave);
		
		if (atributo.contentEquals("escopo")) chave.escopo = valor;
		else if (atributo.contentEquals("lexema")) {
			chave.lexema = valor;
			token.lexema = valor;
		}
		else System.out.println("Erro, atributo nao encontrado");
		
		ts.replace(chave, token);
		
	}
	
	// Metodo busca atributo 
	public String getAtributo (Chave chave, String atributo) {
		
		Token token = ts.get(chave);
		
		if (atributo.contentEquals("escopo")) return chave.escopo;
		else if (atributo.contentEquals("lexema")) return token.lexema;
		else return "Erro, atributo nao encontrado";
	}
}
