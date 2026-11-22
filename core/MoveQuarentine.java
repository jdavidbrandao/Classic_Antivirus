package core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import storage.AppPaths;

public class MoveQuarentine {

    /** Uma entrada da quarentena. */
    public static final class Entry {
        public final String origin;      // onde o arquivo estava
        public final String quarantined; // onde ele esta agora
        public final String date;

        Entry(String origin, String quarantined, String date) {
            this.origin = origin;
            this.quarantined = quarantined;
            this.date = date;
        }
    }

    private static Path quarentineDir() {
        return AppPaths.quarantineDir();
    }

    /**
     * Move o arquivo para a quarentena.
     * @return caminho do arquivo quarentenado, ou "" se a quarentena FALHOU
     *         (o arquivo continua onde estava; quem chama deve avisar o usuario).
     */
    public static String moveToQua(String path, String date) {
        Path source = Paths.get(path);
        Path dir = quarentineDir();

        try {
            Files.createDirectories(dir);

            for (int attempt = 0; attempt < 3; attempt++) {
                String uuid = UUID.randomUUID().toString();
                Path destiny = dir.resolve(uuid);
                Path info = dir.resolve(uuid + ".INFO");

                try {
                    Files.move(source, destiny);
                } catch (FileAlreadyExistsException e) {
                    continue; // UUID repetido (praticamente impossivel): sorteia outro. NAO apaga o original.
                }

                // Remove permissao de execucao do arquivo quarentenado
                try {
                    Files.setPosixFilePermissions(destiny, PosixFilePermissions.fromString("rw-------"));
                } catch (UnsupportedOperationException | IOException ignored) {
                    // sistema sem permissoes POSIX
                }

                // Um valor por linha (antes "::" e "=" quebravam com certos nomes de pasta)
                String threatInfos = "wfound=" + source.toAbsolutePath() + "\n"
                                   + "nlocal=" + destiny + "\n"
                                   + "date=" + date + "\n";
                try {
                    Files.write(info, threatInfos.getBytes(StandardCharsets.UTF_8));
                } catch (IOException e) {
                    // Sem metadados nao da para restaurar: devolve o arquivo ao lugar de origem
                    try {
                        Files.move(destiny, source);
                    } catch (IOException ignored) {
                        // fica na quarentena, sem .INFO
                    }
                    System.err.println("[ClassicAV] Falha ao gravar metadados da quarentena: " + e);
                    return "";
                }
                return destiny.toString();
            }
        } catch (IOException e) {
            System.err.println("[ClassicAV] Falha ao mover para quarentena: " + e);
        }
        return "";
    }

    /** Garante que o caminho esta DENTRO da pasta de quarentena (um .INFO adulterado nao apaga arquivos de fora). */
    private static Path insideQuarentine(String path) {
        Path dir = quarentineDir().toAbsolutePath().normalize();
        Path p = Paths.get(path).toAbsolutePath().normalize();
        if (p.equals(dir) || !p.startsWith(dir)) return null;
        return p;
    }

    /** Apaga definitivamente o arquivo quarentenado e seu .INFO. */
    public static boolean removeFromQua(String path) {
        Path p = insideQuarentine(path);
        if (p == null) return false;
        try {
            Files.deleteIfExists(p);
            Files.deleteIfExists(Paths.get(p + ".INFO"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Restaura o arquivo para o local de origem.
     * Se ja existir algo la, restaura como "<nome>.restored".
     * @return caminho final, ou "" se falhou.
     */
    public static String restoreFromQua(String path) {
        Path p = insideQuarentine(path);
        if (p == null) return "";

        Path infoFile = Paths.get(p + ".INFO");
        Entry entry = readInfo(infoFile);
        if (entry == null) return "";

        try {
            Path target = Paths.get(entry.origin);
            if (target.getParent() != null) Files.createDirectories(target.getParent());

            Path finalTarget = target;
            int n = 1;
            while (Files.exists(finalTarget, LinkOption.NOFOLLOW_LINKS)) {
                finalTarget = target.resolveSibling(target.getFileName() + ".restored" + (n == 1 ? "" : String.valueOf(n)));
                n++;
            }

            Files.move(p, finalTarget);
            Files.deleteIfExists(infoFile);
            return finalTarget.toString();
        } catch (IOException e) {
            return "";
        }
    }

    public static String getOriginPath(String path) {
        Entry e = readInfo(Paths.get(path + ".INFO"));
        return e == null ? "" : e.origin;
    }

    /** Lista tudo que esta em quarentena (ordenado por data). */
    public static List<Entry> listEntries() {
        List<Entry> out = new ArrayList<>();
        Path dir = quarentineDir();
        if (!Files.isDirectory(dir)) return out; // pasta ainda nao existe: lista vazia (antes dava NullPointerException)

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.INFO")) {
            for (Path info : stream) {
                Entry e = readInfo(info);
                if (e != null && Files.exists(Paths.get(e.quarantined))) out.add(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.sort(Comparator.comparing(x -> x.date));
        return out;
    }

    private static Entry readInfo(Path infoFile) {
        try {
            String content = new String(Files.readAllBytes(infoFile), StandardCharsets.UTF_8);
            Map<String, String> values = parseInfo(content);

            String origin = values.get("wfound");
            if (origin == null || origin.isEmpty()) return null;

            // O arquivo quarentenado tem o mesmo nome do .INFO sem a extensao
            // (assim tambem funciona com quarentenas criadas pela versao antiga)
            String name = infoFile.getFileName().toString();
            if (!name.endsWith(".INFO")) return null;
            String quarantined = quarentineDir().resolve(name.substring(0, name.length() - 5)).toString();

            return new Entry(origin, quarantined, values.getOrDefault("date", ""));
        } catch (IOException e) {
            return null;
        }
    }

    // Aceita o formato novo (uma chave=valor por linha) e o antigo (wfound=..::nlocal=..::date=..)
    private static Map<String, String> parseInfo(String content) {
        Map<String, String> values = new HashMap<>();
        String[] parts = (content.contains("::") && !content.contains("\n"))
                ? content.split("::")
                : content.split("\\R");
        for (String part : parts) {
            int i = part.indexOf('=');
            if (i > 0) values.put(part.substring(0, i).trim(), part.substring(i + 1));
        }
        return values;
    }
}
