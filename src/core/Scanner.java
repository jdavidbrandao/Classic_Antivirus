package core;
 
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
 
public class Scanner implements Runnable {
	private String initFolder = "/"; // Pasta inicial -> Onde o scanner vai começar a varredura
	private ArrayList<String> files = new ArrayList<>(); // ArrayList para armazenar os arquivos encontrados
	private ArrayList<String> folders = new ArrayList<>(); // ArrayList para armazenar os diretórios encontrados
	public static boolean detected = false;
	public static String maliciousPath;
 
	public void run() {
		spider();
		ArrayList<String> f = getFiles();
		for (String path : f) {
			try {
				Antivirus.path.set(path);
				Antivirus.getFileContent();
				Antivirus[] scans = Antivirus.getScanM();
				for (Antivirus s : scans) {
					if (s.isMalicious() == true) {
						Scanner.detected = true;
						Scanner.maliciousPath = path;
						Thread.currentThread().interrupt();
					}
				}
			} finally {
				Antivirus.path.remove();
				Antivirus.fileContent.remove();
			}
		}
	}
	
	public Scanner() { // Construtor sem argumentos, inicializa initFolder na pasta raíz
		initFolder = "/"; 
	}
 
	public Scanner(String initFolder) { // Sobrecarga do construtor, nesse caso a pasta inicial é outra pre-definida pelo usuário
		this.initFolder = initFolder;
	}
	
	public static boolean isELF(Path path) {
	    byte[] magic = new byte[4];
	    try (InputStream is = Files.newInputStream(path)) {
	        if (is.read(magic) != 4) return false;
	    } catch (IOException e) {
			return false;
		}
	    return magic[0] == 0x7F &&
	           magic[1] == 'E' &&
	           magic[2] == 'L' &&
	           magic[3] == 'F';
	}
	
	public boolean isScannable(String path) {
		int score = 0;
		String[] sensitiveDirs = {"/tmp", "/var/tmp", "/home/kali", ".config", "/usr/share/backgrounds"};
		for (String sd : sensitiveDirs) {
			if (path.contains(sd)) {
				score++;
				break;
			}
		}
		Path p = Paths.get(path);
		if (isELF(p)) {
			if (Files.isExecutable(p)) {
				score++;
			}
			score++;
		}
		if (score >= 2) {
			return true;
		}
		return false;
		
	}
 
	public void spider(String folder) { // Método spider, vai partir da pasta passada pelo usuário e vai varrer tudo que ele encontrar
		File diretorio = new File(folder);
		File[] arquivos = diretorio.listFiles();
 
		if (arquivos != null) {
			for (File arquivo : arquivos) { 
				if (Files.isSymbolicLink(arquivo.toPath())) continue; // Se o arquivo for um link simbólico, pula ele (Evitar uma recursão infinita)
				if (arquivo.isDirectory() && !folders.contains(arquivo.getAbsolutePath()) ) {
					folders.add(arquivo.getAbsolutePath());
					spider(arquivo.getAbsolutePath()); // Se o "arquivo" do diretório listado for um diretório, escanea também o que tem dentro dele
					continue;						   // recursivamente.
				}
				else if(arquivo.isFile() && !files.contains(arquivo.getAbsolutePath()) && isScannable(arquivo.getAbsolutePath())) {
					System.out.println("Arquivo: " + arquivo.getAbsolutePath());
					files.add(arquivo.getAbsolutePath()); // Se for apenas um arquivo, salva ele no ArrayList
					continue;
				}
			}
 
		}
	}
 
	public void spider() {  // Sobrecarga do metodo spider, inicializa a varredura a partir de initFolder
		spider(initFolder);
	}
 
	public ArrayList<String> getFiles() { // Método para retornar o ArrayList com os arquivos encontrados pelo spider
		return files;
	}
	
	
}
