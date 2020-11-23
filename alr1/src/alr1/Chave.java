package alr1;

public class Chave {
	
	String      escopo;
	TipoToken     tipo;
    String      lexema;
	
    public Chave(String escopo, TipoToken tipo, String lexema) {
		super();
		this.escopo = escopo;
		this.tipo = tipo;
		this.lexema = lexema;
	}

	@Override
	public String toString() {
		return "Simbolo [escopo=" + escopo +", tipo=" + tipo + ", lexema=" + lexema + "]";
	}    
}
