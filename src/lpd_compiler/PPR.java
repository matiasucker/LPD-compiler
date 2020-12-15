package lpd_compiler;

import java.io.IOException;

public class PPR extends Parser {

	
	public PPR(String arquivo) throws IOException {
		super(arquivo);
		
	}
	
	@Override
	public String parse() {
		try {
			analisaPrograma();
		} catch (IOException e) {
			e.printStackTrace();
		}
		salvaCodigo();
		return codigo;
	}
	
	public void salvaCodigo() {
		CodIntermediario codIntermediario = new CodIntermediario(temp, codigo);
		codIntermediario.geraLLVMIR();
	}
	
	/**
	 * ANALISADOR SINTÁTICO
	 * @return
	 * @throws IOException
	 */
	public boolean analisaPrograma() throws IOException {
		
		buscaToken();
		if (token.simbolo == Simbolo.SPROGRAMA) {
			buscaToken();
			
			if (token.simbolo == Simbolo.SIDENTIFICADOR) {
				pilha.push(token.lexema);
				token.escopo = pilha.peek();
				Chave chave = new Chave(token.escopo, token.simbolo, token.lexema);
				ts.addToken(chave, token);
				
				buscaToken();
				
				if (token.simbolo == Simbolo.SPONTO_VIRGULA) {
					analisa_bloco();
					
					if (token.simbolo == Simbolo.SPONTO) {
						buscaToken();
						if (token.lexema.contentEquals("@") && errorFree == true) {
							System.out.println("Compilação executada com sucesso");
						}
						else {
							errorFree = erroToken(token, "@ = fim de arquivo");
						}
					}
					else {
						errorFree = erroToken(token, Simbolo.SPONTO.toString());
					}
				}
				else {
					errorFree = erroToken(token, Simbolo.SPONTO_VIRGULA.toString());
				}
			}
			else {
				errorFree = erroToken(token, Simbolo.SIDENTIFICADOR.toString());
			}
		}
		else {
			errorFree = erroToken(token, Simbolo.SPROGRAMA.toString());
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
		
		if (token.simbolo == Simbolo.SVAR) {
			buscaToken();
			
			if (token.simbolo == Simbolo.SIDENTIFICADOR) {
				
				while (token.simbolo == Simbolo.SIDENTIFICADOR || token.lexema.contentEquals("var")) {
					if (token.lexema.contentEquals("var")) {
						buscaToken();
					}
					analisa_variaveis();
					
					if (token.simbolo == Simbolo.SPONTO_VIRGULA) {
						buscaToken();
					}
					else {
						errorFree = erroToken(token, Simbolo.SPONTO_VIRGULA.toString());
					}
				}
			}
			else {
				errorFree = erroToken(token, Simbolo.SIDENTIFICADOR.toString());
			}
		}
	}	
	
	
	/**
	 * <declaração de variáveis>::= <identificador> {, <identificador>} : <tipo>
	 */
	private void analisa_variaveis() throws IOException {
		
		lv.clear();
		do {
			if (token.simbolo == Simbolo.SIDENTIFICADOR) {
				token.simbolo = Simbolo.SVAR;
				token.escopo = pilha.peek();
				if (!pesquisa_ts(token)) {
					insere_ts(token);
					lv.add(token);
					buscaToken();
					
					if (token.simbolo == Simbolo.SVIRGULA || token.simbolo == Simbolo.SDOISPONTOS ) {
						
						if (token.simbolo == Simbolo.SVIRGULA) {
							buscaToken();
						}
					}
					else {
						errorFree = erroToken(token, Simbolo.SVIRGULA.toString());
						errorFree = erroToken(token, Simbolo.SDOISPONTOS.toString());
					}
				}
				else {
					errorFree = erroDuplic(token);
				}
			}
			else {
				errorFree = erroToken(token, Simbolo.SIDENTIFICADOR.toString());
				break;
			}
		
		} while (!(token.simbolo == Simbolo.SDOISPONTOS));
		
		buscaToken();
		analisa_tipo();
	}
	
	
	/**
	 * <tipo> ::= (inteiro | booleano)
	 */
	private void analisa_tipo() throws IOException {
		
		if (token.simbolo != Simbolo.SINTEIRO && token.simbolo != Simbolo.SBOOLEANO) {
			errorFree = erroToken(token, Simbolo.SINTEIRO.toString());
			errorFree = erroToken(token, Simbolo.SBOOLEANO.toString());
		}
		else {
			for (Token t : lv) {
				Chave chave = new Chave(t.escopo, t.simbolo, t.lexema);
				ts.setAtributo(chave, "tipo", token.lexema);
			}
		}
		
		buscaToken();
	}
	
	
	/**
	 * <comandos>::= inicio
	 *				 	<comando>{;<comando>}[;]
	 *				 fim
	 */
	private void analisa_comandos()  throws IOException {
		
		if (token.simbolo == Simbolo.SINICIO) {
			buscaToken();
			analisa_comando_simples();
			
			 do {
				
				if (token.simbolo == Simbolo.SPONTO_VIRGULA) {
					buscaToken();
					
					if (token.simbolo != Simbolo.SFIM && token.simbolo != Simbolo.SINICIO) {
						analisa_comando_simples();
						
						if (token.simbolo != Simbolo.SPONTO_VIRGULA) {
							errorFree = erroToken(token, Simbolo.SPONTO_VIRGULA.toString());
						}
					}
					else if (token.simbolo == Simbolo.SINICIO) {
						errorFree = erroToken(token, "Outra palavra reservada ou SIDENTIFICADOR");
						break;
					}
				}
				else {
					errorFree = erroToken(token, Simbolo.SPONTO_VIRGULA.toString());
					break;
				}
				
			} while (token.simbolo != Simbolo.SFIM);
			 
			buscaToken();
		}
		else {
			errorFree = erroToken(token, Simbolo.SPONTO_VIRGULA.toString());
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
		
		if (token.simbolo == Simbolo.SIDENTIFICADOR) {
			token.simbolo = Simbolo.SVAR;
			token.escopo = pilha.peek();
			if (pesquisa_ts(token)) {
				analisa_atrib_chprocedimento();
			}
			else {
				errorFree = erroDeclar(token);
			}
			
		}
		else if (token.simbolo == Simbolo.SSE) {
			analisa_se();
		}
		else if (token.simbolo == Simbolo.SENQUANTO) {
			analisa_enquanto();
		}
		else if (token.simbolo == Simbolo.SLEIA) {
			analisa_leia();
		}
		else if (token.simbolo == Simbolo.SESCREVA) {
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
		
		variavelEsqAtrib = token;
		
		buscaToken();
		
		if (token.simbolo == Simbolo.SATRIBUICAO) {
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
		
		if (token.simbolo == Simbolo.SABRE_PARENTESES) {
			buscaToken();
			
			if (token.simbolo == Simbolo.SIDENTIFICADOR) {
				token.simbolo = Simbolo.SVAR;
				token.escopo = pilha.peek();
				if (pesquisa_ts(token)) {
					buscaToken();
					
					if (token.simbolo == Simbolo.SFECHA_PARENTESES) {
						buscaToken();
					}
					else {
						errorFree = erroToken(token, Simbolo.SFECHA_PARENTESES.toString());
					}
				}
				else {
					errorFree = erroDeclar(token);
				}
			}
			else {
				errorFree = erroToken(token, Simbolo.SIDENTIFICADOR.toString());
			}
		}
		else {
			errorFree = erroToken(token, Simbolo.SABRE_PARENTESES.toString());
		}
	}
	
	
	/**
	 * <comando escrita> ::= escreva ( <identificador> )
	 */
	private void analisa_escreva() throws IOException {
		
		buscaToken();
		
		if (token.simbolo == Simbolo.SABRE_PARENTESES) {
			buscaToken();
			
			if (token.simbolo == Simbolo.SIDENTIFICADOR) {
				token.simbolo = Simbolo.SVAR;
				token.escopo = pilha.peek();
				if (pesquisa_ts(token)) {
					buscaToken();
					
					if (token.simbolo == Simbolo.SFECHA_PARENTESES) {
						buscaToken();
					}
					else {
						errorFree = erroToken(token, Simbolo.SFECHA_PARENTESES.toString());
					}
				}
				else {
					errorFree = erroDeclar(token);
				}
			}
			else {
				errorFree = erroToken(token, Simbolo.SIDENTIFICADOR.toString());
			}
		}
		else {
			errorFree = erroToken(token, Simbolo.SABRE_PARENTESES.toString());
		}
	}
	
	
	/**
	 * <comando enquanto> ::= enquanto <expressão> faca <comando>
	 */
	private void analisa_enquanto() throws IOException {
		
		buscaToken();
		analisa_expressao();
		
		if (token.simbolo == Simbolo.SFACA) {
			buscaToken();
			analisa_comando_simples();
		}
		else {
			errorFree = erroToken(token, Simbolo.SFACA.toString());
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
		
		if (token.simbolo == Simbolo.SENTAO) {
			buscaToken();
			analisa_comando_simples();
			
			if (token.simbolo == Simbolo.SSENAO) {
				buscaToken();
				analisa_comando_simples();
			}
		}
		else {
			errorFree = erroToken(token, Simbolo.SENTAO.toString());
		}
	}
	
	
	/**
	 * <etapa de declaração de sub-rotinas> ::= (<declaração de procedimento>;|
	 * 											<declaração de função>;)
	 * 											{<declaração de procedimento>;|
	 * 											<declaração de função>;}
	 */
	private void analisa_subrotinas() throws IOException {
		
		while (token.simbolo == Simbolo.SPROCEDIMENTO || token.simbolo == Simbolo.SFUNCAO) {
			
			if (token.simbolo == Simbolo.SPROCEDIMENTO) {
				analisa_declaracao_procedimento();
			}
			else {
				analisa_declaracao_funcao();
			}
			if (token.simbolo == Simbolo.SPONTO_VIRGULA) {
				buscaToken();
			}
			else {
				errorFree = erroToken(token, Simbolo.SPONTO_VIRGULA.toString());
			}
		}
	}
	
	
	/**
	 * <declaração de procedimento> ::= procedimento <identificador>;
	 *												 <bloco>
	 */
	private void analisa_declaracao_procedimento() throws IOException {
		
		buscaToken();
		
		if (token.simbolo == Simbolo.SIDENTIFICADOR) {
			token.simbolo = Simbolo.SPROCEDIMENTO;
			pilha.push(token.lexema);
			token.escopo = pilha.peek();
			
			if (!pesquisa_ts(token)) {
				insere_ts(token);
				
				buscaToken();
				
				if (token.simbolo == Simbolo.SPONTO_VIRGULA) {
					analisa_bloco();
				}
				else {
					errorFree = erroToken(token, Simbolo.SPONTO_VIRGULA.toString());
				}
			}
			else {
				errorFree = erroDuplic(token);
			}
		}
		else {
			errorFree = erroToken(token, Simbolo.SIDENTIFICADOR.toString());
		}
		pilha.pop();
	}
	
	
	/**
	 * <declaração de função> ::= funcao <identificador>: <tipo>;
	 *									 <bloco>
	 */
	private void analisa_declaracao_funcao() throws IOException {
		
		buscaToken();

		if (token.simbolo == Simbolo.SIDENTIFICADOR) {
			token.simbolo = Simbolo.SFUNCAO;
			pilha.push(token.lexema);
			token.escopo = pilha.peek();
			
			if (!pesquisa_ts(token)) {
				insere_ts(token);

				buscaToken();
				
				if (token.simbolo == Simbolo.SDOISPONTOS) {
					buscaToken();
					
					if (token.simbolo == Simbolo.SINTEIRO || token.simbolo == Simbolo.SBOOLEANO) {
						buscaToken();
						
						if (token.simbolo == Simbolo.SPONTO_VIRGULA) {
							analisa_bloco();
						}
					}
					else {
						errorFree = erroToken(token, Simbolo.SINTEIRO.toString());
						errorFree = erroToken(token, Simbolo.SBOOLEANO.toString());
					}
				}
				else {
					errorFree = erroToken(token, Simbolo.SDOISPONTOS.toString());
				}
			}
		}
		else {
			errorFree = erroToken(token, Simbolo.SIDENTIFICADOR.toString());
		}
		pilha.pop();
	}
	
	
	/**
	 * <expressão>::= <expressão simples> [<operador relacional><expressão simples>]
	 */
	private void analisa_expressao() throws IOException {
		
		analisa_expressao_simples();
		if (token.simbolo == Simbolo.SMAIOR || token.simbolo == Simbolo.SMAIORIG || token.simbolo == Simbolo.SIG || token.simbolo == Simbolo.SMENOR || token.simbolo == Simbolo.SMENORIG || token.simbolo == Simbolo.SDIF) {
			buscaToken();
			analisa_expressao_simples();		
		}
	}
	
	
	/**
	 * <expressão simples> ::= [ + | - ] <termo> {( + | - | ou) <termo> }
	 */
	private Token analisa_expressao_simples() throws IOException {
		
		Token t = new Token("", Simbolo.CODIGO, "", 0, 0, "");
		
		if (token.simbolo == Simbolo.SMAIS || token.simbolo == Simbolo.SMENOS) {
			buscaToken();
		}
		Token t1 = analisa_termo();
			
		while ((token.simbolo == Simbolo.SMAIS) || (token.simbolo == Simbolo.SMENOS) || (token.simbolo == Simbolo.SOU)) {
			buscaToken();
			Token t2 = analisa_termo();
			t.nome = geraTemp();
			geraCod(t.nome + " = add i32 " + t1.nome + ", " + t2.nome);
		}
		
		return t;
	}
	
	
	/**
	 * <termo>::= <fator> {(* | div | e) <fator>}
	 */
	private Token analisa_termo() throws IOException {
		
		Token t = new Token("", Simbolo.CODIGO, "", 0, 0, "");
		Token t1 = analisa_fator();
		
		while (token.simbolo == Simbolo.SMULT || token.simbolo == Simbolo.SDIV || token.simbolo == Simbolo.SE) {
			buscaToken();
			Token t2 = analisa_fator();
			t.nome = geraTemp();
			geraCod(t.nome + " = mul i32 " + t1.nome + ", " + t2.nome);
		}
		
		return t;
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
	private Token analisa_fator() throws IOException {
		
		Token t = new Token("", Simbolo.CODIGO, "", 0, 0, "");
	
		// <variável> ou <função>
		if (token.simbolo == Simbolo.SIDENTIFICADOR) {
				
			// <função>
			Token f_token = new Token(token.escopo, token.simbolo, token.lexema, token.linha, token.coluna, token.tipo);
			f_token.simbolo = Simbolo.SFUNCAO;
			token.escopo = pilha.peek();
			Chave f_chave = new Chave(f_token.escopo, f_token.simbolo, f_token.lexema);
			
			// <variável>
			Token v_token = new Token(token.escopo, token.simbolo, token.lexema, token.linha, token.coluna, token.tipo);
			v_token.simbolo = Simbolo.SVAR;
			token.escopo = pilha.peek();
			Chave v_chave = new Chave(v_token.escopo, v_token.simbolo, v_token.lexema);
			
			// <função>
			if (ts.containsKey(f_chave)) {
				if (ts.getAtributo(f_chave, "tipo") == Simbolo.SINTEIRO.toString() || ts.getAtributo(f_chave, "tipo") == Simbolo.SBOOLEANO.toString()) {
					
					// TODO gera código intermediário para função
					analisa_ch_funcao();
				}
				else {
					errorFree = erroTipo(token, ts.getAtributo(f_chave, "tipo"));
				}
			}
				
			// <variável>
			else if (ts.containsKey(v_chave)) {
				if (variavelEsqAtrib.tipo != v_token.tipo) {
					errorFree = erroTipo(v_token, variavelEsqAtrib.tipo);
				}
				
				// TODO gera código intermediário para variável
				buscaToken();
			}
			
			else {
				errorFree = erroDeclar(token);
			}
		}
	
		// <número>
		else if (token.simbolo == Simbolo.SNUMERO) {
			Chave chave = new Chave(variavelEsqAtrib.escopo, variavelEsqAtrib.simbolo, variavelEsqAtrib.lexema);
			
			if (ts.getAtributo(chave, "tipo").contentEquals("inteiro")) {
				
				// Gera código para número
				t.lexema = token.lexema;
				t.codigo = token.lexema;
				t.nome = geraTemp();
				geraCod("store i32 " + t.lexema + ", i32* " + t.nome + ", align 4");
				buscaToken();
			}
			else {
				errorFree = erroTipo(token, ts.getAtributo(chave, "tipo"));
			}
		}
		
/**
		// <chamada de função>
		else if (token.simbolo == Simbolo.SFUNCAO) {
			token.escopo = pilha.peek();
			Chave chave = new Chave(token.escopo, token.simbolo, token.lexema);			
			if (ts.containsKey(chave)) {
				analisa_ch_funcao();			
			}
			else {
				errorFree = erroDeclar(token);
			}
		}
*/
	
		// ( <expressão> )
		else if (token.simbolo == Simbolo.SABRE_PARENTESES) {
			buscaToken();
			analisa_expressao();
			
			if (token.simbolo == Simbolo.SFECHA_PARENTESES) {
				buscaToken();
			}
			else {
				errorFree = erroToken(token, Simbolo.SFECHA_PARENTESES.toString());
			}
		}
		
		// <verdadeiro> | <falso>
		else if (token.lexema.contentEquals("verdadeiro") || token.lexema.contentEquals("falso")) {
			Chave chave = new Chave(variavelEsqAtrib.escopo, variavelEsqAtrib.simbolo, variavelEsqAtrib.lexema);
			
			if (ts.getAtributo(chave, "tipo").contentEquals("booleano")) {
				buscaToken();
			}
			else {
				erroTipo(token, ts.getAtributo(chave, "tipo"));
			}
		}
		
		// <não>
		else if (token.simbolo == Simbolo.SNAO) {
			buscaToken();
			analisa_fator();
		}
		
		else {
			errorFree = erroToken(token, Simbolo.SVERDADEIRO.toString());
			errorFree = erroToken(token, Simbolo.SFALSO.toString());
		}
		
		return t;
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
		Chave chave = new Chave(token.escopo, token.simbolo, token.lexema);
		
		if (!ts.containsKey(chave)) {
			errorFree = erroDeclar(token);
		}
	}
	
	
	private boolean analisa_ch_funcao() throws IOException {
		token.escopo = pilha.peek();
		Chave chave = new Chave(token.escopo, token.simbolo, token.lexema);
		if (ts.containsKey(chave)) {
			return true;
		}
		else {
			return false;
		}
	}

	
	/**
	 * Pesquisa se existe um token na Tabela de Simbolos
	 */
	private boolean pesquisa_ts(Token token) {
		Chave chave = new Chave(token.escopo, token.simbolo, token.lexema);
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
		Chave chave = new Chave(token.escopo, token.simbolo, token.lexema);
		ts.addToken(chave, token);
		//ts.setAtributo(chave, "tipo", novoTipo.toString());
	}
	
	private String geraTemp() {
		Integer i = temp++;
		String nomeVar = '%' + i.toString();
		geraCod(nomeVar + " = alloca i32, align 4");
		return nomeVar;
	}
	
	private void geraCod(String s) {
		codigo += s + "\n";
	}
	
}

