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
	
	
	/**
	 * ANALISADOR SINTÁTICO
	 * @return
	 * @throws IOException
	 */
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
						if (token.lexema.contentEquals("@") && errorFree == true) {
							System.out.println("Compilação executada com sucesso");
						}
						else {
							errorFree = erroToken(token, "@ = fim de arquivo");
						}
					}
					else {
						errorFree = erroToken(token, TipoToken.SPONTO.toString());
					}
				}
				else {
					errorFree = erroToken(token, TipoToken.SPONTO_VIRGULA.toString());
				}
			}
			else {
				errorFree = erroToken(token, TipoToken.SIDENTIFICADOR.toString());
			}
		}
		else {
			errorFree = erroToken(token, TipoToken.SPROGRAMA.toString());
		}
		return false;
	}
	
	
	/**
	 * <bloco>::= [<etapa de declaração de variáveis>]
	 * 			  [<etapa de declaração de sub-rotinas>]
	 *            <comandos>
	 */
	private void analisa_bloco() throws IOException {
		
		buscaToken();
		
		analisa_et_variaveis();
		analisa_subrotinas();
		analisa_comandos();
		
	}
	
	
	/**
	 * <etapa de declaração de variáveis>::= var <declaração de variáveis> ;
	 *                                           {<declaração de variáveis>;}
	 */
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
						errorFree = erroToken(token, TipoToken.SPONTO_VIRGULA.toString());
					}
				}
			}
			else {
				errorFree = erroToken(token, TipoToken.SIDENTIFICADOR.toString());
			}
		}
	}	
	
	
	/**
	 * <declaração de variáveis>::= <identificador> {, <identificador>} : <tipo>
	 */
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
						errorFree = erroToken(token, TipoToken.SVIRGULA.toString());
						errorFree = erroToken(token, TipoToken.SDOISPONTOS.toString());
					}
				}
				else {
					errorFree = erroDuplic(token);
				}
			}
			else {
				errorFree = erroToken(token, TipoToken.SIDENTIFICADOR.toString());
				break;
			}
		
		} while (!(token.tipo == TipoToken.SDOISPONTOS));
		
		buscaToken();
		analisa_tipo();
	}
	
	
	/**
	 * <tipo> ::= (inteiro | booleano)
	 */
	private void analisa_tipo() throws IOException {
		
		if (token.tipo != TipoToken.SINTEIRO && token.tipo != TipoToken.SBOOLEANO) {
			errorFree = erroToken(token, TipoToken.SINTEIRO.toString());
			errorFree = erroToken(token, TipoToken.SBOOLEANO.toString());
		}
		
		buscaToken();
	}
	
	
	/**
	 * <comandos>::= inicio
	 *				 	<comando>{;<comando>}[;]
	 *				 fim
	 */
	private void analisa_comandos()  throws IOException {
		
		if (token.tipo == TipoToken.SINICIO) {
			buscaToken();
			analisa_comando_simples();
			
			 do {
				
				if (token.tipo == TipoToken.SPONTO_VIRGULA) {
					buscaToken();
					
					if (token.tipo != TipoToken.SFIM && token.tipo != TipoToken.SINICIO) {
						analisa_comando_simples();
					}
					else if (token.tipo == TipoToken.SINICIO) {
						errorFree = erroToken(token, "Outra palavra reservada ou SIDENTIFICADOR");
						break;
					}
				}
				else {
					errorFree = erroToken(token, TipoToken.SPONTO_VIRGULA.toString());
					break;
				}
				
			} while (token.tipo != TipoToken.SFIM);
			 
			buscaToken();
		}
		else {
			errorFree = erroToken(token, TipoToken.SPONTO_VIRGULA.toString());
		}
	}
	
	
	/**
	 * <comando>::= (<atribuição_chprocedimento>|
	 *				<comando condicional> |
	 *				<comando enquanto> |
	 *				<comando leitura> |
	 *				<comando escrita> |
	 *				<comandos>)
	 */
	private void analisa_comando_simples() throws IOException {
		
		if (token.tipo == TipoToken.SIDENTIFICADOR) {
			token.tipo = TipoToken.SVAR;
			token.escopo = pilha.peek();
			if (pesquisa_ts(token)) {
				analisa_atrib_chprocedimento();
			}
			else {
				errorFree = erroDeclar(token);
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
	
	
	/**
	 * <atribuição_chprocedimento>::= (<comando atribuicao>|
	 *								  <chamada de procedimento ou função>)
	 */
	private void analisa_atrib_chprocedimento() throws IOException {
		
		buscaToken();
		
		if (token.tipo == TipoToken.SATRIBUICAO) {
			analisa_atribuicao();
		}
		else {
			analisa_ch_procedimento();
		}
	}
	
	
	/**
	 * <comando leitura> ::= leia ( <identificador> )
	 */
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
						errorFree = erroToken(token, TipoToken.SFECHA_PARENTESES.toString());
					}
				}
				else {
					errorFree = erroDeclar(token);
				}
			}
			else {
				errorFree = erroToken(token, TipoToken.SIDENTIFICADOR.toString());
			}
		}
		else {
			errorFree = erroToken(token, TipoToken.SABRE_PARENTESES.toString());
		}
	}
	
	
	/**
	 * <comando escrita> ::= escreva ( <identificador> )
	 */
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
						errorFree = erroToken(token, TipoToken.SFECHA_PARENTESES.toString());
					}
				}
				else {
					errorFree = erroDeclar(token);
				}
			}
			else {
				errorFree = erroToken(token, TipoToken.SIDENTIFICADOR.toString());
			}
		}
		else {
			errorFree = erroToken(token, TipoToken.SABRE_PARENTESES.toString());
		}
	}
	
	
	/**
	 * <comando enquanto> ::= enquanto <expressão> faca <comando>
	 */
	private void analisa_enquanto() throws IOException {
		
		buscaToken();
		analisa_expressao();
		
		if (token.tipo == TipoToken.SFACA) {
			buscaToken();
			analisa_comando_simples();
		}
		else {
			errorFree = erroToken(token, TipoToken.SFACA.toString());
		}
	}
	
	
	/**
	 * <comando condicional>::= se <expressão>
	 *							entao <comando>
	 *							[senao <comando>]
	 */
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
			errorFree = erroToken(token, TipoToken.SENTAO.toString());
		}
	}
	
	
	/**
	 * <etapa de declaração de sub-rotinas> ::= (<declaração de procedimento>;|
	 * 											<declaração de função>;)
	 * 											{<declaração de procedimento>;|
	 * 											<declaração de função>;}
	 */
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
				errorFree = erroToken(token, TipoToken.SPONTO_VIRGULA.toString());
			}
		}
	}
	
	
	/**
	 * <declaração de procedimento> ::= procedimento <identificador>;
	 *												 <bloco>
	 */
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
					errorFree = erroToken(token, TipoToken.SPONTO_VIRGULA.toString());
				}
			}
			else {
				errorFree = erroDuplic(token);
			}
		}
		else {
			errorFree = erroToken(token, TipoToken.SIDENTIFICADOR.toString());
		}
		pilha.pop();
	}
	
	
	/**
	 * <declaração de função> ::= funcao <identificador>: <tipo>;
	 *									 <bloco>
	 */
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
						errorFree = erroToken(token, TipoToken.SINTEIRO.toString());
						errorFree = erroToken(token, TipoToken.SBOOLEANO.toString());
					}
				}
				else {
					errorFree = erroToken(token, TipoToken.SDOISPONTOS.toString());
				}
			}
		}
		else {
			errorFree = erroToken(token, TipoToken.SIDENTIFICADOR.toString());
		}
		pilha.pop();
	}
	
	
	/**
	 * <expressão>::= <expressão simples> [<operador relacional><expressão simples>]
	 */
	private void analisa_expressao() throws IOException {
		
		analisa_expressao_simples();
		if (token.tipo == TipoToken.SMAIOR || token.tipo == TipoToken.SMAIORIG || token.tipo == TipoToken.SIG || token.tipo == TipoToken.SMENOR || token.tipo == TipoToken.SMENORIG || token.tipo == TipoToken.SDIF) {
			buscaToken();
			analisa_expressao_simples();		
		}
	}
	
	
	/**
	 * <expressão simples> ::= [ + | - ] <termo> {( + | - | ou) <termo> }
	 */
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
	
	
	/**
	 * <termo>::= <fator> {(* | div | e) <fator>}
	 */
	private void analisa_termo() throws IOException {
		
		analisa_fator();
		while (token.tipo == TipoToken.SMULT || token.tipo == TipoToken.SDIV || token.tipo == TipoToken.SE) {
			buscaToken();
			analisa_fator();
		}
	}
	
	
	/**
	 * <fator> ::= (<variável> |
	 *				<número> |
	 *				<chamada de função> |
	 *				(<expressão>) | 
	 *				verdadeiro | falso
	 *				nao <fator>)
	 * 
	 */
	private void analisa_fator() throws IOException {
		
		// <variável>
		if (token.tipo == TipoToken.SIDENTIFICADOR) {
			token.escopo = pilha.peek();
			Chave chave = new Chave(token.escopo, token.tipo, token.lexema);
			
			if (ts.containsKey(chave)) {
				if (ts.getAtributo(chave, "tipo") == TipoToken.SINTEIRO.toString() || ts.getAtributo(chave, "tipo") == TipoToken.SBOOLEANO.toString()) {
					analisa_ch_funcao();
				}
				else {
					buscaToken();
				}
			}
			else {
				errorFree = erroDeclar(token);
			}
		}
		
		// <número>
		else if (token.tipo == TipoToken.SNUMERO) {
			buscaToken();
		}
		
		// <chamada de função>
		else if (token.tipo == TipoToken.SFUNCAO) {
			
			// TODO
			
		}
		
		// ( <expressão> )
		else if (token.tipo == TipoToken.SABRE_PARENTESES) {
			buscaToken();
			analisa_expressao();
			
			if (token.tipo == TipoToken.SFECHA_PARENTESES) {
				buscaToken();
			}
			else {
				errorFree = erroToken(token, TipoToken.SFECHA_PARENTESES.toString());
			}
		}
		
		// <verdadeiro> | <falso>
		else if (token.lexema.contentEquals("verdadeiro") || token.lexema.contentEquals("falso")) {
			buscaToken();
		}
		
		// <não>
		else if (token.tipo == TipoToken.SNAO) {
			buscaToken();
			analisa_fator();
		}
		
		else {
			errorFree = erroToken(token, TipoToken.SVERDADEIRO.toString());
			errorFree = erroToken(token, TipoToken.SFALSO.toString());
		}
	}
	
	
	/**
	 * 
	 * <comando atribuicao>::= <identificador> := <expressão>
	 * 
	 */
	private void analisa_atribuicao() throws IOException {
		buscaToken();
		analisa_expressao();
	}
	
	
	/**
	 * <chamada de procedimento>::= <identificador>
	 */
	private void analisa_ch_procedimento() throws IOException {
		token.escopo = pilha.peek();
		Chave chave = new Chave(token.escopo, token.tipo, token.lexema);
		
		if (!ts.containsKey(chave)) {
			errorFree = erroDeclar(token);
		}
	}
	
	
	private void analisa_ch_funcao() throws IOException {
		
		// TODO
		
	}

	
	/**
	 * Pesquisa se existe um token na Tabela de Simbolos
	 */
	private boolean pesquisa_ts(Token token) {
		Chave chave = new Chave(token.escopo, token.tipo, token.lexema);
		if (ts.containsKey(chave)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	/**
	 * Insere um token na Tabela de Simbolos
	 */
	private void insere_ts(Token token) {
		Chave chave = new Chave(token.escopo, token.tipo, token.lexema);
		ts.addToken(chave, token);
		//ts.setAtributo(chave, "tipo", novoTipo.toString());
	}
	

/**
	private void analisa_comando()  throws IOException {
		
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
					erroToken(token, TipoToken.SPONTO_VIRGULA.toString());
					break;
				}
				buscaToken();
			}
		}
		else {
			erroToken(token, TipoToken.SPONTO_VIRGULA.toString());
		}
	}
*/
}

