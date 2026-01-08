package ui;

import java.awt.*;
import javax.swing.*;

public class ScanWindow extends JFrame {

    private JTextArea area;

    public ScanWindow() {
        setTitle("Scanning...");
        setSize(700, 500);
        setLocationRelativeTo(null);

        area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));

        add(new JScrollPane(area));
    }

    public void log(String msg) {
        area.append(msg + "\n");
    }
}
