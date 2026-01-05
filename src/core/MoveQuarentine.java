package core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MoveQuarentine {
	
	public static boolean moveToQua(String path) {
		try {
			Files.move(Paths.get(path), Paths.get("src", "storage", "quarentine"));
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}
