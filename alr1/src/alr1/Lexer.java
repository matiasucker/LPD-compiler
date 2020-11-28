package alr1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Stack;

public class Lexer {
	
	/**
	 * Tabela de Simbolos
	 */
	TS ts;
	
	/**
	 * Construtor do Lexer com injeção de dependência
	 * @param ts
	 */
	public Lexer (TS ts) {
			this.ts = ts;
	}
	
	/**
	 * Stream de leitura do arquivo fonte
	 */
	PushbackReader r;
	
	/**
	 * Cria uma pilha para identificação do escopo
	 */
	Stack<String> pilha = new Stack<String>();
	
	/**
	 * String para receber o tipo de escopo
	 */
	String escopo;
	
	/**
	 * Lista de tokens
	 */
	ArrayList<Token> lt = new ArrayList<Token>();
	
	/**
	 * Código do caracter sendo analisado
	 */
	int intch;
	
	/**
	 * Caracter sendo analisado
	 */
	char ch;
	
	/**
	 * Linha inicial do token
	 */
	int linha = 1;
	
	/**
	 * Coluna inicial do token;
	 */
	int coluna;
	
	/**
	 * Lê o próximo caracter do Stream
	 * @return
	 */
	private char lech() {
		
		try {
			intch = r.read();
			if (intch != -1)
				ch = (char) intch;
			else
				ch = '@';
			if (ch == '\n') {
				linha++;
				coluna = 0;
			} else if (ch == '\t')
				coluna += 4;
			else
				coluna++;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ch;
	}
	
	public void devolve() {
		try {
			r.unread(ch);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (ch == '\n')
			linha--;
		else if (ch == '\t')
			coluna -= 4;
		else
			coluna--;
	}
	

	
	public Token buscaToken() throws IOException {
		
		/**
		 * Lexema sendo construído quando for um identificador ou palavra-chave
		 */
		String lexema = "";
		
		/**
		 * Coluna inicial do lexema que está sendo lido
		 */
		int col = 0;
		
		
		/**
		 * Iniciando a pilha para o escopo
		 */
		pilha.push("NaoVazio");
		
		/**
		 * Lendo todos os caracteres
		 */
		while (ch != '@') {
			lexema = "";    // Esvazia o conteúdo do lexema
			lexema += ch;   // Adiciona primeiro caractere lido ao lexema
			col = coluna;   // Salva a coluna do primeiro caractere lido do lexema
			
			/**
			 * Pontuação e Símbolos Especiais
			 */
			if (lexema.contentEquals(":")) {
				lech();
				if (ch == '=') {
					lexema += ch;
					Token token = new Token(escopo, TipoToken.SATRIBUICAO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else {
					devolve();
					Token token = new Token(escopo, TipoToken.STIPO, lexema, linha, col);
					lt.add(token);
					return token;
				}
			}
			else if (lexema.contentEquals(";")) {
				Token token = new Token(escopo, TipoToken.SPONTO_VIRGULA, lexema, linha, col);
				lt.add(token);
				return token;
			}
			else if (lexema.contentEquals(",")) {
				Token token = new Token(escopo, TipoToken.SVIRGULA, lexema, linha, col);
				lt.add(token);
				return token;
			}
			else if (lexema.contentEquals(".")) {
				Token token = new Token(escopo, TipoToken.SPONTO, lexema, linha, col);
				lt.add(token);
				return token;
			}
			else if (lexema.contentEquals("(")) {
				Token token = new Token(escopo, TipoToken.SABRE_PARENTESES, lexema, linha, col);
				lt.add(token);
				return token;
			}
			else if (lexema.contentEquals(")")) {
				Token token = new Token(escopo, TipoToken.SFECHA_PARENTESES, lexema, linha, col);
				lt.add(token);
				return token;
			}
			
			
			/**
			 * Operações Numéricas
			 */
			else if (lexema.contentEquals("+")) {
				Token token = new Token(escopo, TipoToken.SMAIS, lexema, linha, col);
				lt.add(token);
				return token;
			}
			else if (lexema.contentEquals("-")) {
				Token token = new Token(escopo, TipoToken.SMENOS, lexema, linha, col);
				lt.add(token);
				return token;
			}
			else if (lexema.contentEquals("*")) {
				Token token = new Token(escopo, TipoToken.SMULT, lexema, linha, col);
				lt.add(token);
				return token;
			}
			/**
			 *  O operador de Divisão é a String "div", portanto a análise e retorno do novo Token 
			 *  será realizada na parte de detecção de < Palavras reservadas e identificadores >
			 *  Laço Loop de todos os caracteres que são letras, usando o método < Character.isLetter >
			 */
			
			
			/**
			 * Operações Relacionais
			 */
			else if (lexema.contentEquals("=")) {
				Token token = new Token(escopo, TipoToken.SIG, lexema, linha, col);
				lt.add(token);
				return token;
			}
			else if (lexema.contentEquals("<")) {
				lech();
				if (ch == '=') {
					lexema += ch;
					Token token = new Token(escopo, TipoToken.SMENORIG, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (ch == '>'){
					lexema += ch;
					Token token = new Token(escopo, TipoToken.SDIF, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else {
					devolve();
					Token token = new Token(escopo, TipoToken.SMENOR, lexema, linha, col);
					lt.add(token);
					return token;
				}
			}
			else if (lexema.contentEquals(">")) {
				lech();
				if (ch == '=') {
					lexema += ch;
					Token token = new Token(escopo, TipoToken.SMAIORIG, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else {
					devolve();
					Token token = new Token(escopo, TipoToken.SMAIOR, lexema, linha, col);
					lt.add(token);
					return token;
				}
			}
			
			/**
			 * Caracteres a serem descartados
			 */
			else if (lexema.contentEquals(" ")) ;
			else if (lexema.contentEquals("\n")) ;
			else if (lexema.contentEquals("\t")) ;
			else if (lexema.contentEquals("{")) {
				while (ch != '}') lech();
			}
			
			/**
			 * Dígitos / números
			 */
			else if (Character.isDigit(ch)) {
				lech();
				while (Character.isDigit(ch)) {
					lexema += ch;
					lech();
				}
				devolve();
				Token token = new Token(escopo, TipoToken.SNUMERO, lexema, linha, col);
				lt.add(token);
				return token;
			}
			
			
			/**
			 * Palavras reservadas e identificadores
			 */
			else if (Character.isLetter(ch)) {
				lech();
				while (Character.isLetter(ch) || Character.isDigit(ch) || ch == '_') {
					lexema += ch;
					lech();
				}
				devolve();
				
				/**
				 * Estrutura Principal do Programa
				 */
				if (lexema.contentEquals("programa")) {
					pilha.push(lexema);
					escopo = pilha.peek();
					Token token = new Token(escopo, TipoToken.SPROGRAMA, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("var")) {
					Token token = new Token(escopo, TipoToken.SVAR, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("inicio")) {
					Token token = new Token(escopo, TipoToken.SINICIO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("fim")) {
					pilha.pop();
					escopo = pilha.peek();
					Token token = new Token(escopo, TipoToken.SFIM, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("escreva")) {
					Token token = new Token(escopo, TipoToken.SESCREVA, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("leia")) {
					Token token = new Token(escopo, TipoToken.SLEIA, lexema, linha, col);
					lt.add(token);
					return token;
				}
				
				/**
				 * Tipos de Dados
				 */
				else if (lexema.contentEquals("inteiro")) {
					Token token = new Token(escopo, TipoToken.SINTEIRO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("booleano")) {
					Token token = new Token(escopo, TipoToken.SBOOLEANO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("verdadeiro")) {
					Token token = new Token(escopo, TipoToken.SVERDADEIRO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("falso")) {
					Token token = new Token(escopo, TipoToken.SFALSO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				
				/**
				 * Operações Lógicas
				 */
				else if (lexema.contentEquals("e")) {
					Token token = new Token(escopo, TipoToken.SE, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("ou")) {
					Token token = new Token(escopo, TipoToken.SOU, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("nao")) {
					Token token = new Token(escopo, TipoToken.SNAO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				
				/**
				 * Procedimentos e Funções
				 */
				else if (lexema.contentEquals("procedimento")) {
					pilha.push(lexema);
					escopo = pilha.peek();
					Token token = new Token(escopo, TipoToken.SPROCEDIMENTO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("funcao")) {
					pilha.push(lexema);
					escopo = pilha.peek();
					Token token = new Token(escopo, TipoToken.SFUNCAO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				
				/**
				 * Decisões e Loop
				 */
				else if (lexema.contentEquals("se")) {
					Token token = new Token(escopo, TipoToken.SSE, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("entao")) {
					Token token = new Token(escopo, TipoToken.SENTAO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("senao")) {
					Token token = new Token(escopo, TipoToken.SSENAO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("enquanto")) {
					Token token = new Token(escopo, TipoToken.SENQUANTO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("faca")) {
					Token token = new Token(escopo, TipoToken.SFACA, lexema, linha, col);
					lt.add(token);
					return token;
				}
				
				/**
				 * Operação Numérica - DIVISÃO (continuação do item mais acima)
				 */
				else if (lexema.contentEquals("div")) {
					Token token = new Token(escopo, TipoToken.SDIV, lexema, linha, col);
					lt.add(token);
					return token;
				}
				
				/**
				 * Identificadores
				 */
				else {
					
					Token token = new Token(escopo, TipoToken.SIDENTIFICADOR, lexema, linha, col);
					lt.add(token);
					
					// Inserção na Tabela de Símbolos
					Chave chave = new Chave(escopo, TipoToken.SIDENTIFICADOR, lexema);
					ts.put(chave, token);
					
					return token;
				}
			}
			
			// Nenhuma correspondência encontrada conforme as regras
			else {
				Token token = new Token(escopo, TipoToken.SERRO, "", linha, coluna);
				lt.add(token);
				return token;
			}
			lech();
		} // Fim while
		
		// Nenhuma correspondência encontrada conforme as regras
		Token token = new Token(escopo, TipoToken.SERRO, "", linha, coluna);
		lt.add(token);
		return token;
	}
		
		
	public void analisa (String arquivo) throws IOException {
	
		try {
			r = new PushbackReader (
					new BufferedReader (
							new InputStreamReader (
									new FileInputStream (arquivo), "US-ASCII")));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// Ler todo o stream r
		while (( ch = lech()) != '@') {
			
			buscaToken();
		}
	}
}
