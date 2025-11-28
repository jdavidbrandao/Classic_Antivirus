package core;
 
import java.util.ArrayList;
import java.io.File;
import java.nio.file.*;
 
public class Varredura {
	private String initFolder; // Pasta inicial -> Onde o scanner vai começar a varredura
	private ArrayList<String> files = new ArrayList<>(); // ArrayList para armazenar os arquivos encontrados
	private ArrayList<String> folders = new ArrayList<>(); // ArrayList para armazenar os diretórios encontrados
 
	public Varredura() { // Construtor sem argumentos, inicializa initFolder na pasta raíz
		initFolder = "/"; 
	}
 
	public Varredura(String initFolder) { // Sobrecarga do construtor, nesse caso a pasta inicial é outra pre-definida pelo usuário
		this.initFolder = initFolder;
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
				else if(arquivo.isFile() && !files.contains(arquivo.getAbsolutePath())) {
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

