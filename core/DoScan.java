package core;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public class DoScan {

    // MÉTODO ORIGINAL (fica igual)
    public static ArrayList<String> scanComputer() {
        Scanner sc = new Scanner();
        sc.setFiles();
        return sc.getFiles();
    }

    // 🔹 NOVO MÉTODO COM LOG
    public static ArrayList<String> scanComputer(ScanLogger logger) {

        logger.log("Initializing scanner...");

        Scanner sc = new Scanner(logger);
        sc.setFiles();
        ArrayList<String> result = sc.getFiles();

        logger.log("Scan completed.");

        return result;
    }


    public static boolean removeFile(String path) {
        File arquivo = new File(path);
        arquivo.delete();
        return !arquivo.exists();
    }

    public static HashMap<Integer, String> scanFolder(File dir) {
        File[] arquivos = dir.listFiles();
        HashMap<Integer, String> result = new HashMap<>();

        int counter = 0;
        for (File arquivo : arquivos) {
            if (Files.isSymbolicLink(arquivo.toPath())) continue;

            if (arquivo.isFile()) {

                Antivirus.path.set(arquivo.getAbsolutePath());
                Antivirus.getFileContent();

                Antivirus[] scanner = {
                    new HashAnalyzer(),
                    new SignatureAnalyzer(),
                    new HeuristicsAnalyzer()
                };

                if (scanner[0].isMalicious()
                 || scanner[1].isMalicious()
                 || scanner[2].isMalicious()) {

                    result.put(counter++, arquivo.getAbsolutePath());
                }
            }
        }
        return result;
    }
}
