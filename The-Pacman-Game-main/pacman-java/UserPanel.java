import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UserPanel extends JFrame {

    private JTextField playerField;
    

    public UserPanel() {

SoundManager.playBackgroundMusic(
    "pacman-java/sounds/menu_music.wav"
);
        setTitle("PAC-MAN");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel background = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;

                GradientPaint gp = new GradientPaint(
                        0, 0,
                        Color.BLACK,
                        0, getHeight(),
                        new Color(0, 0, 90));

                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        background.setBorder(new EmptyBorder(20, 20, 20, 20));

        // ================= TITLE =================

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("PAC-MAN");
        title.setForeground(Color.YELLOW);
        title.setFont(new Font("Arial", Font.BOLD, 52));

        titlePanel.add(title);

        background.add(titlePanel, BorderLayout.NORTH);

        // ================= CENTER =================

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        centerPanel.setOpaque(false);

        // LEFT CARD

        JPanel leftCard = new JPanel();
        leftCard.setLayout(new BoxLayout(leftCard, BoxLayout.Y_AXIS));
        leftCard.setBackground(new Color(20, 20, 20));
        leftCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.YELLOW, 2),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel welcome = new JLabel("WELCOME PLAYER");
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcome.setForeground(Color.YELLOW);
        welcome.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel desc = new JLabel(
                "<html><center>" +
                        "Enter your name and begin your journey.<br>" +
                        "Collect pellets, avoid ghosts and score high." +
                        "</center></html>"
        );

        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        desc.setForeground(Color.WHITE);

        JLabel playerLabel = new JLabel("PLAYER NAME");
        playerLabel.setForeground(Color.YELLOW);
        playerLabel.setFont(new Font("Arial", Font.BOLD, 18));

        playerField = new JTextField();
        playerField.setMaximumSize(new Dimension(300, 40));
        playerField.setFont(new Font("Arial", Font.PLAIN, 18));

        JButton startBtn = createButton("▶ START GAME");
        JButton exitBtn = createButton("✖ EXIT");

        startBtn.addActionListener(e -> startGame());
        exitBtn.addActionListener(e -> System.exit(0));

        leftCard.add(welcome);
        leftCard.add(Box.createVerticalStrut(20));
        leftCard.add(desc);
        leftCard.add(Box.createVerticalStrut(40));
        leftCard.add(playerLabel);
        leftCard.add(Box.createVerticalStrut(10));
        leftCard.add(playerField);
        leftCard.add(Box.createVerticalStrut(30));
        leftCard.add(startBtn);
        leftCard.add(Box.createVerticalStrut(15));
        leftCard.add(exitBtn);

        // RIGHT CARD

        JPanel rightCard = new JPanel();
        rightCard.setLayout(new BoxLayout(rightCard, BoxLayout.Y_AXIS));
        rightCard.setBackground(new Color(20, 20, 20));
        rightCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.CYAN, 2),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel controlTitle = new JLabel("GAME CONTROLS");
        controlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlTitle.setForeground(Color.CYAN);
        controlTitle.setFont(new Font("Arial", Font.BOLD, 24));

        JTextArea controls = new JTextArea(
                """
                ↑ Move Up
                
                ↓ Move Down
                
                ← Move Left
                
                → Move Right
                
                M = Mute / Unmute
                
                OBJECTIVE
                
                • Eat all pellets
                • Avoid ghosts
                • Score maximum points
                • Survive with 3 lives
                """
        );

        controls.setEditable(false);
        controls.setFocusable(false);
        controls.setBackground(new Color(20, 20, 20));
        controls.setForeground(Color.WHITE);
        controls.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        rightCard.add(controlTitle);
        rightCard.add(Box.createVerticalStrut(20));
        rightCard.add(controls);

        centerPanel.add(leftCard);
        centerPanel.add(rightCard);

        background.add(centerPanel, BorderLayout.CENTER);

        // ================= FOOTER =================

        JLabel footer = new JLabel(
                "PAC-MAN • Java Swing Edition",
                SwingConstants.CENTER);

        footer.setForeground(Color.LIGHT_GRAY);

        background.add(footer, BorderLayout.SOUTH);

        add(background);
        setVisible(true);
    }

    private JButton createButton(String text) {

        JButton button = new JButton(text);

        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 45));

        button.setFont(new Font("Arial", Font.BOLD, 18));

        button.setBackground(Color.YELLOW);
        button.setForeground(Color.BLACK);

        button.setFocusPainted(false);

        return button;
    }

    private void startGame() {
SoundManager.stopBackgroundMusic();

SoundManager.playBackgroundMusic(
    "pacman-java/sounds/game_music.wav"
);
        String playerName = playerField.getText().trim();

        if (playerName.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your name!"
            );

            return;
        }

        JFrame frame = new JFrame("Pac Man - " + playerName);

        ImageIcon icon =
                new ImageIcon(
                        App.class.getResource("pacmanRight.png")
                );

        frame.setIconImage(icon.getImage());

        PacMan pacmanGame = new PacMan();

        frame.add(pacmanGame);
        frame.pack();

        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);

        pacmanGame.requestFocus();

        dispose();

    }
    
}