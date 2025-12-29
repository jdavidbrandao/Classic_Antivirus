package core;

import storage.getdb;
import java.util.ArrayList;
import java.util.HexFormat;

public class SignatureAnalyzer extends Antivirus{
	getdb access = new getdb(); // Instancia de getdb para acessar o banco de dados
	
	public boolean isMalicious() {
		byte[] content = Antivirus.fileContent; // Pega o conteudo do arquivo
		ArrayList<String> signatureDb = access.getSignDb(); // Pega o db de assinaturas hexadecimal
		
		String hexContent = HexFormat.of().formatHex(content).toUpperCase(); // Converte o conteudo do arquivo em hexadecimal
		
		for (String sign : signatureDb) {
			if (hexContent.contains(sign)) { // Percorre o db verificando se alguma assinatura esta no arquivo escaneado
				return true; // Se contem, retorna true (Malicioso)
			}
		}
		return false; // Se nao contem, retorna false (Seguro)
	}
}

