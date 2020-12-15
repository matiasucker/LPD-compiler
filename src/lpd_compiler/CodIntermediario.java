package lpd_compiler;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class CodIntermediario {
	
	int temp;
	
	String codigo;
	
	
	public CodIntermediario(int t, String cod) {
		
		temp = t;
		codigo = cod;
		
		
		geraLLVMIR();
		
	}
	
	public String geraPreambulo() {
		String print;
		print  = "@.str = private unnamed_addr constant [3 x i8] c\"%d\00\", align 1 \n";
		print += "; Function Attrs: noinline nounwind optnone uwtable \n";
		print += "define dso_local i32 @main() #0 { \n";
		return print;
	}
	
	public String geraCodigo() {
		
		return codigo;
	}
	
	public String geraEpilogo() {
		String print;
		print = "}\n";
		print += "declare dso_local i32 @printf(i8*, ...) #1\n";
		
		return print;
	}
	
	public void geraLLVMIR() {
		//String preambulo = ";Preâmbulo\n";
		//String epilogo = ";Epílogo\n";
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("saida.ll"));
			writer.write(geraPreambulo());
			writer.write(geraCodigo());
			writer.write(geraEpilogo());
			writer.close();
		} catch (Exception e) {
			System.out.println(e);
		};
	}
}
