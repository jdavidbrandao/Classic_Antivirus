package core;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import storage.getdb;

// Executa uma sequencia de heuristicas no arquivo analisado
public class HeuristicsAnalyzer extends Antivirus {

    private static final int THRESHOLD = 11; // pontuacao minima para considerar malicioso

    private final ArrayList<String> extract = new ArrayList<>(); // strings extraidas do arquivo
    private String lowerContent;                                 // conteudo em minusculas (calculado 1 vez)

    // ---------------------------------------------------------------
    // Termos do banco (compilados uma unica vez e reaproveitados)
    // ---------------------------------------------------------------

    private static final class Term {
        final String lower;
        final Pattern pattern;
        Term(String lower, Pattern pattern) { this.lower = lower; this.pattern = pattern; }
    }

    private static List<String> cachedSource = null;
    private static List<Term> cachedTerms = new ArrayList<>();

    private static boolean isWordChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    /**
     * \b so funciona quando a borda do termo e letra/numero. Para termos como "system("
     * usamos lookaround apenas nas bordas que sao "de palavra".
     */
    private static Pattern compile(String term) {
        String pre = isWordChar(term.charAt(0)) ? "(?<![\\p{L}\\p{N}_])" : "";
        String suf = isWordChar(term.charAt(term.length() - 1)) ? "(?![\\p{L}\\p{N}_])" : "";
        return Pattern.compile(pre + Pattern.quote(term) + suf);
    }

    private static synchronized List<Term> loadTerms() {
        ArrayList<String> source = new getdb().getStringsDb();
        if (cachedSource == null || !cachedSource.equals(source)) {
            List<Term> terms = new ArrayList<>();
            for (String raw : source) {
                // Minusculas dos DOIS lados: antes so o texto do arquivo era convertido
                // e entradas como "CreateRemoteThread" nunca casavam.
                String lower = raw.toLowerCase(Locale.ROOT);
                if (lower.isEmpty()) continue;
                terms.add(new Term(lower, compile(lower)));
            }
            cachedSource = source;
            cachedTerms = terms;
        }
        return cachedTerms;
    }

    // ---------------------------------------------------------------
    // Extracao de strings
    // ---------------------------------------------------------------

    public void setExtract(byte[] data, int minLength) {
        extract.clear();
        StringBuilder sb = new StringBuilder();

        for (byte b : data) {
            if (b >= 32 && b <= 126) {
                sb.append((char) b); // caractere ASCII imprimivel
            } else {
                if (sb.length() >= minLength) {
                    extract.add(sb.toString());
                }
                sb.setLength(0);
            }
        }
        if (sb.length() >= minLength) {
            extract.add(sb.toString());
        }
    }

    public ArrayList<String> getExtract() {
        return extract;
    }

    public Boolean isStringMalicious(String str) {
        String lower = str.toLowerCase(Locale.ROOT);
        for (Term t : loadTerms()) {
            // contains() e barato e descarta quase tudo antes do regex
            if (lower.contains(t.lower) && t.pattern.matcher(lower).find()) {
                return true;
            }
        }
        return false;
    }

    // ---------------------------------------------------------------
    // Heuristicas
    // ---------------------------------------------------------------

    public int StringAnalyzer() {
        byte[] data = Antivirus.fileContent.get();
        if (data == null) return 0;
        setExtract(data, 5);

        Set<String> alreadySeen = new HashSet<>();
        int occurrences = 0; // 1 ou 2 strings suspeitas nao significam malware, por isso o contador

        for (String test : extract) {
            if (alreadySeen.add(test) && isStringMalicious(test)) {
                occurrences++;
            }
        }

        if (occurrences >= 10) return 10;
        if (occurrences >= 3) return 7;
        return 0; // (antes retornava 1 mesmo sem nenhuma ocorrencia)
    }

    public int ShellAnalyzer() {
        byte[] data = Antivirus.fileContent.get();
        if (data == null) return 0;
        if (extract.isEmpty()) setExtract(data, 5); // nao depende mais da ordem das chamadas

        for (String st : extract) {
            if (st.contains("/bin/sh") || st.contains("/bin/bash")) {
                return 4;
            }
        }
        return 0;
    }

    private String lowerContent() {
        if (lowerContent == null) {
            byte[] data = Antivirus.fileContent.get();
            // ISO_8859_1: 1 byte = 1 char, sem depender do charset da maquina
            lowerContent = data == null ? "" : new String(data, StandardCharsets.ISO_8859_1).toLowerCase(Locale.ROOT);
        }
        return lowerContent;
    }

    public int NetworkAnalyzer() {
        String s = lowerContent();
        int score = 0;
        // "send" foi removido: aparece em praticamente qualquer arquivo ("sender", "resend"...)
        for (String key : new String[] { "af_inet", "sock_stream", "htons", "inet_addr", "socket" }) {
            if (s.contains(key)) score++;
        }
        return score;
    }

    // Reverse shell costuma redirecionar stdin/stdout/stderr com 3 chamadas dup2(
    public int DupAnalyzer() {
        String s = lowerContent();
        int count = 0;
        int idx = 0;
        while ((idx = s.indexOf("dup2(", idx)) >= 0) {
            count++;
            idx += 5;
        }
        if (count >= 3) return 3;
        if (count >= 1) return 1;
        return 0;
    }

    public boolean isMalicious() {
        if (Antivirus.fileContent.get() == null) return false;

        int heuristicScore = 0;
        heuristicScore += StringAnalyzer();
        heuristicScore += ShellAnalyzer();
        heuristicScore += NetworkAnalyzer();
        heuristicScore += DupAnalyzer();

        if (heuristicScore >= THRESHOLD) {
            UploadFile.uploadFile(Antivirus.path.get(), "SHA-256");
            return true;
        }
        return false;
    }
}
