package core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class Antivirus {
	public static String path;			// Atributo de classe, caminho do arquivo a ser escaneado
	protected static byte[] fileContent;// Conteudo do arquivo a ser escaneado em bytes
	
	public static boolean getFileContent() { // Funcao para abrir e pegar o conteudo do arquivo
	    try {
	        byte[] data = Files.readAllBytes(Paths.get(path)); // readAllBytes para pegar todos os bytes do arquivo
	        fileContent = data;									// Salva esses bytes no atributo fileContent
	        return true;
	    } catch (IOException e){
	        return false; // Se nao conseguiu abrir o arquivo, retorna false
	    }
	}
	
	public abstract boolean isMalicious(); // Metodo que deve ser implementado nas classes filhas
}
