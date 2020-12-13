package lpd_compiler;

public class Token {

	String      escopo;
	Simbolo     simbolo;
    String      lexema;
    int          linha;
    int         coluna;
    String        tipo;
	
    public Token(String escopo, Simbolo simbolo, String lexema, int linha, int coluna, String tipo) {
		super();
		this.escopo = escopo;
		this.simbolo = simbolo;
		this.lexema = lexema;
		this.linha = linha;
		this.coluna = coluna;
		this.tipo = tipo;
	}

	@Override
	public String toString() {
		return "Token [escopo=" + escopo + ", tipo=" + simbolo + ", lexema=" + lexema + ", linha=" + linha + ", coluna=" + coluna + ", tipo=" + tipo + "]";
	}    
}
