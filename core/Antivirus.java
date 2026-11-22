package core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Antivirus {

    // Arquivos maiores que isso sao ignorados pelos analisadores (evita OutOfMemoryError)
    public static final long MAX_SCAN_BYTES = 100L * 1024 * 1024;

    public static ThreadLocal<String> path = new ThreadLocal<>();            // Caminho do arquivo a ser escaneado
    protected static ThreadLocal<byte[]> fileContent = new ThreadLocal<>();  // Conteudo do arquivo em bytes

    /**
     * Le o arquivo apontado por Antivirus.path.
     * Se falhar, o conteudo anterior e DESCARTADO (antes ele ficava no ThreadLocal e
     * o arquivo seguinte era analisado/acusado com os bytes do arquivo anterior).
     */
    public static boolean getFileContent() {
        fileContent.remove();
        String p = path.get();
        if (p == null) return false;
        try {
            Path file = Paths.get(p);
            if (Files.size(file) > MAX_SCAN_BYTES) return false;
            fileContent.set(Files.readAllBytes(file));
            return true;
        } catch (IOException | RuntimeException e) {
            return false; // sem permissao, arquivo sumiu, caminho invalido...
        } catch (OutOfMemoryError e) {
            fileContent.remove();
            return false;
        }
    }

    /** Libera os ThreadLocals (evita manter o ultimo arquivo inteiro na memoria). */
    public static void clear() {
        path.remove();
        fileContent.remove();
    }

    public static Antivirus[] getScanM() {
        Antivirus[] methods = new Antivirus[3];
        methods[0] = new HashAnalyzer();
        methods[1] = new SignatureAnalyzer();
        methods[2] = new HeuristicsAnalyzer();
        return methods;
    }

    /** Metodo que deve ser implementado nas classes filhas. */
    public abstract boolean isMalicious();

    /**
     * Ponto unico de entrada: le o arquivo UMA vez e roda todos os analisadores.
     * Retorna false se o arquivo nao pode ser lido (nunca acusa por conteudo de outro arquivo).
     */
    public static boolean scanFile(String filePath) {
        path.set(filePath);
        try {
            if (!getFileContent()) return false;
            for (Antivirus method : getScanM()) {
                if (method.isMalicious()) return true;
            }
            return false;
        } finally {
            clear();
        }
    }
}
