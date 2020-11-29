package lpd_compiler;

import java.io.IOException;

public class Main_PPR {

	public static void main(String[] args) throws IOException {
		
		PPR ppr = new PPR("src/prog1.lpd");
		ppr.parse();
	}
}
