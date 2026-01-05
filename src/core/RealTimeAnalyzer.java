package core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import java.nio.file.FileSystems;
import java.nio.file.StandardWatchEventKinds;
import java.io.File;
import java.io.IOException;
import java.nio.file.WatchKey;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import java.nio.file.WatchEvent;
import java.nio.file.Files;

public class RealTimeAnalyzer implements Runnable {
    // Diretório que será monitorado
    private Path dir;
    // Serviço que escuta mudanças no diretório
    private WatchService watchservice;
    // Recebe o diretório que queremos monitorar
    public static String maliciousFile = null;
    
    public RealTimeAnalyzer(String dir) {
        this.dir = Paths.get(dir);
    }
    
    private boolean removeFile(String path) {
    	File arquivo = new File(path);
    	arquivo.delete();
		if (arquivo.exists()) {
			return false;
		}
		return true;
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
        while (!Thread.currentThread().isInterrupted()) {
            try {
                File initdir = new File(dir.toString());
                File[] files = initdir.listFiles();
                
                if (files != null) {
                    for (File f : files) {
                        if (!Files.isSymbolicLink(f.toPath())) {
						    if (analyzeFile(f.getAbsolutePath())) {
						    	SwingUtilities.invokeLater(() -> {
						            JOptionPane.showMessageDialog(
						                null,
						                "Malware detected:\n" + f.getAbsolutePath(),
						                "WARNING",
						                JOptionPane.ERROR_MESSAGE
						            );
						        });
						    	boolean action = removeFile(f.getAbsolutePath());
						    	if (action) {
						    		SwingUtilities.invokeLater(() -> {
							            JOptionPane.showMessageDialog(
							                null,
							                "Malware neutralized\n",
							                "WARNING",
							                JOptionPane.OK_OPTION
							            );
							        });
						    	} else {
						    		SwingUtilities.invokeLater(() -> {
							            JOptionPane.showMessageDialog(
							                null,
							                "Malware couldnt be deleted:\n" + f.getAbsolutePath(),
							                "WARNING",
							                JOptionPane.ERROR_MESSAGE
							            );
							        });
						    	}
						    }
						}
                    }
                }

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
					        SwingUtilities.invokeLater(() -> {
					            JOptionPane.showMessageDialog(
					                null,
					                "Malware detected:\n" + fullPath,
					                "WARNING",
					                JOptionPane.ERROR_MESSAGE
					            );
					        });
					        
					        boolean action = removeFile(fullPath.toString());
					    	if (action) {
					    		SwingUtilities.invokeLater(() -> {
						            JOptionPane.showMessageDialog(
						                null,
						                "Malware neutralized\n",
						                "WARNING",
						                JOptionPane.OK_OPTION
						            );
						        });
					    	} else {
					    		SwingUtilities.invokeLater(() -> {
						            JOptionPane.showMessageDialog(
						                null,
						                "Malware couldnt be deleted:\n" + fullPath.toString(),
						                "WARNING",
						                JOptionPane.ERROR_MESSAGE
						            );
						        });
					    }
				    } else {
				    	SwingUtilities.invokeLater(() -> {
				            JOptionPane.showMessageDialog(
				                null,
				                "No threats detected\n",
				                "OK",
				                JOptionPane.OK_OPTION
				            );
				        });
				    }
					}
                }

                // Libera para continuar recebendo eventos
                key.reset();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Preserva o status de interrupção
            }
        }

        // Fecha o watchservice ao sair
        try {
            watchservice.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    // Analisa o arquivo usando comparação de hash
    private boolean analyzeFile(String path) {
    	Antivirus.path.set(path);
    	Antivirus.getFileContent();
        Antivirus[] analyzers = Antivirus.getScanM();
        for (Antivirus a : analyzers) {
        	if (a.isMalicious()) {
        		return true;
        	}
        }
        return false;
    }
}
