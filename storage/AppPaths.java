package storage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Centraliza todos os caminhos usados pelo antivirus.
 * Nada aqui e fixo para um usuario ou distribuicao: as pastas vem do
 * "user.home" e do arquivo ~/.config/user-dirs.dirs (padrao XDG do Ubuntu),
 * que entende nomes traduzidos como "Area de Trabalho".
 */
public final class AppPaths {

    private static final Path HOME = Paths.get(System.getProperty("user.home"));
    private static volatile Path storageDir;

    private AppPaths() {}

    // ---------------------------------------------------------------
    // Pasta storage (bancos de dados + quarentena)
    // ---------------------------------------------------------------

    /**
     * Procura a pasta "storage" do projeto. Ordem de busca:
     * 1) propriedade -Dclassicav.storage=/caminho
     * 2) ./src/storage  3) ./storage  4) pasta do codigo compilado (../storage, ../src/storage)
     * Se nada existir, usa ./storage (criada quando necessario).
     */
    public static Path storageDir() {
        Path cached = storageDir;
        if (cached != null) return cached;

        String override = System.getProperty("classicav.storage");
        if (override != null && !override.isBlank()) {
            storageDir = Paths.get(override).toAbsolutePath().normalize();
            return storageDir;
        }

        java.util.List<Path> candidates = new java.util.ArrayList<>();
        candidates.add(Paths.get("src", "storage"));
        candidates.add(Paths.get("storage"));
        try {
            Path code = Paths.get(AppPaths.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path base = Files.isDirectory(code) ? code : code.getParent();
            if (base != null) {
                candidates.add(base.resolve("storage"));
                if (base.getParent() != null) {
                    candidates.add(base.getParent().resolve("storage"));
                    candidates.add(base.getParent().resolve("src").resolve("storage"));
                }
            }
        } catch (URISyntaxException | RuntimeException e) {
            // sem informacao sobre o local do codigo: segue com os candidatos acima
        }

        for (Path c : candidates) {
            if (Files.isRegularFile(c.resolve("badstrings.txt"))
                    || Files.isRegularFile(c.resolve("hashdatabase.txt"))) {
                storageDir = c.toAbsolutePath().normalize();
                return storageDir;
            }
        }
        storageDir = Paths.get("storage").toAbsolutePath().normalize();
        return storageDir;
    }

    public static Path quarantineDir() {
        return storageDir().resolve("quarentine");
    }

    // ---------------------------------------------------------------
    // Pastas do usuario (Ubuntu / XDG)
    // ---------------------------------------------------------------

    public static Path homeDir() {
        return HOME;
    }

    public static Path desktopDir() {
        return xdgDir("XDG_DESKTOP_DIR", "Desktop", "\u00c1rea de Trabalho");
    }

    public static Path downloadsDir() {
        return xdgDir("XDG_DOWNLOAD_DIR", "Downloads", "Transfer\u00eancias");
    }

    public static Path tempDir() {
        return Paths.get(System.getProperty("java.io.tmpdir"));
    }

    /** Pasta usada pelo botao "Scan my Computer". Troque aqui para varrer outra coisa. */
    public static Path defaultScanDir() {
        Path desktop = desktopDir();
        return Files.isDirectory(desktop) ? desktop : HOME;
    }

    private static Path configHome() {
        String xdg = System.getenv("XDG_CONFIG_HOME");
        if (xdg != null && !xdg.isBlank()) return Paths.get(xdg);
        return HOME.resolve(".config");
    }

    private static Path xdgDir(String key, String... fallbackNames) {
        Path cfg = configHome().resolve("user-dirs.dirs");
        if (Files.isRegularFile(cfg)) {
            try {
                for (String line : Files.readAllLines(cfg, StandardCharsets.UTF_8)) {
                    line = line.trim();
                    if (!line.startsWith(key + "=")) continue;
                    String value = line.substring(key.length() + 1).trim();
                    if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    value = value.replace("$HOME", HOME.toString());
                    Path p = Paths.get(value);
                    if (Files.isDirectory(p)) return p;
                }
            } catch (IOException | InvalidPathException e) {
                // arquivo ilegivel: usa os nomes padrao abaixo
            }
        }
        for (String name : fallbackNames) {
            try {
                Path p = HOME.resolve(name);
                if (Files.isDirectory(p)) return p;
            } catch (InvalidPathException e) {
                // locale sem UTF-8 (ex.: LANG=C) nao consegue representar nomes acentuados: tenta o proximo
            }
        }
        return HOME.resolve(fallbackNames[0]);
    }
}
