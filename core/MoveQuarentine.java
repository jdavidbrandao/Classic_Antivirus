package core;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

public class MoveQuarentine {
	private static String quarentine = "src/storage/quarentine";
	
	
	private static void createQuarentine() {
		File diretorio = new File(MoveQuarentine.quarentine);
		diretorio.mkdir();
	}
	
	private static String generate_UUID() {
		return UUID.randomUUID().toString();
	}
	
	public static String moveToQua(String path, String date) {
		String uuid = MoveQuarentine.generate_UUID();
		Path destiny = Paths.get(MoveQuarentine.quarentine + "/" + uuid);
		Path InfosDestiny = Paths.get(MoveQuarentine.quarentine + "/" + uuid + ".INFO");
		
		String threatInfos = String.format("wfound=%s:nlocal=%s:date=%s", path, destiny.toString(), date);
		
		if (!Files.exists(Paths.get(MoveQuarentine.quarentine))) {
			MoveQuarentine.createQuarentine();
		}
		try {
			Files.move(Paths.get(path), destiny);
			
			Files.write(InfosDestiny, threatInfos.getBytes());
			return destiny.toString();
		}
		catch(FileAlreadyExistsException e) {
			File malware = new File(path);
			malware.delete();
			return "SAMEFILEFOUND";
		}
		
		catch (IOException e) {
			System.out.println(e);
			return "";
		}
	}
	
	public static void removeFromQua(String path) {
		File malware = new File(path);
		File malwareInfo = new File(path + ".INFO");
		
		if (malware.exists() && malwareInfo.exists()) {
			malware.delete();
			malwareInfo.delete();
		}
	}
	
	public static void restoreFromQua(String path) {
		File suspiciousFileInfos = new File(path + ".INFO");
		
		try {
			String content = Files.readString(suspiciousFileInfos.toPath());
			String source = content.split(":")[0].split("=")[1];
			
			Files.move(Paths.get(path), Paths.get(source));
			suspiciousFileInfos.delete();
		} catch(IOException e) {
			return;
		}
	}

}
