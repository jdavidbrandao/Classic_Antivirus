package core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.Locale;

import storage.AppPaths;
import storage.getdb;

public class AddHash {

    // Recebe um array de bytes e devolve o hash em hexadecimal (minusculo)
    public String retornaHash(byte[] dados, String algoritmo) {
        try {
            MessageDigest motorHash = MessageDigest.getInstance(algoritmo); // local: seguro para varias threads
            motorHash.update(dados);
            byte[] hashBytes = motorHash.digest();

            StringBuilder textoHash = new StringBuilder();
            for (byte tmp : hashBytes) {
                int valor = tmp & 0xff; // byte (-128..127) -> 0..255
                String hex = Integer.toHexString(valor);
                if (hex.length() == 1) {
                    hex = "0" + hex;
                }
                textoHash.append(hex);
            }
            return textoHash.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Acrescenta o hash no final de hashdatabase.txt, sem duplicar e sem corrida entre threads
    public boolean salvaHash(String textoHash) {
        if (textoHash == null || textoHash.isBlank()) return false;
        synchronized (AddHash.class) {
            try {
                if (new getdb().getHashSet().contains(textoHash.trim().toLowerCase(Locale.ROOT))) {
                    return true; // ja esta no banco
                }
                Path db = AppPaths.storageDir().resolve("hashdatabase.txt");
                if (db.getParent() != null) Files.createDirectories(db.getParent());
                try (BufferedWriter w = Files.newBufferedWriter(db, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                    w.write(textoHash.trim());
                    w.newLine();
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
