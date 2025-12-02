package storage;

import java.io.File;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.ArrayList;

public class getdb {
	private Scanner sc = null;
	private ArrayList<String> hashes = new ArrayList<>();
	
	public ArrayList<String> getDb() throws Exception {
		File db = new File(Paths.get("src", "storage", "hashdatabase.txt").toString());
		
		sc = new Scanner(db);
		while (sc.hasNextLine()) {
			hashes.add(sc.nextLine());
		}
		return hashes;
	}
}
