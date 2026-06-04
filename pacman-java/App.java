import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class App {
    private static final int ROW_COUNT = 21;
    private static final int COLUMN_COUNT = 19;
    private static final int TILE_SIZE = 32;

    private static final int BOARD_WIDTH = COLUMN_COUNT * TILE_SIZE;
    private static final int BOARD_HEIGHT = ROW_COUNT * TILE_SIZE;

    private static final String WELCOME_SCREEN = "WELCOME_SCREEN";
    private static final String GAME_SCREEN = "GAME_SCREEN";

    private static final Color PACMAN_YELLOW = new Color(255, 230, 0);
    private static final Color MAZE_BLUE = new Color(55, 55, 210);
    private static final Color DARK_BLUE = new Color(10, 10, 35);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::createAndShowGameWindow);
    }

    private static void createAndShowGameWindow() {
        JFrame frame = new JFrame("Pac Man");

        ImageIcon icon = new ImageIcon(App.class.getResource("pacmanRight.png"));
        frame.setIconImage(icon.getImage());

        CardLayout cardLayout = new CardLayout();
        JPanel screenContainer = new JPanel(cardLayout);

        JPanel welcomeScreen = createWelcomeScreen(cardLayout, screenContainer);
        screenContainer.add(welcomeScreen, WELCOME_SCREEN);

        frame.add(screenContainer);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        cardLayout.show(screenContainer, WELCOME_SCREEN);
    }

    private static JPanel createWelcomeScreen(CardLayout cardLayout, JPanel screenContainer) {
        JPanel welcomePanel = new ArcadeWelcomePanel();
        welcomePanel.setLayout(new GridBagLayout());
        welcomePanel.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("PAC-MAN");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 60));
        titleLabel.setForeground(PACMAN_YELLOW);

        JLabel subtitleLabel = new JLabel("Eat the pellets. Avoid the ghosts. Chase the high score!");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 17));
        subtitleLabel.setForeground(Color.WHITE);

        JLabel instructionLabel = new JLabel("Use arrow keys to move after starting the game");
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        instructionLabel.setForeground(Color.LIGHT_GRAY);

        JLabel dividerLabel = new JLabel("●   ●   ●   ●   ●   ●   ●");
        dividerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dividerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        dividerLabel.setForeground(PACMAN_YELLOW);

        JButton startButton = createStartButton();

        startButton.addActionListener(event -> {
            startButton.setEnabled(false);

            PacMan pacmanGame = new PacMan();
            screenContainer.add(pacmanGame, GAME_SCREEN);

            cardLayout.show(screenContainer, GAME_SCREEN);
            screenContainer.revalidate();
            screenContainer.repaint();

            SwingUtilities.invokeLater(pacmanGame::requestFocusInWindow);
        });

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(18));
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(14));
        contentPanel.add(dividerLabel);
        contentPanel.add(Box.createVerticalStrut(16));
        contentPanel.add(instructionLabel);
        contentPanel.add(Box.createVerticalStrut(34));
        contentPanel.add(startButton);

        welcomePanel.add(contentPanel);

        return welcomePanel;
    }

    private static JButton createStartButton() {
        JButton startButton = new JButton("START GAME");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setFont(new Font("Arial", Font.BOLD, 22));
        startButton.setBackground(PACMAN_YELLOW);
        startButton.setForeground(Color.BLACK);
        startButton.setFocusPainted(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(12, 34, 12, 34)
        ));

        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                startButton.setBackground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent event) {
                startButton.setBackground(PACMAN_YELLOW);
            }
        });

        return startButton;
    }

    private static class ArcadeWelcomePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D g2d = (Graphics2D) graphics.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            drawBackground(g2d);
            drawMazeBorder(g2d);
            drawPellets(g2d);
            drawPacmanIcon(g2d);
            drawGhostIcon(g2d, 92, 105, new Color(255, 80, 80));
            drawGhostIcon(g2d, getWidth() - 130, 105, new Color(255, 170, 210));
            drawGhostIcon(g2d, 92, getHeight() - 155, new Color(80, 220, 220));
            drawGhostIcon(g2d, getWidth() - 130, getHeight() - 155, new Color(255, 180, 70));

            g2d.dispose();
        }

        private void drawBackground(Graphics2D g2d) {
            GradientPaint gradient = new GradientPaint(
                    0, 0, Color.BLACK,
                    0, getHeight(), DARK_BLUE
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        private void drawMazeBorder(Graphics2D g2d) {
            g2d.setColor(MAZE_BLUE);
            g2d.setStroke(new BasicStroke(5));

            int padding = 24;
            g2d.drawRoundRect(
                    padding,
                    padding,
                    getWidth() - (padding * 2),
                    getHeight() - (padding * 2),
                    26,
                    26
            );

            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(
                    padding + 14,
                    padding + 14,
                    getWidth() - ((padding + 14) * 2),
                    getHeight() - ((padding + 14) * 2),
                    20,
                    20
            );
        }

        private void drawPellets(Graphics2D g2d) {
            g2d.setColor(Color.WHITE);

            for (int x = 70; x < getWidth() - 70; x += 42) {
                g2d.fillOval(x, 62, 6, 6);
                g2d.fillOval(x, getHeight() - 68, 6, 6);
            }

            for (int y = 110; y < getHeight() - 110; y += 42) {
                g2d.fillOval(60, y, 6, 6);
                g2d.fillOval(getWidth() - 66, y, 6, 6);
            }
        }

        private void drawPacmanIcon(Graphics2D g2d) {
            int x = getWidth() / 2 - 30;
            int y = 110;

            g2d.setColor(PACMAN_YELLOW);
            g2d.fillArc(x, y, 60, 60, 35, 290);

            g2d.setColor(Color.BLACK);
            g2d.fillOval(x + 33, y + 13, 7, 7);
        }

        private void drawGhostIcon(Graphics2D g2d, int x, int y, Color ghostColor) {
            g2d.setColor(ghostColor);
            g2d.fillRoundRect(x, y, 38, 42, 18, 18);
            g2d.fillRect(x, y + 20, 38, 28);

            int[] xPoints = {x, x + 8, x + 16, x + 24, x + 32, x + 38};
            int[] yPoints = {y + 48, y + 38, y + 48, y + 38, y + 48, y + 48};
            g2d.fillPolygon(xPoints, yPoints, xPoints.length);

            g2d.setColor(Color.WHITE);
            g2d.fillOval(x + 8, y + 15, 9, 12);
            g2d.fillOval(x + 23, y + 15, 9, 12);

            g2d.setColor(Color.BLUE);
            g2d.fillOval(x + 11, y + 19, 4, 5);
            g2d.fillOval(x + 26, y + 19, 4, 5);
        }
    }
}