import java.awt.*;
import javax.swing.*;

public class WelcomeScreen extends JPanel {

    private final JFrame frame;

    public WelcomeScreen(JFrame frame) {
        this.frame = frame;

        setPreferredSize(new Dimension(608, 672));
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(Color.BLACK);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Pacman Image
        ImageIcon pacmanImg =
                new ImageIcon(getClass().getResource("./pacmanRight.png"));

        Image scaled =
                pacmanImg.getImage().getScaledInstance(
                        100,
                        100,
                        Image.SCALE_SMOOTH);

        JLabel pacmanIcon =
                new JLabel(new ImageIcon(scaled));

        pacmanIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel title = new JLabel("PACMAN");
        title.setFont(new Font("Arial", Font.BOLD, 80));
        title.setForeground(new Color(255, 230, 0));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitle =
                new JLabel("Classic Arcade Adventure");

        subtitle.setForeground(Color.CYAN);
        subtitle.setFont(new Font("Arial", Font.BOLD, 20));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Controls
        JLabel controls =
                new JLabel("Use Arrow Keys To Move");

        controls.setForeground(Color.WHITE);
        controls.setFont(new Font("Arial", Font.PLAIN, 16));
        controls.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Start Button
        JButton startButton =
                new JButton("START GAME");

        startButton.setBackground(
                new Color(255, 215, 0));

        startButton.setForeground(Color.BLACK);

        startButton.setFont(
                new Font("Arial", Font.BOLD, 24));

        startButton.setFocusPainted(false);

        startButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR));

        startButton.setBorder(
                BorderFactory.createLineBorder(
                        Color.ORANGE,
                        3));

        startButton.setMaximumSize(
                new Dimension(250, 60));

        startButton.setAlignmentX(
                Component.CENTER_ALIGNMENT);

        startButton.addActionListener(
                e -> startGame());

        centerPanel.add(Box.createVerticalGlue());

        centerPanel.add(pacmanIcon);
        centerPanel.add(Box.createRigidArea(
                new Dimension(0, 20)));

        centerPanel.add(title);
        centerPanel.add(Box.createRigidArea(
                new Dimension(0, 10)));

        centerPanel.add(subtitle);
        centerPanel.add(Box.createRigidArea(
                new Dimension(0, 20)));

        centerPanel.add(controls);
        centerPanel.add(Box.createRigidArea(
                new Dimension(0, 40)));

        centerPanel.add(startButton);

        centerPanel.add(Box.createVerticalGlue());

        JLabel footer =
                new JLabel(
                        "Created for GSSoC 2026",
                        SwingConstants.CENTER);

        footer.setForeground(Color.GRAY);
        footer.setFont(
                new Font("Arial",
                        Font.PLAIN,
                        14));

        add(centerPanel, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    private void startGame() {

        frame.getContentPane().removeAll();

        PacMan game = new PacMan();

        frame.add(game);

        frame.revalidate();
        frame.repaint();

        game.requestFocusInWindow();
    }
}