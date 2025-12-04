package storage;

import java.io.File;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.IOException;

public class getdb {
	private Scanner sc = null;
	private ArrayList<String> wordlist = new ArrayList<>();
	
	public ArrayList<String> getHashDb() {
		File db = new File(Paths.get("src", "storage", "hashdatabase.txt").toString());
		
		try {
			sc = new Scanner(db);
			while (sc.hasNextLine()) {
				wordlist.add(sc.nextLine());
			}
			return wordlist;
		} catch (IOException e) {
			return null;
		}
	}
	
	public ArrayList<String> getStringsDb() {
		File db = new File(Paths.get("src", "storage", "badstrings.txt").toString());
		
		
		try {
			sc = new Scanner(db);
			while (sc.hasNextLine()) {
				wordlist.add(sc.nextLine());
			}
			return wordlist;
		} catch (IOException e) {
			return null;
		}
	}
}
