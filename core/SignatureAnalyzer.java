package core;

import storage.getdb;

import java.util.ArrayList;

public class SignatureAnalyzer extends Antivirus {

    private static final int MIN_SIGNATURE_BYTES = 4; // assinaturas menores geram falso positivo demais

    private final getdb access = new getdb();

    public boolean isMalicious() {
        byte[] content = Antivirus.fileContent.get();
        if (content == null) return false;

        ArrayList<String> signatureDb = access.getSignDb();
        for (String sign : signatureDb) {
            byte[] pattern = hexToBytes(sign);
            if (pattern == null || pattern.length < MIN_SIGNATURE_BYTES) continue; // linha invalida/curta

            // Busca por BYTES (antes era por texto hex, que podia casar no meio de um byte)
            if (indexOf(content, pattern) >= 0) {
                UploadFile.uploadFile(Antivirus.path.get(), "SHA-256");
                return true;
            }
        }
        return false;
    }

    private static byte[] hexToBytes(String hex) {
        if (hex == null) return null;
        String h = hex.replace(" ", "");
        if (h.isEmpty() || h.length() % 2 != 0) return null;
        byte[] out = new byte[h.length() / 2];
        for (int i = 0; i < out.length; i++) {
            int hi = Character.digit(h.charAt(2 * i), 16);
            int lo = Character.digit(h.charAt(2 * i + 1), 16);
            if (hi < 0 || lo < 0) return null;
            out[i] = (byte) ((hi << 4) | lo);
        }
        return out;
    }

    private static int indexOf(byte[] data, byte[] pattern) {
        byte first = pattern[0];
        int max = data.length - pattern.length;
        outer:
        for (int i = 0; i <= max; i++) {
            if (data[i] != first) continue;
            for (int j = 1; j < pattern.length; j++) {
                if (data[i + j] != pattern[j]) continue outer;
            }
            return i;
        }
        return -1;
    }
}
