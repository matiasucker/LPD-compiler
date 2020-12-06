package lpd_compiler;

import java.io.IOException;

public class PPR extends Parser {

	
	public PPR(String arquivo) throws IOException {
		super(arquivo);
		
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
				pilha.push(token.lexema);
				token.escopo = pilha.peek();
				Chave chave = new Chave(token.escopo, token.tipo, token.lexema);
				ts.addToken(chave, token);
				
				buscaToken();
				
				if (token.tipo == TipoToken.SPONTO_VIRGULA) {
					analisa_bloco();
					
					if (token.tipo == TipoToken.SPONTO) {
						buscaToken();
						if (token.lexema.contentEquals("@")) {
							System.out.println("Compilação executada com sucesso");
						}
						else {
							erroToken(token, "@ = fim de arquivo");
						}
					}
					else {
						erroToken(token, TipoToken.SPONTO.toString());
					}
				}
				else {
					erroToken(token, TipoToken.SPONTO_VIRGULA.toString());
				}
			}
			else {
				erroToken(token, TipoToken.SIDENTIFICADOR.toString());
			}
		}
		else {
			erroToken(token, TipoToken.SPROGRAMA.toString());
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
						erroToken(token, TipoToken.SPONTO_VIRGULA.toString());
					}
				}
			}
			else {
				erroToken(token, TipoToken.SIDENTIFICADOR.toString());
			}
		}
	}	
	
	
	private void analisa_variaveis() throws IOException {
		
		do {
			if (token.tipo == TipoToken.SIDENTIFICADOR) {
				token.tipo = TipoToken.SVAR;
				token.escopo = pilha.peek();
				if (!pesquisa_ts(token)) {
					insere_ts(token);
					buscaToken();
					
					if (token.tipo == TipoToken.SVIRGULA || token.tipo == TipoToken.SDOISPONTOS ) {
						
						if (token.tipo == TipoToken.SVIRGULA) {
							buscaToken();
						}
					}
					else {
						erroToken(token, TipoToken.SVIRGULA.toString());
						erroToken(token, TipoToken.SDOISPONTOS.toString());
					}
				}
				else {
					erroDuplic(token);
				}
			}
			else {
				erroToken(token, TipoToken.SIDENTIFICADOR.toString());
				break;
			}
		
		} while (!(token.tipo == TipoToken.SDOISPONTOS));
		
		buscaToken();
		analisa_tipo();
	}

	
	private void analisa_tipo() throws IOException {
		
		if (token.tipo != TipoToken.SINTEIRO && token.tipo != TipoToken.SBOOLEANO) {
			erroToken(token, TipoToken.SINTEIRO.toString());
			erroToken(token, TipoToken.SBOOLEANO.toString());
		}
		
		buscaToken();
	}


	private void analisa_comandos()  throws IOException {
		
		if (token.tipo == TipoToken.SINICIO) {
			buscaToken();
			analisa_comando_simples();
			
			if (token.tipo != TipoToken.SPONTO_VIRGULA) {
				erroToken(token, TipoToken.SPONTO_VIRGULA.toString());
			}
			
			//if (token.tipo != TipoToken.SFIM) {
				while (token.tipo != TipoToken.SFIM) {
					
					if (token.tipo == TipoToken.SPONTO_VIRGULA) {
						buscaToken();
						
						if (token.tipo != TipoToken.SFIM) {
							analisa_comando_simples();
						}
					}
					else if (token.tipo == TipoToken.SPONTO) {
						break;
					}
					else {
						erroToken(token, TipoToken.SPONTO_VIRGULA.toString());
						break;
					}
					buscaToken();
					if (token.lexema.contentEquals("@")) {
						break;
					}
				}
			//}
		}
		else {
			erroToken(token, TipoToken.SPONTO_VIRGULA.toString());
		}
	}
	
