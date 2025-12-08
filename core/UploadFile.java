package core;

import java.nio.file.Files;
import java.nio.file.Paths;

public class UploadFile {
	// Método que tem como parametros o camingo do arquivo e que tipo de algoritmo será aplicado nele
	public boolean uploadFile(String caminhoArquivo, String algoritmo){
	    try{
			// Lê todos os bytes do arquivo e armazena no array dados
	        byte[] dados = Files.readAllBytes(Paths.get(caminhoArquivo));
			// Gera a hash chamando o método retornaHash da classe AddHash
	        String hashGerada = new AddHash().retornaHash(dados, algoritmo);
			// Se a hash não foi gerada retorna false
	        if(hashGerada == null){ // 
	            return false;
	        }
			//chama salvaHash para gravar no database.txt
	        boolean salvou = new AddHash().salvaHash(hashGerada);
	        return salvou;
	    }
		// Caso ocorra erro retorna false
	    catch(Exception e){
	        e.printStackTrace();
	        return false;
	    }
	}
}
