package ui;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class AVFrame extends JFrame{

    //Destaque seleção de botão
    private JButton selectedButton = null;
    private void selectMenuButton(JButton button){
        if(selectedButton != null){
            selectedButton.setBackground(Color.WHITE);
        }
        button.setBackground(new Color(140,140,140));
        selectedButton = button;
    }

    //Personalização dos headers dos menus
    private JPanel createHeader(String titulo){
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(120,120,120));
        header.setPreferredSize(new Dimension(0, 50));

        JLabel label = new JLabel(titulo);
        label.setFont(new Font("Segoe UI", Font.BOLD, 26));
        header.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(0,0,0)),
        BorderFactory.createEmptyBorder(0, 20, 0, 0)
    ));

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

    //Janela
// ======================================================================    
    AVFrame(String nome){
        super(nome);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

    //Lateral Opções
// ======================================================================
        //Menu lateral
        JPanel list = new JPanel();
        list.setBackground(Color.DARK_GRAY);
        list.setLayout(new BorderLayout());
        list.setPreferredSize(new Dimension(160,0));
        list.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0,0,0)),
        BorderFactory.createEmptyBorder(0, 0, 0, 0)
    ));
        //Panel Auxiliar para fazer alinhamento do menu lateral
        JPanel listTop = new JPanel();
        listTop.setLayout(new BoxLayout(listTop, BoxLayout.Y_AXIS));
        
        //Criação dos botões
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

        JButton about = new JButton("About");
        about.setFont(menuFont);
        about.setVerticalTextPosition(SwingConstants.BOTTOM);
        about.setHorizontalTextPosition(SwingConstants.CENTER);
        about.setBackground(Color.WHITE);
        about.setOpaque(true);
        about.setBorderPainted(false);
        about.setFocusPainted(false);
        ImageIcon icon4 = new ImageIcon(
            getClass().getResource("/ui/resource/icon4.png")
        );
        about.setIcon(icon4);

        JButton scan = new JButton("Scan my Computer"); 
        scan.setFont(menuFont); 
        scan.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,"Verifying files...\nPlease wait.","Verifying", JOptionPane.INFORMATION_MESSAGE);
            // Simulação de verificação
            boolean threatFound = Math.random() < 0.5;
            if (threatFound) {
                JOptionPane.showMessageDialog(this,"Threat detected!\nA malicious file was found.","Threat Detected", JOptionPane.ERROR_MESSAGE);
                //Aqui tem que ter algo similar que ocorre no botao "dir" pra abrir a janela do explorador de arquivos nativo do windowns
            } else {
                JOptionPane.showMessageDialog(this, "No threats detected.\nYour system is safe.", "No Threat Detected", JOptionPane.PLAIN_MESSAGE);
            }
        });
        scan.setBackground(Color.WHITE);
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

        JOptionPane.showMessageDialog(this,"Verifying files...\nPlease wait.","Verifying", JOptionPane.INFORMATION_MESSAGE);
        // Simulação de verificação
        boolean threatFound = Math.random() < 0.5;
            if (threatFound) {
                JOptionPane.showMessageDialog(this,"Threat detected!\nLocation: " + folder.getAbsolutePath() + "\nA malicious file was found.","Threat Detected", JOptionPane.ERROR_MESSAGE);
                try {
                    Desktop.getDesktop().open(folder);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Unable to open directory.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } else {
                JOptionPane.showMessageDialog(this, "No threats detected.\nYour system is safe.", "No Threat Detected", JOptionPane.PLAIN_MESSAGE);
            }
        });

        dir.setBackground(Color.WHITE);
        dir.setFocusPainted(false); 
        dir.setOpaque(true); 
        dir.setMaximumSize(new Dimension(180, 40)); 
        dir.setPreferredSize(new Dimension(180, 40));
        dir.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton rtaToggle = new JButton("Start"); 
        rtaToggle.setFont(menuFont); 
        rtaToggle.setBackground(Color.WHITE);
        rtaToggle.setFocusPainted(false); 
        rtaToggle.setOpaque(true); 
        rtaToggle.setMaximumSize(new Dimension(180, 40)); 
        rtaToggle.setPreferredSize(new Dimension(180, 40));
        rtaToggle.setAlignmentX(Component.CENTER_ALIGNMENT);

        final boolean[] running = { false };
        rtaToggle.addActionListener(e -> {
            if (!running[0]) {
                running[0] = true;
                JOptionPane.showMessageDialog(this,"RTA activated\n","Real Time Analyzer", JOptionPane.INFORMATION_MESSAGE);
                rtaToggle.setText("Stop");
                rtaToggle.setBackground(new Color(220, 120, 120));
            } else {
                running[0] = false;
                JOptionPane.showMessageDialog(this,"RTA desactivated\n","Real Time Analyzer", JOptionPane.INFORMATION_MESSAGE);
                rtaToggle.setText("Start");
                rtaToggle.setBackground(Color.WHITE);
            }
        });

        Dimension checkBox = new Dimension(180, 25);
        JCheckBox temp = new JCheckBox("Temp");
        temp.setFont((menuFont));
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

        //Tamanho dos botões
        Dimension blSize = new Dimension(160, 100);
        bl1.setMaximumSize(blSize);
        bl2.setMaximumSize(blSize);
        bl3.setMaximumSize(blSize);
        about.setMaximumSize(blSize);
        bl1.setPreferredSize(blSize);
        bl2.setPreferredSize(blSize);
        bl3.setPreferredSize(blSize);
        about.setPreferredSize(blSize);

        //Alinhando para esquerda 
        bl1.setAlignmentX(Component.LEFT_ALIGNMENT);
        bl2.setAlignmentX((Component.LEFT_ALIGNMENT));
        bl3.setAlignmentX((Component.LEFT_ALIGNMENT));
        about.setAlignmentX(Component.LEFT_ALIGNMENT);

        //Espaçamento e adicionando os botões ao Panel auxiliar
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
        listTop.add(about);
        listTop.add(createSeparator());
        listTop.add(Box.createVerticalStrut(10));
        listTop.add(createSeparator());

        //Panel principal recebendo o auxiliar
        list.add(listTop, BorderLayout.NORTH);

    //Tela em destaque
