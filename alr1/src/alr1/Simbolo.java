package alr1;

public class Simbolo {

	String      escopo;
	TipoToken     tipo;
    String      lexema;
    int          linha;
    int         coluna;
    
    public Simbolo(String escopo, TipoToken tipo, String lexema, int linha, int coluna) {
		super();
		this.escopo = escopo;
		this.tipo = tipo;
		this.lexema = lexema;
		this.linha = linha;
		this.coluna = coluna;
	}

	@Override
	public String toString() {
		return "[escopo=" + escopo + ", tipo=" + tipo + ", lexema=" + lexema + ", linha=" + linha + ", coluna=" + coluna + "]" + "\n";
	}

}
