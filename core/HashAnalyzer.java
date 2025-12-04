package core;

import storage.getdb;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.ArrayList;

public class HashAnalyzer extends Antivirus {
	
	public HashAnalyzer(String path) {
		super(path);
	}
	
	public byte[] getFileContent() {
	    try {
	        byte[] data = Files.readAllBytes(Paths.get(super.path));
	        return data;
	    } catch (IOException e){
	        System.out.println("Não foi possível ler o arquivo: " + e.getMessage());
	    }
	    return null;        
	}
	
	public Boolean isHashMalicious(String hash) throws Exception {
		getdb db = new getdb();
		ArrayList<String> hashesdb = db.getHashDb();
		for (String h : hashesdb) {
			if (h.equals(hash)) {
				return true;
				}
			}
		
		return false;
	}
	
	
	
}
