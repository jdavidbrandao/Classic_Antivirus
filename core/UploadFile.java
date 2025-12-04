package core;

import java.nio.file.Files;
import java.nio.file.Paths;

public class UploadFile {
	
	public boolean uploadFile(String caminhoArquivo, String algoritmo){
	    try{
	        byte[] dados = Files.readAllBytes(Paths.get(caminhoArquivo));
	        String hashGerada = new Addhash().retorna_hash(dados, algoritmo);
	        if(hashGerada == null){
	            return false;
	        }
	
	        boolean salvou = new Addhash().salvaHash(hashGerada);
	        return salvou;
	    }
	    catch(Exception e){
	        e.printStackTrace();
	        return false;
	    }
	}
}
