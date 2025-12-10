package core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class Antivirus {
	protected String path;
	
	public Antivirus(String path) {
		this.path = path;
	}
	
	public byte[] getFileContent() {
	    try {
	        byte[] data = Files.readAllBytes(Paths.get(path));
	        return data;
	    } catch (IOException e){
	        System.out.println("Não foi possível ler o arquivo: " + e.getMessage());
	    }
	    return null; 
	}
}
