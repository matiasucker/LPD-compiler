package alr1;

import java.util.Hashtable;

public class TS {
	
	/**
	 * Tabela de simbolos implementado em Hashtable <chave, valor>
	 */
	Hashtable<Chave, Token> ts;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ts == null) ? 0 : ts.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TS other = (TS) obj;
		if (ts == null) {
			if (other.ts != null)
				return false;
		} else if (!ts.equals(other.ts))
			return false;
		return true;
	}

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
		
		if (atributo.contentEquals("escopo")) token.escopo = valor;
		else if (atributo.contentEquals("lexema")) token.lexema = valor;
		else if (atributo.contentEquals("tipo")) token.tipo = TipoToken.valueOf(valor);
		else if (atributo.contentEquals("linha")) token.linha = Integer.valueOf(valor);
		else if (atributo.contentEquals("coluna")) token.coluna = Integer.valueOf(valor);
		else System.out.println("Erro, atributo nao encontrado");
	}
	
	// Metodo busca atributo 
	public String getAtributo (Chave chave, String atributo) {
		
		Token token = ts.get(chave);
		
		if (atributo.contentEquals("escopo")) return chave.escopo;
		else if (atributo.contentEquals("lexema")) return token.lexema;
		else if (atributo.contentEquals("tipo")) return token.tipo.toString();
		else if (atributo.contentEquals("linha")) return String.valueOf(token.linha);
		else if (atributo.contentEquals("coluna")) return String.valueOf(token.coluna);
		else return "Erro, atributo nao encontrado";
	}
}
