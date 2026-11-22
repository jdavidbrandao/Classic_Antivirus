package core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public class DoScan {

    public static ArrayList<String> scanComputer() {
        Scanner sc = new Scanner();
        sc.setFiles();
        return sc.getFiles();
    }

    public static ArrayList<String> scanComputer(ScanLogger logger) {

        logger.log("Initializing scanner...");

        Scanner sc = new Scanner(logger);
        sc.setFiles();
        ArrayList<String> result = sc.getFiles();

        logger.log("Scan completed.");

        return result;
    }

    public static boolean removeFile(String path) {
        try {
            Files.deleteIfExists(new File(path).toPath());
        } catch (IOException e) {
            return false;
        }
        return !new File(path).exists();
    }

    // Varre a pasta escolhida E as subpastas (antes so olhava o primeiro nivel e dava NPE
    // quando listFiles() retornava null).
    public static HashMap<Integer, String> scanFolder(File dir) {
        HashMap<Integer, String> result = new HashMap<>();

        Scanner sc = new Scanner(dir.getAbsolutePath(), null);
        sc.setFiles();

        int counter = 0;
        for (String threat : sc.getFiles()) {
            result.put(counter++, threat);
        }
        return result;
    }
}
