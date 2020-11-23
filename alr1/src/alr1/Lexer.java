package alr1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

public class Lexer {

	/**
	 * String para receber o tipo de escopo
	 */
	String escopo;
	
	/**
	 * Cria uma pilha para identificação do escopo
	 */
	Stack<String> pilha = new Stack<String>();
	
	/**
	 * Lista de tokens
	 */
	ArrayList<Token> lt = new ArrayList<Token>();
	
	/**
	 * Tabela de simbolos implementado em Hashtable <chave, valor>
	 */
	Hashtable<Chave, Token> ts = new Hashtable<Chave, Token>();
	
	
	/**
	 * Stream de leitura do arquivo fonte
	 */
	PushbackReader r;
	
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ch;
	}
	
	public void devolve() {
		try {
			r.unread(ch);
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		// TODO Auto-generated method stub
		//return null;
		
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
					Token token = new Token(TipoToken.SATRIBUICAO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else {
					devolve();
					Token token = new Token(TipoToken.STIPO, lexema, linha, col);
					lt.add(token);
					return token;
				}
			}
			else if (lexema.contentEquals(";")) {
				Token token = new Token(TipoToken.SPONTO_E_VIRGULA, lexema, linha, col);
				lt.add(token);
				return token;
			}
			else if (lexema.contentEquals(",")) {
				Token token = new Token(TipoToken.SVIRGULA, lexema, linha, col);
				lt.add(token);
				return token;
			}
			else if (lexema.contentEquals(".")) {
				Token token = new Token(TipoToken.SPONTO, lexema, linha, col);
				lt.add(token);
				return token;
			}
			else if (lexema.contentEquals("(")) {
				Token token = new Token(TipoToken.SABRE_PARENTESIS, lexema, linha, col);
				lt.add(token);
				return token;
			}
			else if (lexema.contentEquals(")")) {
				Token token = new Token(TipoToken.SFECHA_PARENTESIS, lexema, linha, col);
				lt.add(token);
				return token;
			}
			
			
			/**
			 * Operações Numéricas
			 */
			else if (lexema.contentEquals("+")) {
				Token token = new Token(TipoToken.SMAIS, lexema, linha, col);
				lt.add(token);
				return token;
			}
			else if (lexema.contentEquals("-")) {
				Token token = new Token(TipoToken.SMENOS, lexema, linha, col);
				lt.add(token);
				return token;
			}
			else if (lexema.contentEquals("*")) {
				Token token = new Token(TipoToken.SMULTIPLICACAO, lexema, linha, col);
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
				Token token = new Token(TipoToken.SIGUAL, lexema, linha, col);
				lt.add(token);
				return token;
			}
			else if (lexema.contentEquals("<")) {
				lech();
				if (ch == '=') {
					lexema += ch;
					Token token = new Token(TipoToken.SMENORIGUAL, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (ch == '>'){
					lexema += ch;
					Token token = new Token(TipoToken.SDIFERENTEDE, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else {
					devolve();
					Token token = new Token(TipoToken.SMENOR, lexema, linha, col);
					lt.add(token);
					return token;
				}
			}
			else if (lexema.contentEquals(">")) {
				lech();
				if (ch == '=') {
					lexema += ch;
					Token token = new Token(TipoToken.SMAIORIGUAL, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else {
					devolve();
					Token token = new Token(TipoToken.SMAIOR, lexema, linha, col);
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
				Token token = new Token(TipoToken.SNUMERO, lexema, linha, col);
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
					Token token = new Token(TipoToken.SPROGRAMA, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("var")) {
					Token token = new Token(TipoToken.SVAR, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("inicio")) {
					Token token = new Token(TipoToken.SINICIO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("fim")) {
					pilha.pop();
					escopo = pilha.peek();
					Token token = new Token(TipoToken.SFIM, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("escreva")) {
					Token token = new Token(TipoToken.SESCREVA, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("leia")) {
					Token token = new Token(TipoToken.SLEIA, lexema, linha, col);
					lt.add(token);
					return token;
				}
				
				/**
				 * Tipos de Dados
				 */
				else if (lexema.contentEquals("inteiro")) {
					Token token = new Token(TipoToken.SINTEIRO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("booleano")) {
					Token token = new Token(TipoToken.SBOOLEANO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("verdadeiro")) {
					Token token = new Token(TipoToken.SVERDADEIRO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("falso")) {
					Token token = new Token(TipoToken.SFALSO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				
				/**
				 * Operações Lógicas
				 */
				else if (lexema.contentEquals("e")) {
					Token token = new Token(TipoToken.SE, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("ou")) {
					Token token = new Token(TipoToken.SOU, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("nao")) {
					Token token = new Token(TipoToken.SNAO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				
				/**
				 * Procedimentos e Funções
				 */
				else if (lexema.contentEquals("procedimento")) {
					pilha.push(lexema);
					escopo = pilha.peek();
					Token token = new Token(TipoToken.SPROCEDIMENTO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("funcao")) {
					pilha.push(lexema);
					escopo = pilha.peek();
					Token token = new Token(TipoToken.SFUNCAO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				
				/**
				 * Decisões e Loop
				 */
				else if (lexema.contentEquals("se")) {
					Token token = new Token(TipoToken.SSE, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("entao")) {
					Token token = new Token(TipoToken.SENTAO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("senao")) {
					Token token = new Token(TipoToken.SSENAO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("enquanto")) {
					Token token = new Token(TipoToken.SENQUANTO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				else if (lexema.contentEquals("faca")) {
					Token token = new Token(TipoToken.SFACA, lexema, linha, col);
					lt.add(token);
					return token;
				}
				
				/**
				 * Operação Numérica - DIVISÃO (continuação do item mais acima)
				 */
				else if (lexema.contentEquals("div")) {
					Token token = new Token(TipoToken.SDIVISAO, lexema, linha, col);
					lt.add(token);
					return token;
				}
				
				/**
				 * Identificadores
				 */
				else {
					
					Token token = new Token(TipoToken.SIDENTIFICADOR, lexema, linha, col);
					lt.add(token);
					
					// Inserção na Tabela de Símbolos
					Chave chave = new Chave(escopo, TipoToken.SIDENTIFICADOR, lexema);
					ts.put(chave, token);
					
					return token;
					
				}
			}
			
			// Nenhuma correspondência encontrada conforme as regras
			else {
				Token token = new Token(TipoToken.SERRO, "", linha, coluna);
				lt.add(token);
				return token;
			}
			lech();
		} // Fim while
		
		// Nenhuma correspondência encontrada conforme as regras
		Token token = new Token(TipoToken.SERRO, "", linha, coluna);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Ler todo o stream r
		while (( ch = lech()) != '@') {
			
			buscaToken();
		}
		
    

	}
}
