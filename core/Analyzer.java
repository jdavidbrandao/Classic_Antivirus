package core;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class Analyzer {
	private static String path;
	
	public Analyzer(String path) {
		Analyzer.path = path;
	}
	
	public static byte[] getFileContent() {
		try {
			byte[] data = Files.readAllBytes(Paths.get(path));
			return data;
		} catch (IOException e){
			System.out.println("Não foi possível ler o arquivo");
		}
		return null;		
	}
}
