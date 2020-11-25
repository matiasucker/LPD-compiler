package alr1;

import java.io.IOException;

public class PPR extends Parser {

	
	public PPR(String arquivo) throws IOException {
		super();
		lexer.analisa(arquivo);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void parse() {
		try {
			analisaPrograma();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public boolean analisaPrograma() throws IOException {
		
		token = buscaToken();
		if (token.tipo == TipoToken.SPROGRAMA) {
			
			token = buscaToken();
			if (token.tipo == TipoToken.SIDENTIFICADOR) {
				
				token = buscaToken();
				if (token.tipo == TipoToken.SPONTO_E_VIRGULA) {
					
					analisaBloco();
					
					if (token.tipo == TipoToken.SPONTO) {
						
						if (lexer.ch == '@') {
							
							System.out.println("sucesso");
						}
						else {
							
							System.out.println("ERRO");
						}
					}
					else {
						
						System.out.println("ERRO");
					}
					
				}
				else {
					
					System.out.println("ERRO");
				}
				
			}
			else {
				
				System.out.println("ERRO");
			}
			
		}
		else {
			
			System.out.println("ERRO");
		}
		
		return false;
		
	}

	private void analisaBloco() throws IOException {
		// TODO Auto-generated method stub
		
		token = buscaToken();
		
		analisa_et_variaveis();
		analisa_subrotinas();
		analisa_comandos();
		
	}

	
	private void analisa_et_variaveis() throws IOException {
		// TODO Auto-generated method stub
		
		if (token.tipo == TipoToken.SVAR) {
			
			token = buscaToken();
			if (token.tipo == TipoToken.SIDENTIFICADOR) {
				
				while (token.tipo == TipoToken.SIDENTIFICADOR) {
					
					analisa_variaveis();
					if (token.tipo == TipoToken.SPONTO_E_VIRGULA) {
						token = buscaToken();
					}
					else {
						
						System.out.println("ERRO");
					}
				}
			}
			else {
				
				System.out.println("ERRO");
			}
		}
		
		
		
	}	
	
	
	private void analisa_variaveis() throws IOException {
		// TODO Auto-generated method stub
		
		do {
			if (token.tipo == TipoToken.SIDENTIFICADOR) {
				
				token = buscaToken();
				if (token.tipo == TipoToken.SVIRGULA || token.tipo == TipoToken.SDOISPONTOS ) {
					
					if (token.tipo == TipoToken.SVIRGULA) {
						
						token = buscaToken();
						if (token.tipo == TipoToken.SDOISPONTOS) {
							
							System.out.println("ERRO");
						}
					}
				}
				else {
					
					System.out.println("ERRO");
				}
				
			}
			else {
				
				System.out.println("ERRO");
			}
			
		} while (token.tipo != TipoToken.SDOISPONTOS);
		
		token = buscaToken();
		analisa_tipo();
	}

	
	private void analisa_tipo() {
		// TODO Auto-generated method stub
		
		
	}

	private void analisa_subrotinas() throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	
	private void analisa_comandos()  throws IOException {
		// TODO Auto-generated method stub
		
	}




	
	
	
}
