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
		
		buscaToken();
		if (token.tipo == TipoToken.SPROGRAMA) {
			buscaToken();
			
			if (token.tipo == TipoToken.SIDENTIFICADOR) {
				buscaToken();
				
				if (token.tipo == TipoToken.SPONTO_E_VIRGULA) {
					analisa_bloco();
					
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

	private void analisa_bloco() throws IOException {
		// TODO Auto-generated method stub
		
		buscaToken();
		
		analisa_et_variaveis();
		analisa_subrotinas();
		analisa_comandos();
		
	}

	
	private void analisa_et_variaveis() throws IOException {
		// TODO Auto-generated method stub
		
		if (token.tipo == TipoToken.SVAR) {
			buscaToken();
			
			if (token.tipo == TipoToken.SIDENTIFICADOR) {
				
				while (token.tipo == TipoToken.SIDENTIFICADOR) {
					analisa_variaveis();
					
					if (token.tipo == TipoToken.SPONTO_E_VIRGULA) {
						buscaToken();
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
				buscaToken();
				
				if (token.tipo == TipoToken.SVIRGULA || token.tipo == TipoToken.SDOISPONTOS ) {
					
					if (token.tipo == TipoToken.SVIRGULA) {
						buscaToken();
						
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
			
		} while (token.tipo == TipoToken.SDOISPONTOS);
		
		buscaToken();
		analisa_tipo();
	}

	
	private void analisa_tipo() throws IOException {
		// TODO Auto-generated method stub
		
		if (token.tipo != TipoToken.SINTEIRO || token.tipo != TipoToken.SBOOLEANO) {
			System.out.println("ERRO");
		}
		buscaToken();
	}

	
	private void analisa_comandos()  throws IOException {
		// TODO Auto-generated method stub
		
		if (token.tipo == TipoToken.SINICIO) {
			buscaToken();
			analisa_comando_simples();
			
			while (token.tipo != TipoToken.SFIM) {
				
				if (token.tipo == TipoToken.SPONTO_E_VIRGULA) {
					buscaToken();
					
					if (token.tipo != TipoToken.SFIM) {
						analisa_comando_simples();
					}
				}
				else {
					System.out.println("ERRO");
				}
			buscaToken();
			}
		}
		else {
			System.out.println("ERRO");
		}
		
	}	
	
	
	private void analisa_comando_simples() throws IOException {
		// TODO Auto-generated method stub
		
		if (token.tipo == TipoToken.SIDENTIFICADOR) {
			analisa_atrib_chprocedimento();
		}
		else if (token.tipo == TipoToken.SSE) {
			analisa_se();
		}
		else if (token.tipo == TipoToken.SENQUANTO) {
			analisa_enquanto();
		}
		else if (token.tipo == TipoToken.SLEIA) {
			analisa_leia();
		}
		else if (token.tipo == TipoToken.SESCREVA) {
			analisa_escreva();
		}
		else {
			analisa_comandos();
		}
	}


	private void analisa_atrib_chprocedimento() throws IOException {
		// TODO Auto-generated method stub
		
		buscaToken();
		
		if (token.tipo == TipoToken.SATRIBUICAO) {
			analisa_atribuicao();
		}
		else {
			chamada_procedimento();
		}
	}
	
	
	private void analisa_leia() throws IOException {
		// TODO Auto-generated method stub
		
		buscaToken();
		
		if (token.tipo == TipoToken.SABRE_PARENTESIS) {
			buscaToken();
			
			if (token.tipo == TipoToken.SIDENTIFICADOR) {
				buscaToken();
				
				if (token.tipo == TipoToken.SFECHA_PARENTESIS) {
					buscaToken();
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
	

	private void analisa_escreva() throws IOException {
		// TODO Auto-generated method stub
		
		buscaToken();
		
		if (token.tipo == TipoToken.SABRE_PARENTESIS) {
			buscaToken();
			
			if (token.tipo == TipoToken.SIDENTIFICADOR) {
				buscaToken();
				
				if (token.tipo == TipoToken.SFECHA_PARENTESIS) {
					buscaToken();
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
	
	
	private void analisa_enquanto() throws IOException {
		// TODO Auto-generated method stub
		
		buscaToken();
		analisa_expressao();
		
		if (token.tipo == TipoToken.SFACA) {
			buscaToken();
			analisa_comando_simples();
		}
		else {
			System.out.println("ERRO");
		}
	}
	
	
	private void analisa_se() throws IOException {
		// TODO Auto-generated method stub
		
		buscaToken();
		analisa_expressao();
		
		if (token.tipo == TipoToken.SENTAO) {
			buscaToken();
			analisa_comando_simples();
			
			if (token.tipo == TipoToken.SSENAO) {
				buscaToken();
				analisa_comando_simples();
			}
		}
		else {
			System.out.println("ERRO");
		}
	}
	
	
	private void analisa_subrotinas() throws IOException {
		// TODO Auto-generated method stub
		
		while (token.tipo == TipoToken.SPROCEDIMENTO || token.tipo == TipoToken.SFUNCAO) {
			
			if (token.tipo == TipoToken.SPROCEDIMENTO) {
				analisa_declaracao_procedimento();
			}
			else {
				analisa_declaracao_funcao();
			}
			if (token.tipo == TipoToken.SPONTO_E_VIRGULA) {
				buscaToken();
			}
			else {
				System.out.println("ERRO");
			}
		}
	}
	
	
	private void analisa_declaracao_procedimento() throws IOException {
		// TODO Auto-generated method stub
		
		buscaToken();
		
		if (token.tipo == TipoToken.SIDENTIFICADOR) {
			buscaToken();
			
			if (token.tipo == TipoToken.SPONTO_E_VIRGULA) {
				analisa_bloco();
			}
			else {
				System.out.println("ERRO");
			}
		}
		else {
			System.out.println("ERRO");
		}
	}
	
	
	private void analisa_declaracao_funcao() throws IOException {
		// TODO Auto-generated method stub
		
		buscaToken();
		
		if (token.tipo == TipoToken.SIDENTIFICADOR) {
			buscaToken();
			
			if (token.tipo == TipoToken.SDOISPONTOS) {
				buscaToken();
				
				if (token.tipo == TipoToken.SINTEIRO || token.tipo == TipoToken.SBOOLEANO) {
					buscaToken();
					
					if (token.tipo == TipoToken.SPONTO_E_VIRGULA) {
						analisa_bloco();
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


	private void analisa_expressao() throws IOException {
		// TODO Auto-generated method stub
		
		analisa_expressao_simples();
		if (token.tipo == TipoToken.SMAIOR || token.tipo == TipoToken.SMAIORIGUAL || token.tipo == TipoToken.SIGUAL || token.tipo == TipoToken.SMENOR || token.tipo == TipoToken.SMENORIGUAL || token.tipo == TipoToken.SDIFERENTEDE) {
			buscaToken();
			analisa_expressao_simples();		
		}
	}
	
	
	private void analisa_expressao_simples() throws IOException {
		// TODO Auto-generated method stub
		
		if (token.tipo == TipoToken.SMAIS || token.tipo == TipoToken.SMENOS) {
			buscaToken();
		}
		analisa_termo();
			
		while ((token.tipo == TipoToken.SMAIS) || (token.tipo == TipoToken.SMENOS) || (token.tipo == TipoToken.SOU)) {
			buscaToken();
			analisa_termo();
		}
	}
	
	
	private void analisa_termo() throws IOException {
		// TODO Auto-generated method stub
		
		analisa_fator();
		while (token.tipo == TipoToken.SMULTIPLICACAO || token.tipo == TipoToken.SDIVISAO || token.tipo == TipoToken.SE) {
			buscaToken();
			analisa_fator();
		}
	}
	
	
	private void analisa_fator() throws IOException {
		// TODO Auto-generated method stub
		
		if (token.tipo == TipoToken.SIDENTIFICADOR) {
			
			
			//analisa_chamada_funcao();
		}
		else if (token.tipo == TipoToken.SNUMERO) {
			buscaToken();
		}
		else if (token.tipo == TipoToken.SNAO) {
			buscaToken();
			analisa_fator();
		}
		else if (token.tipo == TipoToken.SABRE_PARENTESIS) {
			buscaToken();
			analisa_expressao();
			
			if (token.tipo == TipoToken.SFECHA_PARENTESIS) {
				buscaToken();
			}
			else {
				System.out.println("ERRO");
			}
		}
		else if (token.lexema.contentEquals("verdadeiro") || token.lexema.contentEquals("falso")) {
			buscaToken();
		}
		else {
			System.out.println("ERRO");
		}
	}
	

	private void analisa_atribuicao() throws IOException {
		// TODO Auto-generated method stub
		
		buscaToken();
		analisa_expressao();
	}
	
	
	private void chamada_procedimento() throws IOException {
		// TODO Auto-generated method stub
		
	}


	private void chamada_funcao() throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	
}
