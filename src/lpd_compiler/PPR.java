package lpd_compiler;

import java.io.IOException;

public class PPR extends Parser {

	
	public PPR(String arquivo) throws IOException {
		super();
		lexer.analisa(arquivo);
	}
	
	@Override
	public void parse() {
		try {
			analisaPrograma();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean analisaPrograma() throws IOException {
		
		buscaToken();
		if (token.tipo == TipoToken.SPROGRAMA) {
			buscaToken();
			
			if (token.tipo == TipoToken.SIDENTIFICADOR) {
				insere_tabela();
				buscaToken();
				
				if (token.tipo == TipoToken.SPONTO_VIRGULA) {
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
		
		buscaToken();
		
		analisa_et_variaveis();
		analisa_subrotinas();
		analisa_comandos();
		
	}

	
	private void analisa_et_variaveis() throws IOException {
		
		if (token.tipo == TipoToken.SVAR) {
			buscaToken();
			
			if (token.tipo == TipoToken.SIDENTIFICADOR) {
				
				while (token.tipo == TipoToken.SIDENTIFICADOR) {
					analisa_variaveis();
					
					if (token.tipo == TipoToken.SPONTO_VIRGULA) {
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
		
		do {
			if (token.tipo == TipoToken.SIDENTIFICADOR) {
				if (!pesquisa_duplicvar_tabela()) {
					insere_tabela();
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
			}
			else {
				System.out.println("ERRO");
			}
			
		} while (token.tipo == TipoToken.SDOISPONTOS);
		
		buscaToken();
		analisa_tipo();
	}

	
	private void analisa_tipo() throws IOException {
		
		if (token.tipo != TipoToken.SINTEIRO || token.tipo != TipoToken.SBOOLEANO) {
			System.out.println("ERRO");
		}
		buscaToken();
	}

	
	private void analisa_comandos()  throws IOException {
		
		if (token.tipo == TipoToken.SINICIO) {
			buscaToken();
			analisa_comando_simples();
			
			while (token.tipo != TipoToken.SFIM) {
				
				if (token.tipo == TipoToken.SPONTO_VIRGULA) {
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
		
		buscaToken();
		
		if (token.tipo == TipoToken.SATRIBUICAO) {
			analisa_atribuicao();
		}
		else {
			chamada_procedimento();
		}
	}
	
	
	private void analisa_leia() throws IOException {
		
		buscaToken();
		
		if (token.tipo == TipoToken.SABRE_PARENTESES) {
			buscaToken();
			
			if (token.tipo == TipoToken.SIDENTIFICADOR) {
				buscaToken();
				
				if (token.tipo == TipoToken.SFECHA_PARENTESES) {
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
		
		buscaToken();
		
		if (token.tipo == TipoToken.SABRE_PARENTESES) {
			buscaToken();
			
			if (token.tipo == TipoToken.SIDENTIFICADOR) {
				buscaToken();
				
				if (token.tipo == TipoToken.SFECHA_PARENTESES) {
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
		
		while (token.tipo == TipoToken.SPROCEDIMENTO || token.tipo == TipoToken.SFUNCAO) {
			
			if (token.tipo == TipoToken.SPROCEDIMENTO) {
				analisa_declaracao_procedimento();
			}
			else {
				analisa_declaracao_funcao();
			}
			if (token.tipo == TipoToken.SPONTO_VIRGULA) {
				buscaToken();
			}
			else {
				System.out.println("ERRO");
			}
		}
	}
	
	
	private void analisa_declaracao_procedimento() throws IOException {
		
		buscaToken();
		
		if (token.tipo == TipoToken.SIDENTIFICADOR) {
			buscaToken();
			
			if (token.tipo == TipoToken.SPONTO_VIRGULA) {
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
		
		buscaToken();
		
		if (token.tipo == TipoToken.SIDENTIFICADOR) {
			buscaToken();
			
			if (token.tipo == TipoToken.SDOISPONTOS) {
				buscaToken();
				
				if (token.tipo == TipoToken.SINTEIRO || token.tipo == TipoToken.SBOOLEANO) {
					buscaToken();
					
					if (token.tipo == TipoToken.SPONTO_VIRGULA) {
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
		
		analisa_expressao_simples();
		if (token.tipo == TipoToken.SMAIOR || token.tipo == TipoToken.SMAIORIG || token.tipo == TipoToken.SIG || token.tipo == TipoToken.SMENOR || token.tipo == TipoToken.SMENORIG || token.tipo == TipoToken.SDIF) {
			buscaToken();
			analisa_expressao_simples();		
		}
	}
	
	
	private void analisa_expressao_simples() throws IOException {
		
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
		
		analisa_fator();
		while (token.tipo == TipoToken.SMULT || token.tipo == TipoToken.SDIV || token.tipo == TipoToken.SE) {
			buscaToken();
			analisa_fator();
		}
	}
	
	
	private void analisa_fator() throws IOException {
		
		if (token.tipo == TipoToken.SIDENTIFICADOR) {
			Chave chave = new Chave(token.escopo, token.tipo, token.lexema);
			if (ts.containsKey(chave)) {
				if (ts.getAtributo(chave, "tipo") == TipoToken.SINTEIRO.toString() || ts.getAtributo(chave, "tipo") == TipoToken.SBOOLEANO.toString()) {
					analisa_chamada_funcao();
				}
				else {
					buscaToken();
				}
			}
			else {
				System.out.println("ERRO");
			}
		}
		else if (token.tipo == TipoToken.SNUMERO) {
			buscaToken();
		}
		else if (token.tipo == TipoToken.SNAO) {
			buscaToken();
			analisa_fator();
		}
		else if (token.tipo == TipoToken.SABRE_PARENTESES) {
			buscaToken();
			analisa_expressao();
			
			if (token.tipo == TipoToken.SFECHA_PARENTESES) {
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
		buscaToken();
		analisa_expressao();
	}
	
	
	private void chamada_procedimento() throws IOException {
		Chave chave = new Chave(token.escopo, token.tipo, token.lexema);
		if (!ts.containsKey(chave)) {
			System.out.println("ERRO");
		}
	}


	private void analisa_chamada_funcao() throws IOException {
		Chave chave = new Chave(token.escopo, token.tipo, token.lexema);
		if (!ts.containsKey(chave)) {
			System.out.println("ERRO");
		}	
	}
	
	private void insere_tabela() {
		Chave chave = new Chave(token.escopo, token.tipo, token.lexema);
		ts.addToken(chave, token);
	}
}
