package core;

public interface ThreatListener {
    void onThreatDetected(String path);

    // Chamado quando o monitoramento de uma pasta nao pode iniciar/continuar (ex.: pasta inexistente)
    default void onMonitorError(String directory, String message) {
    }
}
