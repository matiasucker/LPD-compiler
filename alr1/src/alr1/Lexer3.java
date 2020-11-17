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
import java.util.List;
import java.util.Stack;

public class Lexer3 {
	
	/**
	 * Objeto do tipo Simbolo (escopo, tipo, lexema, linha, coluna)
	 */
	Simbolo simbolo;

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
	Hashtable<String, ArrayList<Simbolo>> ts = new Hashtable<String, ArrayList<Simbolo>>();
	
	// Metodo adiciona token
	public void addToken (String chave, Simbolo simbolo) {
		
		// cria a ArrayList do tipo Simbolo
		ArrayList<Simbolo> simbolos = ts.get(chave);
		
		// Se a lista de simbolos não existir, ela então é criada
		if (simbolos == null) {
			simbolos = new ArrayList<Simbolo>();
			simbolos.add(simbolo);
			ts.put(chave, simbolos);
		} else {
			// Adiciona se o símbolo não existir na lista
			if (!simbolos.contains(simbolo)) simbolos.add(simbolo);
		}
		
	}
	
	public ArrayList<Simbolo> getToken (String chave) {
			
		return ts.get(chave);
	}
	
	public void setAtributo (String chave, String atributo, String valor) {
		
		
	}
	
	@SuppressWarnings("rawtypes")
	public ArrayList<List> getAtributo (String chave, String atributo) {
		
		ArrayList<List> listaAtributos = new ArrayList<List>();
		ArrayList<Simbolo> listaTokens = ts.get(chave);
		
		return listaAtributos;
	}
	
	
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
	

	
	private void buscaToken() throws IOException {
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
					lt.add(new Token(TipoToken.SATRIBUICAO, lexema, linha, col));
				}
				else {
					devolve();
					lt.add(new Token(TipoToken.STIPO, lexema, linha, col));
				}
			}
			else if (lexema.contentEquals(";")) lt.add(new Token(TipoToken.SPONTO_E_VIRGULA, lexema, linha, col));
			else if (lexema.contentEquals(",")) lt.add(new Token(TipoToken.SVIRGULA, lexema, linha, col));
			else if (lexema.contentEquals(".")) lt.add(new Token(TipoToken.SPONTO, lexema, linha, col));
			else if (lexema.contentEquals("(")) lt.add(new Token(TipoToken.SABRE_PARENTESIS, lexema, linha, col));
			else if (lexema.contentEquals(")")) lt.add(new Token(TipoToken.SFECHA_PARENTESIS, lexema, linha, col));
			
			
			/**
			 * Operações Numéricas
			 */
			else if (lexema.contentEquals("+")) lt.add(new Token(TipoToken.SMAIS, lexema, linha, col));
			else if (lexema.contentEquals("-")) lt.add(new Token(TipoToken.SMENOS, lexema, linha, col));
			else if (lexema.contentEquals("*")) lt.add(new Token(TipoToken.SMULTIPLICACAO, lexema, linha, col));
			/**
			 *  O operador de Divisão é a String "div", portanto a análise e retorno do novo Token 
			 *  será realizada na parte de detecção de < Palavras reservadas e identificadores >
			 *  Laço Loop de todos os caracteres que são letras, usando o método < Character.isLetter >
			 */
			
			
			/**
			 * Operações Relacionais
			 */
			else if (lexema.contentEquals("=")) lt.add(new Token(TipoToken.SIGUAL, lexema, linha, col));
			else if (lexema.contentEquals("<")) {
				lech();
				if (ch == '=') {
					lexema += ch;
					lt.add(new Token(TipoToken.SMENORIGUAL, lexema, linha, col));
				}
				else if (ch == '>'){
					lexema += ch;
					lt.add(new Token(TipoToken.SDIFERENTEDE, lexema, linha, col));
				}
				else {
					devolve();
					lt.add(new Token(TipoToken.SMENOR, lexema, linha, col));
				}
			}
			else if (lexema.contentEquals(">")) {
				lech();
				if (ch == '=') {
					lexema += ch;
					lt.add(new Token(TipoToken.SMAIORIGUAL, lexema, linha, col));
				}
				else {
					devolve();
					lt.add(new Token(TipoToken.SMAIOR, lexema, linha, col));
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
				lt.add(new Token(TipoToken.SNUMERO, lexema, linha, col));
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
					lt.add(new Token(TipoToken.SPROGRAMA, lexema, linha, col));
				}
				else if (lexema.contentEquals("var")) lt.add(new Token(TipoToken.SVAR, lexema, linha, col));
				else if (lexema.contentEquals("inicio")) lt.add(new Token(TipoToken.SINICIO, lexema, linha, col));
				else if (lexema.contentEquals("fim")) {
					pilha.pop();
					escopo = pilha.peek();
					lt.add(new Token(TipoToken.SFIM, lexema, linha, col));
				}
				else if (lexema.contentEquals("escreva")) lt.add(new Token(TipoToken.SESCREVA, lexema, linha, col));
				else if (lexema.contentEquals("leia")) lt.add(new Token(TipoToken.SLEIA, lexema, linha, col));
				
				/**
				 * Tipos de Dados
				 */
				else if (lexema.contentEquals("inteiro")) lt.add(new Token(TipoToken.SINTEIRO, lexema, linha, col));
				else if (lexema.contentEquals("booleano")) lt.add(new Token(TipoToken.SBOOLEANO, lexema, linha, col));
				else if (lexema.contentEquals("verdadeiro")) lt.add(new Token(TipoToken.SVERDADEIRO, lexema, linha, col));
				else if (lexema.contentEquals("falso")) lt.add(new Token(TipoToken.SFALSO, lexema, linha, col));
				
				/**
				 * Operações Lógicas
				 */
				else if (lexema.contentEquals("e")) lt.add(new Token(TipoToken.SE, lexema, linha, col));
				else if (lexema.contentEquals("ou")) lt.add(new Token(TipoToken.SOU, lexema, linha, col));
				else if (lexema.contentEquals("nao")) lt.add(new Token(TipoToken.SNAO, lexema, linha, col));
				
				/**
				 * Procedimentos e Funções
				 */
				else if (lexema.contentEquals("procedimento")) {
					pilha.push(lexema);
					escopo = pilha.peek();
					lt.add(new Token(TipoToken.SPROCEDIMENTO, lexema, linha, col));
				}
				else if (lexema.contentEquals("funcao")) {
					pilha.push(lexema);
					escopo = pilha.peek();
					lt.add(new Token(TipoToken.SFUNCAO, lexema, linha, col));
				}
				
				/**
				 * Decisões e Loop
				 */
				else if (lexema.contentEquals("se")) lt.add(new Token(TipoToken.SSE, lexema, linha, col));
				else if (lexema.contentEquals("entao")) lt.add(new Token(TipoToken.SENTAO, lexema, linha, col));
				else if (lexema.contentEquals("senao")) lt.add(new Token(TipoToken.SSENAO, lexema, linha, col));
				else if (lexema.contentEquals("enquanto")) lt.add(new Token(TipoToken.SENQUANTO, lexema, linha, col));
				else if (lexema.contentEquals("faca")) lt.add(new Token(TipoToken.SFACA, lexema, linha, col));
				
				/**
				 * Operação Numérica - DIVISÃO (continuação do item mais acima)
				 */
				else if (lexema.contentEquals("div")) lt.add(new Token(TipoToken.SDIVISAO, lexema, linha, col));
				
				/**
				 * Identificadores
				 */
				else {
					lt.add(new Token (TipoToken.SIDENTIFICADOR, lexema, linha, col));
					
					simbolo = new Simbolo (escopo, TipoToken.SIDENTIFICADOR, lexema, linha, col);
					
					addToken(lexema, simbolo);
				}
			}
			
			// Nenhuma correspondência encontrada conforme as regras
			else lt.add(new Token(TipoToken.SERRO, "", linha, coluna));
			lech();
		} // Fim while
		
		// Nenhuma correspondência encontrada conforme as regras
		//lt.add(new Token(TipoToken.SERRO, "", linha, coluna));
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
		
    
        /**
         * Percorre lista de tokens imprimindo-os
         */
		System.out.println("\n\n--------------------------------------------------------------------------");
		System.out.println("Imprimindo a lista de tokens");
		System.out.println("--------------------------------------------------------------------------\n");
        //Imprime número de tokens
        System.out.println("Número de tokens: " +lt.size() + "\n");
        lt.forEach(token -> System.out.println(token.toString()));
        
        /**
         * Percorre a tabela de simbolos imprimindo-os
         */
		System.out.println("\n\n--------------------------------------------------------------------------");
		System.out.println("Imprimindo a tabela de simbolos");
		System.out.println("--------------------------------------------------------------------------\n");
        //Imprime número de simbolos
        System.out.println("Número de simbolos: " +ts.size() + "\n");
		ts.forEach( (chave, simbolo) -> System.out.println("Simbolo : " + chave + "\n" + simbolo) );
				
		/**
		 * Adicionando token com a função addToken(chave, simbolo)
		 * 
		 * simbolo e dentro os seus atributos são valores para a hashtable
		 */
		simbolo = new Simbolo ("sem escopo", TipoToken.SERRO, "sem lexema", 99999, 99999);
		// chave = "outraChave", valor = simbolo
		addToken("outraChave", simbolo);
		
		// Imprimindo com a função getToken(chave)
		System.out.println("\n\n--------------------------------------------------------------------------");
		System.out.println("Imprimindo com a função getToken(chave)");
		System.out.println("--------------------------------------------------------------------------\n");
		System.out.println(getToken("calcula_expressao"));    // chave = "calcula_expressao"
		System.out.println(getToken("teste"));                // chave = "teste"
		System.out.println(getToken("qualquer"));             // chave = "qualquer"
		System.out.println(getToken("x"));                    // chave = "x"
		
		// Imprimindo após inserção com a função addToken()
		System.out.println("\n\n--------------------------------------------------------------------------");
		System.out.println("Imprimindo após inserção com a função addToken()");
		System.out.println("--------------------------------------------------------------------------\n");
		System.out.println(getToken("outraChave"));           // chave = "outraChave", foi adicionada avulso
	}
}
