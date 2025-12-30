package core;

import java.util.*;
import storage.getdb;
import java.util.regex.Pattern;

// Extrair e analisar todas as strings do arquivo analisado
public class StringAnalyzer extends Antivirus {
	private ArrayList<String> extract = new ArrayList<>(); //Atributo encapsulado, contem as strings extraidas
	
	public void setExtract(byte[] data, int minLength){ // set (encapsulamento)
		StringBuilder sb = new StringBuilder(); // String builder para juntar os caracteres obtidos
		
		for (byte b : data) {
			if (b >= 32 && b <= 126) {
				sb.append((char) b);	// Se o valor numerico do byte for maior que 32 e menor que 126, entao e um caracter (tabela ASCII)
										// Adiciona o caractere no stringbuilder
			} else {
				if (sb.length() >= minLength) {
					extract.add(sb.toString()); // Se o tamanho da palavra formata satisfazer o tamanho definido, adiciona no arraylist
				}
			sb.setLength(0); // Limpa o buffer do arraylist
			}
		}
		
		if (sb.length() >= minLength) {
			extract.add(sb.toString()); // Se houver alguma coisa ainda salva no buffer e satisfazer o tamanho, salva no ArrayList
		}
	}
	
	public ArrayList<String> getExtract() { // Obtem as Strings extraidas
		return extract;
	}
	
	public Boolean isStringMalicious(String str) { // Verifica se uma string esta no banco de dados de strings maliciosas
		getdb db = new getdb(); // Instancia de getdb

		ArrayList<String> stringsDb = db.getStringsDb(); // Pega o banco de dados de strings e salva no ArrayList stringsDb

		
		for (String mw : stringsDb) {
			if (str.toLowerCase().matches(".*\\b" + Pattern.quote(mw) + "\\b.*")) { // Verifica se a string contem a string verificada no db
				return true;
			}
		}
		return false;
		
	}
	
	public boolean isMalicious() {
		byte[] data = Antivirus.fileContent.get();
		setExtract(data, 5); // Extrai strings
		ArrayList<String> strings = getExtract();	// Pega as strings e armazena 
		

		int ocurrences = 0; // Contador de ocorrencias de strings maliciosas
							// Ter 1 ou 2 strings suspeitas nao signfica que o arquivo e um malware
							// por isso o contador
		for (String test : strings) {
			if (isStringMalicious(test) == true) {
				ocurrences += 1; // Se contem uma string suspeita, adiciona 1 no contador
			}
		}

		if (ocurrences > 50) {
			return true; 		// Durante alguns testes verifiquei que um binario malicioso costuma ter acima de 100 strins suspeitas
		}
		return false; // Se nao for malicioso, retorna false
	}
	
}
