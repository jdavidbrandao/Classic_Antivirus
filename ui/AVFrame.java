package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import core.*;

@SuppressWarnings("serial")
public class AVFrame extends JFrame {

    // Destaque seleção de  botão
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

    private void addThreat(DefaultTableModel model, String path, String date) {
            if (model.getRowCount() >= 50) {
                model.removeRow(0);
            }
            model.addRow(new Object[]{path, date});
    }

    // Janela
    // ======================================================================
    AVFrame(String nome) {

        super(nome);
        setSize(800, 610);
        // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        // Lateral Opções
        // ======================================================================
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

        // Panel Auxiliar para alinhamento do menu lateral
        JPanel listTop = new JPanel();
        listTop.setLayout(new BoxLayout(listTop, BoxLayout.Y_AXIS));

        // Criação dos botões
        Font menuFont = new Font("Segoe UI", Font.BOLD, 13);

        JButton bl1 = new JButton("Scanner");
        bl1.setFont(menuFont);
        bl1.setVerticalTextPosition(SwingConstants.BOTTOM);
        bl1.setHorizontalTextPosition(SwingConstants.CENTER);
        bl1.setBackground(Color.WHITE);
        bl1.setOpaque(true);
        bl1.setBorderPainted(false);
        bl1.setFocusPainted(false);
        selectMenuButton(bl1);

        ImageIcon icon1 = new ImageIcon(
            getClass().getResource("/ui/resource/icon1.png")
        );
        bl1.setIcon(icon1);

        JButton bl2 = new JButton("Directory Scanner");
        bl2.setFont(menuFont);
        bl2.setVerticalTextPosition(SwingConstants.BOTTOM);
        bl2.setHorizontalTextPosition(SwingConstants.CENTER);
        bl2.setBackground(Color.WHITE);
        bl2.setOpaque(true);
        bl2.setBorderPainted(false);
        bl2.setFocusPainted(false);

        ImageIcon icon2 = new ImageIcon(
            getClass().getResource("/ui/resource/icon2.png")
        );
        bl2.setIcon(icon2);

        JButton bl3 = new JButton("Real Time Analyzer");
        bl3.setFont(menuFont);
        bl3.setVerticalTextPosition(SwingConstants.BOTTOM);
        bl3.setHorizontalTextPosition(SwingConstants.CENTER);
        bl3.setBackground(Color.WHITE);
        bl3.setOpaque(true);
        bl3.setBorderPainted(false);
        bl3.setFocusPainted(false);

        ImageIcon icon3 = new ImageIcon(
            getClass().getResource("/ui/resource/icon3.png")
        );
        bl3.setIcon(icon3);

        JButton bl4 = new JButton("Quarantine");
        bl4.setFont(menuFont);
        bl4.setVerticalTextPosition(SwingConstants.BOTTOM);
        bl4.setHorizontalTextPosition(SwingConstants.CENTER);
        bl4.setBackground(Color.WHITE);
        bl4.setOpaque(true);
        bl4.setBorderPainted(false);
        bl4.setFocusPainted(false);

        ImageIcon icon4 = new ImageIcon(
            getClass().getResource("/ui/resource/icon4.png")
        );
        bl4.setIcon(icon4);

        JButton about = new JButton("About");
        about.setFont(menuFont);
        about.setVerticalTextPosition(SwingConstants.BOTTOM);
        about.setHorizontalTextPosition(SwingConstants.CENTER);
        about.setBackground(Color.WHITE);
        about.setOpaque(true);
        about.setBorderPainted(false);
        about.setFocusPainted(false);

        ImageIcon icon5 = new ImageIcon(
            getClass().getResource("/ui/resource/icon5.png")
        );
        about.setIcon(icon5);

        JButton scan = new JButton("Scan my Computer");
        scan.setFont(menuFont);
        scan.setBackground(Color.WHITE);
        scan.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,"Verifying files...\nPlease wait.","Verifying",JOptionPane.INFORMATION_MESSAGE);
            ArrayList<Object> threatFound = DoScan.scanComputer();
            if ((Boolean) threatFound.get(0)){
                JOptionPane.showMessageDialog(this,"Threat detected!\nA malicious file was found: " + (String) threatFound.get(1),"Threat Detected",JOptionPane.ERROR_MESSAGE);
                boolean success = DoScan.removeFile((String) threatFound.get(1));
                if (success) {
                	JOptionPane.showMessageDialog(this,"Threat killed.\nYour system is safe.", "Your system is safe",JOptionPane.PLAIN_MESSAGE);
                } else {
                	JOptionPane.showMessageDialog(this,"Couldnt delete the Thread.\nYour system is unsafe.", "Your system is unsafe",JOptionPane.ERROR_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(this,"No threats detected.\nYour system is safe.", "No Threat Detected",JOptionPane.PLAIN_MESSAGE);
            }
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

            File folder = chooser.getSelectedFile();

            //JOptionPane.showMessageDialog(this,"Verifying files...\nPlease wait.","Verifying",JOptionPane.INFORMATION_MESSAGE);
            JOptionPane pane = new JOptionPane("Verifying files...\nPlease wait.", JOptionPane.INFORMATION_MESSAGE);
            pane.setOptions(new Object[]{});
            
            JDialog dialog = pane.createDialog(this, "Verifying");
            dialog.setModal(false);
            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            dialog.setVisible(true);

            HashMap<Integer, String> threatFound = DoScan.scanFolder(folder);

            if (threatFound.size() > 0) {
	            for (Integer indice : threatFound.keySet()){
	                JOptionPane.showMessageDialog(this,"Threat detected!\nLocation: " + threatFound.get(indice) + "\nA malicious file was found.","Threat Detected",JOptionPane.ERROR_MESSAGE);
	            }
	            for (Integer indice : threatFound.keySet()) {
		            try {
		                boolean neutralize = DoScan.removeFile((String) threatFound.get(indice));
		                if (neutralize == false) {
		                	JOptionPane.showMessageDialog(this,"Unable to remove the malicious file.","Access Denied Error",JOptionPane.ERROR_MESSAGE);
		                } else {
		                	JOptionPane.showMessageDialog(this,"Threat neutralized!","Success",JOptionPane.PLAIN_MESSAGE);
		                }
		                continue;
		            }catch (Exception ex) {
		                JOptionPane.showMessageDialog(this,"Unable to open directory.","Error",JOptionPane.ERROR_MESSAGE);
		            }
	            }
            } else{
                JOptionPane.showMessageDialog(this, "No threats detected.\nYour system is safe.", "No Threat Detected",JOptionPane.PLAIN_MESSAGE);
            }
            dialog.dispose();
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

        JCheckBox desktop = new JCheckBox("Destkop");
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
        Thread[] threads = new Thread[3];
        rtaToggle.addActionListener(e -> {
            if (!running[0]) {

                // Verifica se pelo menos um checkbox está selecionado
                if (!temp.isSelected() && !downloads.isSelected() && !desktop.isSelected()) {
                    JOptionPane.showMessageDialog(this, "Select at least one directory.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                running[0] = true;
                rtaToggle.setText("Stop");
                rtaToggle.setBackground(new Color(220, 120, 120));

                // Exemplo de uso
                if (temp.isSelected()) {
                	Thread t1 = new Thread(new RealTimeAnalyzer("G:\\Curse"));
                	t1.start();
                	threads[0] = t1;
                    System.out.println("Monitorando TEMP");
                }
                if (downloads.isSelected()) {
                	Thread t2 = new Thread(new RealTimeAnalyzer("G:\\Curse"));
                	t2.start();
                	threads[1] = t2;
                    System.out.println("Monitorando DOWNLOADS");
                }
                if (desktop.isSelected()) {
                	Thread t3 = new Thread(new RealTimeAnalyzer("G:\\Curse"));
                	t3.start();
                	threads[2] = t3;
                    System.out.println("Monitorando DESKTOP");
                }

            } else {
                running[0] = false;
                for (Thread t : threads) {
                	if (t != null && t.isAlive()) {
                		t.interrupt();
                	}
                }
                rtaToggle.setText("Start");
                rtaToggle.setBackground(Color.WHITE);
            }
        });




        // Tamanho dos botões
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

        // Tela em destaque
        // ======================================================================
        CardLayout cardLayout = new CardLayout();
        JPanel panelMain = new JPanel(cardLayout);

        // ================= TELA 1 =================
        JPanel t1 = new JPanel();
        t1.setLayout(new BorderLayout());
        t1.add(createHeader("Scanner"), BorderLayout.NORTH);

        ImageIcon icon1p = new ImageIcon(
                getClass().getResource("/ui/resource/icon1p.png")
        );

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

        // ================= TELA 2 =================
        JPanel t2 = new JPanel();
        t2.setLayout(new BorderLayout());
        t2.add(createHeader("Directory"), BorderLayout.NORTH);

        ImageIcon icon2p = new ImageIcon(
                getClass().getResource("/ui/resource/icon2p.png")
        );

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

        // ================= TELA 3 =================
        JPanel t3 = new JPanel();
        t3.setLayout(new BorderLayout());
        t3.add(createHeader("Real Time Analyzer"), BorderLayout.NORTH);

        ImageIcon icon3p = new ImageIcon(
            getClass().getResource("/ui/resource/icon3p.png")
        );

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

        // ================= TELA 4 =================
        JPanel t4 = new JPanel();
        t4.setLayout(new BorderLayout());
        t4.add(createHeader("Quarantine"), BorderLayout.NORTH);

        String[] columns = {"File Location", "Date"};
        DefaultTableModel quarantineModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }   
        };

        JTable quarantineTable = new JTable(quarantineModel);
        quarantineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        quarantineTable.setRowHeight(22);
        quarantineTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(quarantineTable);
        scroll.setPreferredSize(new Dimension(500, 0));

        JPanel actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        actions.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

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
        int row = quarantineTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a threat first.",  "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
        String path = (String) quarantineModel.getValueAt(row, 0);

          //DELETA ARQUIVO AQUI

        quarantineModel.removeRow(row);
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
        int row = quarantineTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a threat first.",  "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String path = (String) quarantineModel.getValueAt(row, 0);

        //RESTAURA AQUI

        quarantineModel.removeRow(row);
        });

        actions.add(deleteBtn);
        actions.add(Box.createVerticalStrut(5));
        actions.add(restoreBtn);

        JPanel center = new JPanel(new BorderLayout());
        center.add(scroll, BorderLayout.CENTER);
        center.add(actions, BorderLayout.EAST);

        t4.add(center, BorderLayout.CENTER);

        // ================= ABOUT =================
        JPanel tAbout = new JPanel();
        tAbout.setLayout(new BorderLayout());
        tAbout.add(createHeader("About"), BorderLayout.NORTH);

        ImageIcon icon5p = new ImageIcon(
            getClass().getResource("/ui/resource/icon5p.png")
        );

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

        // Adiciona telas ao CardLayout
        panelMain.add(t1, "T1");
        panelMain.add(t2, "T2");
        panelMain.add(t3, "T3");
        panelMain.add(t4, "T4");
        panelMain.add(tAbout, "ABOUT");

        // Adicionar na Janela
        add(list, BorderLayout.WEST);
        add(panelMain, BorderLayout.CENTER);

        setVisible(true);
    }
}
