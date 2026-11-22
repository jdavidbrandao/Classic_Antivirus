package storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Acesso aos bancos de dados em texto (hashes, strings e assinaturas).
 * - Le cada arquivo uma unica vez e so recarrega se ele mudar (cache por data/tamanho).
 * - Fecha os arquivos corretamente, ignora linhas vazias/comentarios (#).
 * - Nunca retorna null: se o arquivo nao existir, retorna lista vazia e avisa uma vez no stderr.
 */
public class getdb {

    private static final class Cached {
        final long mtime;
        final long size;
        final ArrayList<String> lines;
        Set<String> lowerSet; // criado so quando pedido (hashes)

        Cached(long mtime, long size, ArrayList<String> lines) {
            this.mtime = mtime;
            this.size = size;
            this.lines = lines;
        }
    }

    private static final Map<String, Cached> CACHE = new HashMap<>();
    private static final Set<String> WARNED = new HashSet<>();

    private static synchronized Cached load(String fileName) {
        Path file = AppPaths.storageDir().resolve(fileName);
        try {
            long mtime = Files.getLastModifiedTime(file).toMillis();
            long size = Files.size(file);

            Cached cached = CACHE.get(fileName);
            if (cached != null && cached.mtime == mtime && cached.size == size) {
                return cached;
            }

            // new String(...) nao falha com bytes invalidos (diferente de readAllLines)
            String text = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
            if (text.startsWith("\uFEFF")) text = text.substring(1); // remove BOM

            ArrayList<String> lines = new ArrayList<>();
            for (String line : text.split("\\R")) {
                String t = line.trim();
                if (t.isEmpty() || t.startsWith("#")) continue;
                lines.add(t);
            }
            Cached fresh = new Cached(mtime, size, lines);
            CACHE.put(fileName, fresh);
            return fresh;

        } catch (IOException e) {
            if (WARNED.add(fileName)) {
                System.err.println("[ClassicAV] Banco de dados nao encontrado: " + file.toAbsolutePath()
                        + " (use -Dclassicav.storage=/caminho/para/storage)");
            }
            return new Cached(-1, -1, new ArrayList<>());
        }
    }

    public ArrayList<String> getHashDb() {
        return new ArrayList<>(load("hashdatabase.txt").lines);
    }

    /** Hashes em minusculas, prontas para consulta rapida. */
    public Set<String> getHashSet() {
        Cached c = load("hashdatabase.txt");
        synchronized (getdb.class) {
            if (c.lowerSet == null) {
                Set<String> s = new HashSet<>();
                for (String h : c.lines) s.add(h.toLowerCase(Locale.ROOT));
                c.lowerSet = Collections.unmodifiableSet(s);
            }
            return c.lowerSet;
        }
    }

    public ArrayList<String> getStringsDb() {
        return new ArrayList<>(load("badstrings.txt").lines);
    }

    public ArrayList<String> getSignDb() {
        return new ArrayList<>(load("badsignatures.txt").lines);
    }
}
