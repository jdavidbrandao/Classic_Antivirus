package core;

import storage.getdb;
import java.util.ArrayList;

// Hash aqui e uma 'assinatura' de um arquivo
// Ou seja, vamos comparar a assinatura do arquivo escaneado com a assinatura de malwares conhecidos

public class HashAnalyzer extends Antivirus {
	
	public Boolean isHashMalicious(String hash) { // Funcao que verifica se uma hash esta no db de hashes maliciosas
		getdb db = new getdb();
		ArrayList<String> hashesdb = db.getHashDb(); // Pega as hashes
		
		for (String h : hashesdb) {
			if (h.equals(hash)) {	// Compara a hash passada com as hashes no db
				return true; 		// Se a hash esta no db entao ela e de um malware
				}
			}
		
		return false;
	}
	
	public boolean isMalicious() {
		AddHash geth = new AddHash();
		ArrayList<String> hashes = new ArrayList<>(); // ArrayList com hashes do arquivo escaneado
		
		hashes.add(geth.retornaHash(Antivirus.fileContent, "MD5"));
		hashes.add(geth.retornaHash(Antivirus.fileContent, "SHA-1"));	// Calcula a hash usando 3 algoritmos distintos
		hashes.add(geth.retornaHash(Antivirus.fileContent, "SHA-256"));
		
		Boolean isMal = false;
		for (String hash : hashes) {
			isMal = isHashMalicious(hash);	// Se alguma das hashes for maliciosa, retorna true
			if (isMal) {
				return true;
			}
		}
		return false;
	}
}
		

	
