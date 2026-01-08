package core;

import java.util.ArrayList;
import java.io.File;
import java.nio.file.*;

public class Scanner {

    private String initFolder;
    private ArrayList<String> files = new ArrayList<>();
    private ArrayList<String> folders = new ArrayList<>();
    private ScanLogger logger; // ✅ callback, não UI

    public Scanner() {
        initFolder = Paths.get("/home", "kali", "Desktop").toString();
    }

    public Scanner(String initFolder, ScanLogger logger) {
        this.initFolder = initFolder;
        this.logger = logger;
    }

    public Scanner(ScanLogger logger) {
        this();
        this.logger = logger;
    }

    private void log(String msg) {
        if (logger != null) {
            logger.log(msg);
        }
    }

    public void spider(String folder) {
        File diretorio = new File(folder);
        File[] arquivos = diretorio.listFiles();

        if (arquivos != null) {
            for (File arquivo : arquivos) {

                log("Analyzing: " + arquivo.getAbsolutePath());

                if (Files.isSymbolicLink(arquivo.toPath())) continue;

                if (arquivo.isDirectory() &&
                    !folders.contains(arquivo.getAbsolutePath())) {

                    folders.add(arquivo.getAbsolutePath());
                    spider(arquivo.getAbsolutePath());
                }

                else if (arquivo.isFile() &&
                         !files.contains(arquivo.getAbsolutePath())) {

                    boolean malicious = false;

                    Antivirus[] scm = Antivirus.getScanM();
                    for (Antivirus method : scm) {
                        Antivirus.path.set(arquivo.getAbsolutePath());
                        Antivirus.getFileContent();
                        if (method.isMalicious()) {
                            malicious = true;
                        }
                    }

                    if (malicious) {
                        log("⚠️ THREAT: " + arquivo.getAbsolutePath());
                        files.add(arquivo.getAbsolutePath());
                    }
                }
            }
        }
    }

    public void spider() {
        spider(initFolder);
    }

    public ArrayList<String> getFiles() {
        spider();
        return files;
    }
}
