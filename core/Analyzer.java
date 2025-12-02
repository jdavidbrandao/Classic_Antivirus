package core;

import storage.getdb;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.ArrayList;

public class Analyzer {
	private String path;
	
	public Analyzer(String path) {
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
	
	public Boolean isHashMalicious(String hash) throws Exception {
		getdb db = new getdb();
		ArrayList<String> hashesdb = db.getDb();
		for (String h : hashesdb) {
			if (h.equals(hash)) {
				return true;
				}
			}
		
		return false;
	}
	
}
