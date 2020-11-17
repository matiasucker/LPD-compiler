package alr1;

public class Token {

	TipoToken     tipo;
    String      lexema;
    int          linha;
    int         coluna;
	
    public Token(TipoToken tipo, String lexema, int linha, int coluna) {
		super();
		this.tipo = tipo;
		this.lexema = lexema;
		this.linha = linha;
		this.coluna = coluna;
	}

	@Override
	public String toString() {
		return "Token [tipo=" + tipo + ", lexema=" + lexema + ", linha=" + linha + ", coluna=" + coluna + "]";
	}    
}
