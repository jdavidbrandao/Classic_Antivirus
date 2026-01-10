package core;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

public class RealTimeAnalyzer implements Runnable {

    private final Path dir;
    private final ThreatListener listener;
    private volatile boolean running = true;
    private final Set<String> processedFiles = new HashSet<>();

    public RealTimeAnalyzer(String dir, ThreatListener listener) {
        this.dir = Paths.get(dir);
        this.listener = listener;
    }

    @Override
    public void run() {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {

            dir.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY
            );

            while (running && !Thread.currentThread().isInterrupted()) {

                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {

                    if (event.kind() == StandardWatchEventKinds.OVERFLOW) continue;

                    Path file = dir.resolve((Path) event.context());
                    String absPath = file.toAbsolutePath().toString();

                    // Filtros importantes
                    if (!Files.isRegularFile(file)) continue;
                    if (absPath.endsWith(".INFO") || absPath.endsWith(".tmp")) continue;
                    if (processedFiles.contains(absPath)) continue;

                    if (analyzeFile(file)) {
                        processedFiles.add(absPath);
                        listener.onThreatDetected(absPath);
          
                    }
                }

                key.reset();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        running = false;
    }

    private boolean analyzeFile(Path file) {
        Antivirus.path.set(file.toString());
        Antivirus.getFileContent();

        Antivirus[] scanners = Antivirus.getScanM();
        for (Antivirus sc : scanners) {
            if (sc.isMalicious()) return true;
        }

        return false;
    }
}
