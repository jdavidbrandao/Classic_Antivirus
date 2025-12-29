package core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import java.nio.file.FileSystems;
import java.nio.file.StandardWatchEventKinds;
import java.io.IOException;
import java.nio.file.WatchKey;
import java.nio.file.WatchEvent;
import java.nio.file.Files;

public class RealTimeAnalyzer implements Runnable {
    // Diretório que será monitorado
    private Path dir;
    // Serviço que escuta mudanças no diretório
    private WatchService watchservice;
    // Recebe o diretório que queremos monitorar
    public RealTimeAnalyzer(String dir) {
        this.dir = Paths.get(dir);
    }

    // Inicializa o monitoramento do diretório
    private void initWatch() {
        try {
            watchservice = FileSystems.getDefault().newWatchService();
            // Define quais eventos queremos capturar
            dir.register(watchservice, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método que roda na Thread
    public void run() {
        initWatch();
        // O programa fica rodando o tempo todo
        while (true) {
            try {
                // Espera até acontecer alguma mudança no diretório
                WatchKey key = watchservice.take();
                // Percorre os eventos que aconteceram
                for (WatchEvent<?> event : key.pollEvents()) {
                    // Ignora eventos inválidos
                    if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    // Nome do arquivo que mudou
                    Path fileName = (Path) event.context();
                    // Caminho completo do arquivo
                    Path fullPath = dir.resolve(fileName);
                    // Só analisa se for um arquivo comum
                    if (Files.isRegularFile(fullPath)) {
                        boolean malicious = analyzeFile(fullPath.toString());
                        if (malicious) {
                            System.out.println("[ALERTA] Arquivo suspeito: " + fullPath);
                        } else {
                            System.out.println("[OK] Arquivo limpo: " + fullPath);
                        }
                    }
                }
                // Libera para continuar recebendo eventos
                key.reset();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Analisa o arquivo usando comparação de hash
    private boolean analyzeFile(String path) {
        HashAnalyzer analyzer = new HashAnalyzer();
        return analyzer.isMalicious();
    }
}
