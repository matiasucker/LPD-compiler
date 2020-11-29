package lpd_compiler;

import java.util.Objects;

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
	public int hashCode() {
		return Objects.hash(escopo, lexema, tipo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Chave other = (Chave) obj;
		return Objects.equals(escopo, other.escopo) && Objects.equals(lexema, other.lexema) && tipo == other.tipo;
	}

	@Override
	public String toString() {
		return "Simbolo [escopo=" + escopo +", tipo=" + tipo + ", lexema=" + lexema + "]";
	}
}
