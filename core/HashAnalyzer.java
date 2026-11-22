package core;

import java.util.Locale;

import storage.getdb;

// Hash aqui e uma 'assinatura' de um arquivo.
// Comparamos a assinatura do arquivo escaneado com a de malwares conhecidos.

public class HashAnalyzer extends Antivirus {

    private final getdb db = new getdb();

    // Verifica se um hash esta no banco de hashes maliciosos (consulta em conjunto: O(1))
    public Boolean isHashMalicious(String hash) {
        if (hash == null) return false;
        return db.getHashSet().contains(hash.toLowerCase(Locale.ROOT));
    }

    public boolean isMalicious() {
        byte[] data = Antivirus.fileContent.get();
        if (data == null) return false;

        AddHash geth = new AddHash();
        for (String algoritmo : new String[] { "MD5", "SHA-1", "SHA-256" }) {
            if (isHashMalicious(geth.retornaHash(data, algoritmo))) {
                return true; // se algum dos hashes for malicioso, para por aqui
            }
        }
        return false;
    }
}
