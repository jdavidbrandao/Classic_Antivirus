package core;

import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import storage.getdb;

public class StringAnalyzer extends Antivirus {
	
	public StringAnalyzer(String path) {
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
	
	public ArrayList<String> extractString(byte[] data, int minLength){
		ArrayList<String> result = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		
		for (byte b : data) {
			if (b >= 32 && b <= 126) {
				sb.append((char) b);
			} else {
				if (sb.length() >= minLength) {
					result.add(sb.toString());
				}
			sb.setLength(0);
			}
		}
		
		if (sb.length() >= minLength) {
			result.add(sb.toString());
		}
		return result;
	}
	
	public Boolean isStringMalicious(String str) {
		getdb db = new getdb();

		ArrayList<String> stringsDb = db.getStringsDb();

		
		for (String mw : stringsDb) {
			if (str.toLowerCase().contains(mw)) {
				return true;
			}
		}
		return false;
		
	}
	
}
