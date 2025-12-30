package core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class Antivirus {
	public static ThreadLocal<String> path = new ThreadLocal<>();			// Atributo de classe, caminho do arquivo a ser escaneado
	protected static ThreadLocal<byte[]> fileContent = new ThreadLocal<>();// Conteudo do arquivo a ser escaneado em bytes
	
	public static boolean getFileContent() { // Funcao para abrir e pegar o conteudo do arquivo
	    try {
	        byte[] data = Files.readAllBytes(Paths.get(Antivirus.path.get())); // readAllBytes para pegar todos os bytes do arquivo
	        Antivirus.fileContent.set(data);								// Salva esses bytes no atributo fileContent
	        return true;
	    } catch (IOException e){
	        return false; // Se nao conseguiu abrir o arquivo, retorna false
	    }
	}
	
	public static Antivirus[] getScanM() {
		Antivirus[] methods = new Antivirus[3];
		methods[0] = new HashAnalyzer();
		methods[1] = new SignatureAnalyzer();
		methods[2] = new StringAnalyzer();
		return methods;
	}
	
	public abstract boolean isMalicious(); // Metodo que deve ser implementado nas classes filhas
}
