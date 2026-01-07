package core;
 
import java.util.ArrayList;
import java.io.File;
import java.nio.file.*;
 
public class Scanner {
	private String initFolder;
	private ArrayList<String> files = new ArrayList<>();
	private ArrayList<String> folders = new ArrayList<>();
 
	public Scanner() {
		initFolder = Paths.get("/home", "kali", "Desktop").toString();
	}
 
	public Scanner(String initFolder) {
		this.initFolder = initFolder;
	}
 
	public void spider(String folder) {
		File diretorio = new File(folder);
		File[] arquivos = diretorio.listFiles();
 
		if (arquivos != null) {
			for (File arquivo : arquivos) {
				if (Files.isSymbolicLink(arquivo.toPath())) continue;
				if (arquivo.isDirectory() && !folders.contains(arquivo.getAbsolutePath()) ) {
					folders.add(arquivo.getAbsolutePath());
					spider(arquivo.getAbsolutePath());
					continue;
				}
				else if(arquivo.isFile() && !files.contains(arquivo.getAbsolutePath())) {
					boolean malicious = false;
					Antivirus[] scm = Antivirus.getScanM();
					for (Antivirus method : scm) {
						Antivirus.path.set(arquivo.getAbsolutePath());
						Antivirus.getFileContent();
						if (method.isMalicious()) {
							malicious = true;
						}
					}
					if (malicious == true) {
						files.add(arquivo.getAbsolutePath());
					}
					continue;
				}
			}
 
		}
	}
 
	public void spider() {
		spider(initFolder);
	}
	
 
	public ArrayList<String> getFiles() {
		spider();
		return files;
	}
}