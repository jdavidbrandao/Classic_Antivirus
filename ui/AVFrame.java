package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.time.LocalDateTime;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import core.*;
import storage.AppPaths;

@SuppressWarnings("serial")
public class AVFrame extends JFrame implements ThreatListener {
	
	private DefaultTableModel quarantineModel;

    // Destaque seleção de botão do menu
    private JButton selectedButton = null;
    private void selectMenuButton(JButton button) {
        if (selectedButton != null) {
            selectedButton.setBackground(Color.WHITE);
        }
        button.setBackground(new Color(140, 140, 140));
        selectedButton = button;
    }

    // Personalização dos headers dos menus
    private JPanel createHeader(String titulo) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(120, 120, 120));
        header.setPreferredSize(new Dimension(0, 50));

        JLabel label = new JLabel(titulo);
        label.setFont(new Font("Segoe UI", Font.BOLD, 26));

        header.setBorder(
            BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(0, 0, 0)),
            BorderFactory.createEmptyBorder(0, 20, 0, 0)
            )
        );

        header.add(label, BorderLayout.WEST);
        return header;
    }

    private JSeparator createSeparator() {
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(Color.BLACK);
        sep.setPreferredSize(new Dimension(160, 1));
        sep.setMaximumSize(new Dimension(320, 1));
        return sep;
    }

    // Move o arquivo para a quarentena e, se deu certo, adiciona a linha na tabela.
    // Retorna false se a quarentena FALHOU (o arquivo continua no lugar original).
    private boolean quarantineFile(String path) {
        String date = LocalDateTime.now().toString();
        String destiny = MoveQuarentine.moveToQua(path, date);
        if (destiny.isEmpty()) return false;
        addRow(path, date, destiny);
        return true;
    }

    // A coluna 3 (oculta) guarda o caminho real do arquivo dentro da quarentena
    private void addRow(String origin, String date, String quarantinedPath) {
        quarantineModel.addRow(new Object[] { origin, date, quarantinedPath });
    }

    // Resumo unico para os dois tipos de varredura (antes eram 2 dialogos por ameaca,
    // e dizia "Your system is safe" mesmo com ameacas encontradas ou quarentena falhando)
    private void handleThreats(java.util.List<String> threats) {
        if (threats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No threats detected.", "No Threat Detected", JOptionPane.PLAIN_MESSAGE);
            return;
        }

        StringBuilder moved = new StringBuilder();
        StringBuilder failed = new StringBuilder();
        for (String threat : threats) {
            if (quarantineFile(threat)) moved.append(" - ").append(threat).append("\n");
            else failed.append(" - ").append(threat).append("\n");
        }

        StringBuilder msg = new StringBuilder();
        msg.append(threats.size()).append(" threat(s) found.\n");
        if (moved.length() > 0) msg.append("\nMoved to quarantine:\n").append(moved);
        if (failed.length() > 0) msg.append("\nCould NOT be quarantined (check permissions):\n").append(failed);

        JTextArea area = new JTextArea(msg.toString());
        area.setEditable(false);
        JScrollPane pane = new JScrollPane(area);
        pane.setPreferredSize(new Dimension(520, 220));

        JOptionPane.showMessageDialog(this, pane, "Threat Detected",
            failed.length() > 0 ? JOptionPane.ERROR_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void onThreatDetected(String path) {
        SwingUtilities.invokeLater(() -> {
            boolean moved = quarantineFile(path);
            JOptionPane.showMessageDialog(
                this,
                (moved ? "Malware detected and moved to quarantine:\n"
                       : "Malware detected, but it could NOT be quarantined:\n") + path,
                "WARNING",
                JOptionPane.ERROR_MESSAGE
            );
        });
    }

    @Override
    public void onMonitorError(String directory, String message) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
            this,
            "Could not monitor " + directory + ":\n" + message,
            "Real Time Analyzer",
            JOptionPane.WARNING_MESSAGE
        ));
    }

    public class ScanWindow extends JFrame {

        private JTextArea area;

        public ScanWindow() {
            setTitle("Scanning...");
            setSize(700, 500);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            area = new JTextArea();
            area.setEditable(false);
            area.setFont(new Font("Monospaced", Font.PLAIN, 12));

            add(new JScrollPane(area));
        }

        public void log(String msg) {
            area.append(msg + "\n");
        }
    }

    // Janela
    AVFrame(String nome) {
        super(nome);
        setSize(800, 610);
        setLayout(new BorderLayout());
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    // =============================================End===============================================

        // Lateral Opções
        JPanel list = new JPanel();
        list.setBackground(Color.DARK_GRAY);
        list.setLayout(new BorderLayout());
        list.setPreferredSize(new Dimension(160, 0));
        list.setBorder(
            BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0, 0, 0)),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
            )
        );

    // =============================================End===============================================

        // Panel Auxiliar para alinhamento do menu lateral
        JPanel listTop = new JPanel();
        listTop.setLayout(new BoxLayout(listTop, BoxLayout.Y_AXIS));
        
        String[] columns = {"File Location", "Date", "Quarantined"}; 
        quarantineModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }   
        };

    // =============================================End===============================================

        // Criação dos botões do menu
        Font menuFont = new Font("Segoe UI", Font.BOLD, 11);
        JButton bl1 = new JButton("Scanner");
        bl1.setFont(menuFont);
        bl1.setVerticalTextPosition(SwingConstants.BOTTOM);
        bl1.setHorizontalTextPosition(SwingConstants.CENTER);
        bl1.setBackground(Color.WHITE);
        bl1.setOpaque(true);
        bl1.setBorderPainted(false);
        bl1.setFocusPainted(false);
        selectMenuButton(bl1);
        ImageIcon icon1 = new ImageIcon(getClass().getResource("/ui/resource/icon1.png"));
        bl1.setIcon(icon1);

        JButton bl2 = new JButton("Directory Scanner");
        bl2.setFont(menuFont);
        bl2.setVerticalTextPosition(SwingConstants.BOTTOM);
        bl2.setHorizontalTextPosition(SwingConstants.CENTER);
        bl2.setBackground(Color.WHITE);
        bl2.setOpaque(true);
        bl2.setBorderPainted(false);
        bl2.setFocusPainted(false);
        ImageIcon icon2 = new ImageIcon(getClass().getResource("/ui/resource/icon2.png"));
        bl2.setIcon(icon2);

        JButton bl3 = new JButton("Real Time Analyzer");
        bl3.setFont(menuFont);
        bl3.setVerticalTextPosition(SwingConstants.BOTTOM);
        bl3.setHorizontalTextPosition(SwingConstants.CENTER);
        bl3.setBackground(Color.WHITE);
        bl3.setOpaque(true);
        bl3.setBorderPainted(false);
        bl3.setFocusPainted(false);
        ImageIcon icon3 = new ImageIcon(getClass().getResource("/ui/resource/icon3.png"));
        bl3.setIcon(icon3);

        JButton bl4 = new JButton("Quarantine");
        bl4.setFont(menuFont);
        bl4.setVerticalTextPosition(SwingConstants.BOTTOM);
        bl4.setHorizontalTextPosition(SwingConstants.CENTER);
        bl4.setBackground(Color.WHITE);
        bl4.setOpaque(true);
        bl4.setBorderPainted(false);
        bl4.setFocusPainted(false);
        ImageIcon icon4 = new ImageIcon(getClass().getResource("/ui/resource/icon4.png"));
        bl4.setIcon(icon4);

        JButton about = new JButton("About");
        about.setFont(menuFont);
        about.setVerticalTextPosition(SwingConstants.BOTTOM);
        about.setHorizontalTextPosition(SwingConstants.CENTER);
        about.setBackground(Color.WHITE);
        about.setOpaque(true);
        about.setBorderPainted(false);
        about.setFocusPainted(false);
        ImageIcon icon5 = new ImageIcon(getClass().getResource("/ui/resource/icon5.png"));
        about.setIcon(icon5);

    // =============================================End===============================================

        // Criação dos botões funcionais
        JButton scan = new JButton("Scan my Computer");
        scan.setFont(menuFont);
        scan.setBackground(Color.WHITE);
        scan.addActionListener(e -> {

            ScanWindow logWindow = new ScanWindow();
            logWindow.setVisible(true);

            JOptionPane pane = new JOptionPane("Verifying files...\nPlease wait.", JOptionPane.INFORMATION_MESSAGE);
            pane.setOptions(new Object[]{});
            
            JDialog dialog = pane.createDialog(this, "Verifying");
            dialog.setModal(false);
            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            dialog.setVisible(true);

            SwingWorker<ArrayList<String>, String> worker =
                new SwingWorker<>() {

                @Override
                protected ArrayList<String> doInBackground() {

                    // Logs em tempo real
                    return DoScan.scanComputer(msg -> publish(msg));
                }

                @Override
                protected void process(java.util.List<String> logs) {
                    for (String log : logs) {
                        logWindow.log(log);
                    }
                }

                @Override
                protected void done() {
                    try {
                    	dialog.dispose();
                        ArrayList<String> threatFound = get();

                        logWindow.log("\nScan finished.");

                        handleThreats(threatFound);
                    } catch (Exception ex) {
                    	System.out.println(ex);
                        JOptionPane.showMessageDialog(
                            null,
                            "An error occurred during scanning.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            };

            worker.execute();
        });
        scan.setFocusPainted(false);
        scan.setOpaque(true);
        scan.setMaximumSize(new Dimension(180, 40));
        scan.setPreferredSize(new Dimension(180, 40));
        scan.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton dir = new JButton("Scan Directory");
        dir.setFont(menuFont);
        dir.addActionListener(e -> {

            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setDialogTitle("Select a directory");

            if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            
            JOptionPane pane2 = new JOptionPane("Verifying files...\nPlease wait.", JOptionPane.INFORMATION_MESSAGE);
            pane2.setOptions(new Object[]{});
            
            JDialog dialog2 = pane2.createDialog(this, "Verifying");
            dialog2.setModal(false);
            dialog2.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            dialog2.setVisible(true);

            File folder = chooser.getSelectedFile();


            dialog2.setVisible(true);

            SwingWorker<HashMap<Integer, String>, Void> worker =
                    new SwingWorker<>() {

                @Override
                protected HashMap<Integer, String> doInBackground() {
                    return DoScan.scanFolder(folder);
                }

                @Override
                protected void done() {
                    dialog2.dispose();

                    try {
                        HashMap<Integer, String> threatFound = get();
                        handleThreats(new ArrayList<>(threatFound.values()));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };

            worker.execute();
        });
        dir.setBackground(Color.WHITE);
        dir.setFocusPainted(false);
        dir.setOpaque(true);
        dir.setMaximumSize(new Dimension(180, 40));
        dir.setPreferredSize(new Dimension(180, 40));
        dir.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        Dimension checkBox = new Dimension(180, 25);
        JCheckBox temp = new JCheckBox("Temp");
        temp.setFont(menuFont);
        temp.setAlignmentX(CENTER_ALIGNMENT);
        temp.setMaximumSize(checkBox);

        JCheckBox downloads = new JCheckBox("Downloads");
        downloads.setFont(menuFont);
        downloads.setAlignmentX(CENTER_ALIGNMENT);
        downloads.setMaximumSize(checkBox);

        JCheckBox desktop = new JCheckBox("Desktop");
        desktop.setFont(menuFont);
        desktop.setAlignmentX(CENTER_ALIGNMENT);
        desktop.setMaximumSize(checkBox);
        
        JButton rtaToggle = new JButton("Start");
        rtaToggle.setFont(menuFont);
        rtaToggle.setBackground(Color.WHITE);
        rtaToggle.setFocusPainted(false);
        rtaToggle.setOpaque(true);
        rtaToggle.setMaximumSize(new Dimension(180, 40));
        rtaToggle.setPreferredSize(new Dimension(180, 40));
        rtaToggle.setAlignmentX(Component.CENTER_ALIGNMENT);

        final boolean[] running = { false };
        final java.util.List<RealTimeAnalyzer> monitors = new ArrayList<>();
        rtaToggle.addActionListener(e -> {
            if (!running[0]) {

                // Pastas do usuario atual (Ubuntu/XDG), nada fixo para o usuario "kali"
                java.util.List<String> dirs = new ArrayList<>();
                if (temp.isSelected()) dirs.add(AppPaths.tempDir().toString());
                if (downloads.isSelected()) dirs.add(AppPaths.downloadsDir().toString());
                if (desktop.isSelected()) dirs.add(AppPaths.desktopDir().toString());

                if (dirs.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Select at least one directory.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                StringBuilder missing = new StringBuilder();
                for (String d : dirs) {
                    if (!Files.isDirectory(Paths.get(d))) {
                        missing.append(" - ").append(d).append("\n");
                        continue;
                    }
                    RealTimeAnalyzer rta = new RealTimeAnalyzer(d, this);
                    monitors.add(rta);
                    rta.start();
                }

                if (missing.length() > 0) {
                    JOptionPane.showMessageDialog(this, "Folder(s) not found and not monitored:\n" + missing,
                        "Warning", JOptionPane.WARNING_MESSAGE);
                }
                if (monitors.isEmpty()) return; // nada foi iniciado

                running[0] = true;
                rtaToggle.setText("Stop");
                rtaToggle.setBackground(new Color(220, 120, 120));

            } else {
                for (RealTimeAnalyzer rta : monitors) {
                    rta.stop();
                }
                monitors.clear();
                running[0] = false;
                rtaToggle.setText("Start");
                rtaToggle.setBackground(Color.WHITE);
            }
        });
    
    // =============================================End===============================================

        // Tamanho e adição dos botoes ao menu
        Dimension blSize = new Dimension(160, 100);

        bl1.setMaximumSize(blSize);
        bl2.setMaximumSize(blSize);
        bl3.setMaximumSize(blSize);
        bl4.setMaximumSize(blSize);
        about.setMaximumSize(blSize);

        bl1.setPreferredSize(blSize);
        bl2.setPreferredSize(blSize);
        bl3.setPreferredSize(blSize);
        bl4.setPreferredSize(blSize);
        about.setPreferredSize(blSize);

        bl1.setAlignmentX(Component.LEFT_ALIGNMENT);
        bl2.setAlignmentX(Component.LEFT_ALIGNMENT);
        bl3.setAlignmentX(Component.LEFT_ALIGNMENT);
        bl4.setAlignmentX(Component.LEFT_ALIGNMENT);
        about.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Espaçamento e adicionando os botões ao Panel auxiliar
        listTop.add(createSeparator());
        listTop.add(Box.createVerticalStrut(10));
        listTop.add(createSeparator());
        listTop.add(bl1);
        listTop.add(createSeparator());
        listTop.add(Box.createVerticalStrut(10));
        listTop.add(createSeparator());
        listTop.add(bl2);
        listTop.add(createSeparator());
        listTop.add(Box.createVerticalStrut(10));
        listTop.add(createSeparator());
        listTop.add(bl3);
        listTop.add(createSeparator());
        listTop.add(Box.createVerticalStrut(10));
        listTop.add(createSeparator());
        listTop.add(bl4);
        listTop.add(createSeparator());
        listTop.add(Box.createVerticalStrut(10));
        listTop.add(createSeparator());
        listTop.add(about);
        listTop.add(createSeparator());
        listTop.add(Box.createVerticalStrut(10));
        listTop.add(createSeparator());
        list.add(listTop, BorderLayout.NORTH);

    // =============================================End===============================================

        // Tela em destaque
        CardLayout cardLayout = new CardLayout();
        JPanel panelMain = new JPanel(cardLayout);

    // =============================================End===============================================

        // ================= TELA 1 ===================
        JPanel t1 = new JPanel();
        t1.setLayout(new BorderLayout());
        t1.add(createHeader("Scanner"), BorderLayout.NORTH);

        ImageIcon icon1p = new ImageIcon(getClass().getResource("/ui/resource/icon1p.png"));
        JLabel iconLabel1 = new JLabel(icon1p);
        iconLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel1.setVerticalAlignment(SwingConstants.TOP);
        iconLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel centerPanel1 = new JPanel();
        centerPanel1.setLayout(new BoxLayout(centerPanel1, BoxLayout.Y_AXIS));
        centerPanel1.setOpaque(false);
        centerPanel1.add(Box.createVerticalStrut(30));
        centerPanel1.add(iconLabel1);
        centerPanel1.add(Box.createVerticalStrut(25));
        centerPanel1.add(scan);

        t1.add(centerPanel1, BorderLayout.CENTER);

    // =============================================End===============================================

        // ================= TELA 2 =================
        JPanel t2 = new JPanel();
        t2.setLayout(new BorderLayout());
        t2.add(createHeader("Directory"), BorderLayout.NORTH);

        ImageIcon icon2p = new ImageIcon(getClass().getResource("/ui/resource/icon2p.png"));
        JLabel iconLabel2 = new JLabel(icon2p);
        iconLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel2.setVerticalAlignment(SwingConstants.TOP);
        iconLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel centerPanel2 = new JPanel();
        centerPanel2.setLayout(new BoxLayout(centerPanel2, BoxLayout.Y_AXIS));
        centerPanel2.setOpaque(false);
        centerPanel2.add(Box.createVerticalStrut(30));
        centerPanel2.add(iconLabel2);
        centerPanel2.add(Box.createVerticalStrut(25));
        centerPanel2.add(dir);

        t2.add(centerPanel2, BorderLayout.CENTER);

    // =============================================End===============================================

        // ================= TELA 3 =================
        JPanel t3 = new JPanel();
        t3.setLayout(new BorderLayout());
        t3.add(createHeader("Real Time Analyzer"), BorderLayout.NORTH);

        ImageIcon icon3p = new ImageIcon(getClass().getResource("/ui/resource/icon3p.png"));
        JLabel iconLabel3 = new JLabel(icon3p);
        iconLabel3.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel3.setVerticalAlignment(SwingConstants.TOP);
        iconLabel3.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel centerPanel3 = new JPanel();
        centerPanel3.setLayout(new BoxLayout(centerPanel3, BoxLayout.Y_AXIS));
        centerPanel3.setOpaque(false);

        JPanel option = new JPanel();
        option.setLayout(new BoxLayout(option, BoxLayout.Y_AXIS));
        option.setOpaque(false);
        option.add(temp);
        option.add(Box.createVerticalStrut(8));
        option.add(desktop);
        option.add(Box.createVerticalStrut(8));
        option.add(downloads);
        option.add(Box.createVerticalStrut(25));

        centerPanel3.add(Box.createVerticalStrut(30));
        centerPanel3.add(iconLabel3);
        centerPanel3.add(Box.createVerticalStrut(20));
        centerPanel3.add(option);
        centerPanel3.add(rtaToggle);

        t3.add(centerPanel3, BorderLayout.CENTER);
    
    // =============================================End===============================================

        // ================= TELA 4 =================
        JPanel t4 = new JPanel();
        t4.setLayout(new BorderLayout());
        t4.add(createHeader("Quarantine"), BorderLayout.NORTH);
        
        // Tabela para o historico da quarentena (lista vazia se a pasta ainda nao existe)
        for (MoveQuarentine.Entry q : MoveQuarentine.listEntries()) {
            addRow(q.origin, q.date, q.quarantined);
        }

        JTable quarantineTable = new JTable(quarantineModel);
        quarantineTable.removeColumn(quarantineTable.getColumnModel().getColumn(2)); // oculta o caminho interno
        quarantineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        quarantineTable.setRowHeight(22);
        quarantineTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(quarantineTable);
        scroll.setPreferredSize(new Dimension(500, 0));

        JPanel actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        actions.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        // Botões funcionais da tela 4
        Dimension btnSize = new Dimension(160, 40);
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setFont(menuFont);
        deleteBtn.setBackground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setOpaque(true);
        deleteBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteBtn.setPreferredSize(btnSize);
        deleteBtn.setMaximumSize(btnSize);
        deleteBtn.setMinimumSize(btnSize);
        deleteBtn.addActionListener(e -> {
            int viewRow = quarantineTable.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a threat first.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int row = quarantineTable.convertRowIndexToModel(viewRow);
            String quarantinedPath = (String) quarantineModel.getValueAt(row, 2);

            if (MoveQuarentine.removeFromQua(quarantinedPath)) {
                quarantineModel.removeRow(row);
                JOptionPane.showMessageDialog(this, "Malware deleted!", "Success", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Could not delete the quarantined file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton restoreBtn = new JButton("Restore");
        restoreBtn.setFont(menuFont);
        restoreBtn.setBackground(Color.WHITE);
        restoreBtn.setFocusPainted(false);
        restoreBtn.setOpaque(true);
        restoreBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        restoreBtn.setPreferredSize(btnSize);
        restoreBtn.setMaximumSize(btnSize);
        restoreBtn.setMinimumSize(btnSize);
        restoreBtn.addActionListener(e -> {
            int viewRow = quarantineTable.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a threat first.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int row = quarantineTable.convertRowIndexToModel(viewRow);
            String quarantinedPath = (String) quarantineModel.getValueAt(row, 2);

            String restored = MoveQuarentine.restoreFromQua(quarantinedPath);
            if (!restored.isEmpty()) {
                quarantineModel.removeRow(row);
                JOptionPane.showMessageDialog(this, "File restored to:\n" + restored, "Success", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Could not restore the file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        actions.add(deleteBtn);
        actions.add(Box.createVerticalStrut(5));
        actions.add(restoreBtn);

        JPanel center = new JPanel(new BorderLayout());
        center.add(scroll, BorderLayout.CENTER);
        center.add(actions, BorderLayout.EAST);

        t4.add(center, BorderLayout.CENTER);

    // =============================================End===============================================

        // ================= ABOUT =================
        JPanel tAbout = new JPanel();
        tAbout.setLayout(new BorderLayout());
        tAbout.add(createHeader("About"), BorderLayout.NORTH);

        ImageIcon icon5p = new ImageIcon(getClass().getResource("/ui/resource/icon5p.png"));
        JLabel iconLabel5 = new JLabel(icon5p);
        iconLabel5.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel5.setVerticalAlignment(SwingConstants.TOP);
        iconLabel5.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(
        BorderFactory.createEmptyBorder(0, 20, 0, 0)
        );

        JLabel aboutText = new JLabel(
        "<html>" +
        "<div style='width:420px; font-family:Segoe UI; font-size:10px;'>" +

            "<h3>Scanner</h3>" +
            "<p>Performs a full scan of the computer in search of suspicious or malicious files.</p>" +

            "<h3>Directory Scanner</h3>" +
            "<p>Allows selecting specific directories for manual analysis, such as personal or external folders.</p>" +

            "<h3>Real Time Analyzer</h3>" +
            "<p>Monitors the system in real time, analyzing changes in the selected folders.</p>" +

            "<h3>Quarantine</h3>" +
            "<p>Threats are moved to quarantine and user have to decide their destiny.</p>" +

        "</div>" +
        "</html>"
        );

        aboutText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel centerPanel4 = new JPanel();
        centerPanel4.setLayout(new BoxLayout(centerPanel4, BoxLayout.Y_AXIS));
        centerPanel4.setOpaque(false);
        centerPanel4.add(Box.createVerticalStrut(30));
        centerPanel4.add(iconLabel5);
        centerPanel4.add(Box.createVerticalStrut(25));
        centerPanel4.add(aboutText);

        contentWrapper.add(centerPanel4, BorderLayout.CENTER);
        tAbout.add(contentWrapper, BorderLayout.CENTER);

    // =============================================End===============================================    

        // Listeners dos botões do Menu
        bl1.addActionListener(e -> {
            cardLayout.show(panelMain, "T1");
            selectMenuButton(bl1);
        });

        bl2.addActionListener(e -> {
            cardLayout.show(panelMain, "T2");
            selectMenuButton(bl2);
        });

        bl3.addActionListener(e -> {
            cardLayout.show(panelMain, "T3");
            selectMenuButton(bl3);
        });

        bl4.addActionListener(e -> {
            cardLayout.show(panelMain, "T4");
            selectMenuButton(bl4);
        });

        about.addActionListener(e -> {
            cardLayout.show(panelMain, "ABOUT");
            selectMenuButton(about);
        });

    // =============================================End===============================================

        // Adiciona telas ao CardLayout
        panelMain.add(t1, "T1");
        panelMain.add(t2, "T2");
        panelMain.add(t3, "T3");
        panelMain.add(t4, "T4");
        panelMain.add(tAbout, "ABOUT");

        // Adicionar menu auxiliar lateral e telas do cardlayout à janela principal
        add(list, BorderLayout.WEST);
        add(panelMain, BorderLayout.CENTER);
        setVisible(true);
    }
}
