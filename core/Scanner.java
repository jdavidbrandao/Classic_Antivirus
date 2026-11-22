package core;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import storage.AppPaths;

public class Scanner {

    private final String initFolder;
    private final ArrayList<String> files = new ArrayList<>();
    private final ScanLogger logger;

    // Pasta padrao: Desktop do usuario atual (Ubuntu/XDG), ou a home se nao existir
    public Scanner() {
        this(AppPaths.defaultScanDir().toString(), null);
    }

    public Scanner(String initFolder, ScanLogger logger) {
        this.initFolder = initFolder;
        this.logger = logger;
    }

    public Scanner(ScanLogger logger) {
        this(AppPaths.defaultScanDir().toString(), logger);
    }

    private void log(String msg) {
        if (logger != null) {
            logger.log(msg);
        }
    }

    private void spider() {
        Path start = Paths.get(initFolder);
        if (!Files.isDirectory(start)) {
            log("Directory not found: " + start);
            return;
        }

        // Nunca varre a propria pasta de bancos/quarentena (senao reacusaria os arquivos quarentenados)
        Path skip = AppPaths.storageDir().toAbsolutePath().normalize();

        try {
            // walkFileTree nao segue links simbolicos e continua mesmo com pastas sem permissao
            Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (dir.toAbsolutePath().normalize().equals(skip)) return FileVisitResult.SKIP_SUBTREE;
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (Thread.currentThread().isInterrupted()) return FileVisitResult.TERMINATE;
                    if (!attrs.isRegularFile()) return FileVisitResult.CONTINUE; // ignora links, sockets, etc.

                    String abs = file.toAbsolutePath().toString();
                    log("Analyzing: " + abs);

                    // Le o arquivo UMA vez e roda todos os analisadores
                    if (Antivirus.scanFile(abs)) {
                        log("[!] THREAT: " + abs);
                        files.add(abs);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    log("Skipped (no access): " + file);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log("Scan error: " + e.getMessage());
        }
    }

    public ArrayList<String> getFiles() {
        return files;
    }

    public void setFiles() {
        spider();
    }
}