/**	
	private void analisa_comandos()  throws IOException {
		
		if (token.tipo == TipoToken.SINICIO) {
			buscaToken();
			
			if (token.tipo != TipoToken.SFIM) {
				analisa_comando_simples();
			
				while (token.tipo != TipoToken.SFIM) {
					
					if (token.tipo == TipoToken.SPONTO_VIRGULA) {
						buscaToken();
						
						if (token.tipo != TipoToken.SFIM) {
							analisa_comando_simples();
						}
						else {
							buscaToken();
							if (token.tipo != TipoToken.SPONTO) {
								erroToken(token, TipoToken.SPONTO.toString());
							}
						}
					}
					else if (token.tipo == TipoToken.SFIM) {
						buscaToken();
					}
					else {
						erroToken(token, TipoToken.SPONTO_VIRGULA.toString());
						break;
					}
				}
			}
			else {
				buscaToken();
				if (token.tipo != TipoToken.SPONTO_VIRGULA) {
					erroToken(token, TipoToken.SPONTO_VIRGULA.toString());
				}
			}
		}
		else {
			erroToken(token, TipoToken.SINICIO.toString());
		}
	}
*/
	
	private void analisa_comando_simples() throws IOException {
		
		if (token.tipo == TipoToken.SIDENTIFICADOR) {
			token.tipo = TipoToken.SVAR;
			token.escopo = pilha.peek();
			if (pesquisa_ts(token)) {
				analisa_atrib_chprocedimento();
			}
			else {
				erroDeclar(token);
			}
			
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
			analisa_ch_proced_funcao();
		}
	}
	
	
	private void analisa_leia() throws IOException {
		
		buscaToken();
		
		if (token.tipo == TipoToken.SABRE_PARENTESES) {
			buscaToken();
			
			if (token.tipo == TipoToken.SIDENTIFICADOR) {
				token.tipo = TipoToken.SVAR;
				token.escopo = pilha.peek();
				if (pesquisa_ts(token)) {
					buscaToken();
					
					if (token.tipo == TipoToken.SFECHA_PARENTESES) {
						buscaToken();
					}
					else {
						erroToken(token, TipoToken.SFECHA_PARENTESES.toString());
					}
				}
				else {
					erroDeclar(token);
				}
			}
			else {
				erroToken(token, TipoToken.SIDENTIFICADOR.toString());
			}
		}
		else {
			erroToken(token, TipoToken.SABRE_PARENTESES.toString());
		}
	}
	
	
	private void analisa_escreva() throws IOException {
		
		buscaToken();
		
		if (token.tipo == TipoToken.SABRE_PARENTESES) {
			buscaToken();
			
			if (token.tipo == TipoToken.SIDENTIFICADOR) {
				token.tipo = TipoToken.SVAR;
				token.escopo = pilha.peek();
				if (pesquisa_ts(token)) {
					buscaToken();
					
					if (token.tipo == TipoToken.SFECHA_PARENTESES) {
						buscaToken();
					}
					else {
						erroToken(token, TipoToken.SFECHA_PARENTESES.toString());
					}
				}
				else {
					erroDeclar(token);
				}
			}
			else {
				erroToken(token, TipoToken.SIDENTIFICADOR.toString());
			}
		}
		else {
			erroToken(token, TipoToken.SABRE_PARENTESES.toString());
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
			erroToken(token, TipoToken.SFACA.toString());
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
			erroToken(token, TipoToken.SENTAO.toString());
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
				erroToken(token, TipoToken.SPONTO_VIRGULA.toString());
			}
		}
	}
	
	
	private void analisa_declaracao_procedimento() throws IOException {
		
		buscaToken();
		
		if (token.tipo == TipoToken.SIDENTIFICADOR) {
			token.tipo = TipoToken.SPROCEDIMENTO;
			pilha.push(token.lexema);
			token.escopo = pilha.peek();
			
			if (!pesquisa_ts(token)) {
				insere_ts(token);
				
				buscaToken();
				
				if (token.tipo == TipoToken.SPONTO_VIRGULA) {
					analisa_bloco();
				}
				else {
					erroToken(token, TipoToken.SPONTO_VIRGULA.toString());
				}
			}
			else {
				erroDuplic(token);
			}
		}
		else {
			erroToken(token, TipoToken.SIDENTIFICADOR.toString());
		}
		pilha.pop();
	}
	
	
	private void analisa_declaracao_funcao() throws IOException {
		
		buscaToken();

		if (token.tipo == TipoToken.SIDENTIFICADOR) {
			token.tipo = TipoToken.SFUNCAO;
			pilha.push(token.lexema);
			token.escopo = pilha.peek();
			
			if (!pesquisa_ts(token)) {
				insere_ts(token);

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
						erroToken(token, TipoToken.SINTEIRO.toString());
						erroToken(token, TipoToken.SBOOLEANO.toString());
					}
				}
				else {
					erroToken(token, TipoToken.SDOISPONTOS.toString());
				}
			}
		}
		else {
			erroToken(token, TipoToken.SIDENTIFICADOR.toString());
		}
		pilha.pop();
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
			token.escopo = pilha.peek();
			Chave chave = new Chave(token.escopo, token.tipo, token.lexema);
			
			if (ts.containsKey(chave)) {
				if (ts.getAtributo(chave, "tipo") == TipoToken.SINTEIRO.toString() || ts.getAtributo(chave, "tipo") == TipoToken.SBOOLEANO.toString()) {
					analisa_ch_proced_funcao();
				}
				else {
					buscaToken();
				}
			}
			else {
				erroDeclar(token);
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
				erroToken(token, TipoToken.SFECHA_PARENTESES.toString());
			}
		}
		else if (token.lexema.contentEquals("verdadeiro") || token.lexema.contentEquals("falso")) {
			buscaToken();
		}
		else {
			erroToken(token, TipoToken.SVERDADEIRO.toString());
			erroToken(token, TipoToken.SFALSO.toString());
		}
	}
	

	private void analisa_atribuicao() throws IOException {
		buscaToken();
		analisa_expressao();
	}
	
	
	private void analisa_ch_proced_funcao() throws IOException {
		token.escopo = pilha.peek();
		Chave chave = new Chave(token.escopo, token.tipo, token.lexema);
		
		if (!ts.containsKey(chave)) {
			erroDeclar(token);
		}
	}

	
	private boolean pesquisa_ts(Token token) {
		Chave chave = new Chave(token.escopo, token.tipo, token.lexema);
		if (ts.containsKey(chave)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	private void insere_ts(Token token) {
		Chave chave = new Chave(token.escopo, token.tipo, token.lexema);
		ts.addToken(chave, token);
		//ts.setAtributo(chave, "tipo", novoTipo.toString());
	}	
}
