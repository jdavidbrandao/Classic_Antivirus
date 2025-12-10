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
	
	public Boolean isHashMalicious(String hash) {
		getdb db = new getdb();
		ArrayList<String> hashesdb = db.getHashDb();
		for (String h : hashesdb) {
			if (h.equals(hash)) {
				return true;
				}
			}
		
		return false;
	}
	
	public Boolean isHashMalicious(byte[] fileContent) {
		AddHash geth = new AddHash();
		ArrayList<String> hashes = new ArrayList<>();
		
		hashes.add(geth.retornaHash(fileContent, "MD5"));
		hashes.add(geth.retornaHash(fileContent, "SHA-1"));
		hashes.add(geth.retornaHash(fileContent, "SHA-256"));
		
		Boolean isMal = false;
		for (String hash : hashes) {
			isMal = isHashMalicious(hash);
			if (isMal) {
				return true;
			}
		}
		return false;
	}
}
		

	
