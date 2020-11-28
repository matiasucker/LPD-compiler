package alr1;

public class Token {

	String      escopo;
	TipoToken     tipo;
    String      lexema;
    int          linha;
    int         coluna;
	
    public Token(String escopo, TipoToken tipo, String lexema, int linha, int coluna) {
		super();
		this.escopo = escopo;
		this.tipo = tipo;
		this.lexema = lexema;
		this.linha = linha;
		this.coluna = coluna;
	}

	@Override
	public String toString() {
		return "Token [escopo=" + escopo + ", tipo=" + tipo + ", lexema=" + lexema + ", linha=" + linha + ", coluna=" + coluna + "]";
	}    
}
