package core;

import java.nio.file.*;
import storage.getdb;
import java.util.HashMap;
import java.util.ArrayList;

public class MIMEAnalyzer extends Antivirus {
	protected String extension;
	protected String MIME;
	
	public MIMEAnalyzer(String path) {
		super(path);
	}
	
	public String getMIME() {
		Path p = Paths.get(super.path);
		String MIME = null;
		try {
			MIME = Files.probeContentType(p);
		} catch (Exception e) {
			System.out.println("Não foi possivel abrir o arquivo.");
			return null;
		}
		if (MIME != null) {
			System.out.println("Mime: " + MIME);
			this.MIME = MIME;
			return MIME;
		} else {
			System.out.println("MIME não encontrado.");
		}
		return null;
	}
	
	public String getExtension(Path path) {
		String fileName = path.getFileName().toString();
		int index = fileName.lastIndexOf(".");
		
		if (index != -1 && index < fileName.length() - 1) {
			this.extension = fileName.substring(index + 1);
			return extension;
		} else {
			return "S/E";
		}
		
	}
	
	public boolean isMimeMalicious() {
		getdb db = new getdb();
		HashMap<String, String> mimeTypes = new HashMap<>();
		ArrayList<String> pureDb = db.getMimeDb();
		
		for (String palavra : pureDb) {
			if (palavra.startsWith("#") || palavra.isEmpty()) {
				continue;
			}
			String[] parse = palavra.split("=");
			mimeTypes.put(parse[0], parse[1]);
		}
		if (mimeTypes.get(extension) != MIME) {
			return true;
		}
		return false;
		
	}
}
