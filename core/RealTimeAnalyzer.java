package core;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import storage.AppPaths;

public class RealTimeAnalyzer implements Runnable {

    private final Path dir;
    private final ThreatListener listener;
    private volatile boolean running = true;
    private volatile WatchService watchService;
    private Thread thread;

    // caminho -> "tamanho:data". Evita reanalisar o mesmo arquivo a cada evento repetido.
    // So e usado pela thread do monitor, por isso nao precisa de sincronizacao.
    private final Map<String, String> analyzed = new HashMap<>();

    public RealTimeAnalyzer(String dir, ThreatListener listener) {
        this.dir = Paths.get(dir);
        this.listener = listener;
    }

    /** Inicia o monitoramento em uma thread propria (daemon: nao impede o programa de fechar). */
    public synchronized void start() {
        if (thread != null) return;
        thread = new Thread(this, "realtime-" + dir.getFileName());
        thread.setDaemon(true);
        thread.start();
    }

    /** Para de verdade: fecha o WatchService (acorda o take()) e interrompe a thread. */
    public synchronized void stop() {
        running = false;
        WatchService ws = watchService;
        if (ws != null) {
            try {
                ws.close();
            } catch (IOException ignored) {
                // ja fechado
            }
        }
        if (thread != null) thread.interrupt();
    }

    @Override
    public void run() {
        try (WatchService ws = FileSystems.getDefault().newWatchService()) {
            watchService = ws;

            dir.register(ws,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY
            );

            while (running && !Thread.currentThread().isInterrupted()) {
                WatchKey key = ws.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.OVERFLOW) continue;
                    try {
                        handle(dir.resolve((Path) event.context()));
                    } catch (RuntimeException e) {
                        e.printStackTrace(); // um arquivo problematico nao pode matar o monitor
                    }
                }

                if (!key.reset()) break; // pasta removida/inacessivel
            }

        } catch (ClosedWatchServiceException e) {
            // parada solicitada por stop()
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            if (running) listener.onMonitorError(dir.toString(), e.toString());
        }
    }

    private void handle(Path file) {
        String abs = file.toAbsolutePath().toString();
        String name = file.getFileName().toString().toLowerCase(Locale.ROOT);

        // Filtros importantes (.part/.crdownload = downloads incompletos do Firefox/Chrome)
        if (!Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)) return;
        if (name.endsWith(".info") || name.endsWith(".tmp") || name.endsWith(".part")
                || name.endsWith(".crdownload") || name.endsWith(".swp")) return;
        if (file.toAbsolutePath().normalize().startsWith(AppPaths.storageDir())) return;

        if (!waitUntilStable(file)) return; // ainda sendo gravado: um proximo evento MODIFY chamara de novo

        String signature;
        try {
            signature = Files.size(file) + ":" + Files.getLastModifiedTime(file).toMillis();
        } catch (IOException e) {
            return;
        }
        if (signature.equals(analyzed.get(abs))) return; // ja analisado nesta versao

        if (analyzed.size() > 10_000) analyzed.clear();
        analyzed.put(abs, signature);

        if (Antivirus.scanFile(abs)) {
            listener.onThreatDetected(abs);
        }
    }

    // Espera o tamanho parar de mudar (arquivo recem-copiado/baixado ainda esta sendo escrito)
    private boolean waitUntilStable(Path file) {
        try {
            long modified = Files.getLastModifiedTime(file).toMillis();
            if (System.currentTimeMillis() - modified > 2000) return true; // parado ha tempo

            long last = -1;
            for (int i = 0; i < 20; i++) {
                long size = Files.size(file);
                if (size == last) return true;
                last = size;
                Thread.sleep(300);
            }
            return false;
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
