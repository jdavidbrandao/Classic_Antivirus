package ui;

import javax.swing.SwingUtilities;

public class Exec {
    public static void main(String[] args) {
        // Swing deve ser criado na thread de eventos (EDT)
        SwingUtilities.invokeLater(() -> new AVFrame("Pega-Gato"));
    }
}
