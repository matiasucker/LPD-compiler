package lpd_compiler;

import java.util.Objects;

public class Chave {
	
	String      escopo;
	Simbolo     simbolo;
    String      lexema;
	
    public Chave(String escopo, Simbolo simbolo, String lexema) {
		super();
		this.escopo = escopo;
		this.simbolo = simbolo;
		this.lexema = lexema;
	}

	@Override
	public int hashCode() {
		return Objects.hash(escopo, lexema, simbolo);
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
		return Objects.equals(escopo, other.escopo) && Objects.equals(lexema, other.lexema) && simbolo == other.simbolo;
	}

	@Override
	public String toString() {
		return "Simbolo [escopo=" + escopo +", tipo=" + simbolo + ", lexema=" + lexema + "]";
	}
}