// ======================================================================
        CardLayout cardLayout = new CardLayout();
        JPanel panelMain = new JPanel(cardLayout);

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

        JPanel t2 = new JPanel();
        t2.setLayout(new BorderLayout());
        t2.add(createHeader("Directory Scanner"), BorderLayout.NORTH);

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

        JPanel tAbout = new JPanel();
        tAbout.setLayout(new BorderLayout());
        tAbout.add(createHeader("About"), BorderLayout.NORTH);

        ImageIcon icon4p = new ImageIcon(
            getClass().getResource("/ui/resource/icon4p.png")
        );
        JLabel iconLabel4 = new JLabel(icon4p);
        iconLabel4.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel4.setVerticalAlignment(SwingConstants.TOP);
        iconLabel4.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(
        BorderFactory.createEmptyBorder(0, 20, 0, 0)
        );

        JLabel aboutText = new JLabel(
        "<html>" +
        "<div style='width:420px; font-family:Segoe UI; font-size: 10px;'>" +

        "<h3>Scanner</h3>" +
        "<p>Perform a full computer scan for suspicious or malicious files.</p>" +

        "<h3>Directory Scanner</h3>" +
        "<p>Allows you to select specific directories for manual analysis, such as home or external folders.</p>" +

        "<h3>Real Time Analyzer</h3>" +
        "<p>Monitors the system in real time, analyzing changes in selected folders.</p>" +
        "</div>" +
        "</html>"
        );
        aboutText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel centerPanel4 = new JPanel();
        centerPanel4.setLayout(new BoxLayout(centerPanel4, BoxLayout.Y_AXIS));
        centerPanel4.setOpaque(false);
        centerPanel4.add(Box.createVerticalStrut(30));
        centerPanel4.add(iconLabel4);
        centerPanel4.add(Box.createVerticalStrut(25));
        centerPanel4.add(aboutText);
        
        contentWrapper.add(centerPanel4, BorderLayout.CENTER);
        tAbout.add(contentWrapper, BorderLayout.CENTER);

        //Listeners dos botões do Menu
        bl1.addActionListener(e -> {
        cardLayout.show(panelMain, "T1");
        selectMenuButton(bl1);});

        bl2.addActionListener(e -> {
        cardLayout.show(panelMain, "T2");
        selectMenuButton(bl2);});

        bl3.addActionListener(e -> {
        cardLayout.show(panelMain, "T3");
        selectMenuButton(bl3);});

        about.addActionListener(e -> {
        cardLayout.show(panelMain, "ABOUT");
        selectMenuButton(about);});

        //Adiciona a tela de acordo com o botão selecionado
        panelMain.add(t1,"T1");
        panelMain.add(t2,"T2");
        panelMain.add(t3,"T3");
        panelMain.add(tAbout,"ABOUT");
        
    //Adicionar na Janela
    add(list, BorderLayout.WEST);
    add(panelMain, BorderLayout.CENTER);
// ======================================================================
    setVisible(true);
    }
}

class Main{
    public static void main(String[] args) {
        new AVFrame("Pega-Gato");
    }
}