package core;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public class DoScan {
    public static ArrayList<Object> scanComputer() {
    	ArrayList<Object> result = new ArrayList<>();
    	Thread t = new Thread(new Scanner());
    	t.run();
    	if (Scanner.detected) {
    		result.add(true);
    		result.add(Scanner.maliciousPath);
    	} else {
    		result.add(false);
    		result.add(Scanner.maliciousPath);
    	}
    	return result;
    }
    
    public static boolean removeFile(String path) {
    	File arquivo = new File(path);
    	arquivo.delete();
		if (arquivo.exists()) {
			return false;
		}
		return true;
    }
    
    public static HashMap<Integer, String> scanFolder(File dir) {
    	File[] arquivos = dir.listFiles();
    	HashMap<Integer, String> result = new HashMap<>();
    	
    	int counter = 0;
    	for (File arquivo : arquivos) {
    		if (Files.isSymbolicLink(arquivo.toPath())) {
    			continue;
    		}
	    	
    		else if (arquivo.isFile()) {
		    	Antivirus.path.set(arquivo.getAbsolutePath());
		    	Antivirus.getFileContent();
		    	
		    	Antivirus[] scanner = new Antivirus[3];
		    	scanner[0] = new HashAnalyzer();
		    	scanner[1] = new SignatureAnalyzer();
		    	scanner[2] = new HeuristicsAnalyzer();
		    	
		    	if (scanner[0].isMalicious()) {
		    		result.put(counter, arquivo.getAbsolutePath());
		    		counter++;
		    		continue;
		    	} else if (scanner[1].isMalicious()) {
		    		result.put(counter, arquivo.getAbsolutePath());
		    		counter++;
		    		continue;
		    	}
		    	if (scanner[2].isMalicious()) {
		    		result.put(counter, arquivo.getAbsolutePath());
		    	}
		    	counter++;
		    	continue;
    		}
    	}
    	return result;
    }
}
