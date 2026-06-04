import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener {

    static class HighScore implements Comparable<HighScore> {
        String name;
        int score;

        HighScore(String name, int score) {
            this.name = name;
            this.score = score;
        }

        @Override
        public int compareTo(HighScore o) {
            return Integer.compare(o.score, this.score);
        }
    }

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'U'; // U D L R
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -tileSize/4;
            }
            else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = tileSize/4;
            }
            else if (this.direction == 'L') {
                this.velocityX = -tileSize/4;
                this.velocityY = 0;
            }
            else if (this.direction == 'R') {
                this.velocityX = tileSize/4;
                this.velocityY = 0;
            }
        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "X        bpo      X",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX" 
    };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    private List<HighScore> highScores = new ArrayList<>();
    private final String LEADERBOARD_FILE = "leaderboard.txt";
    private boolean hasPromptedHighScore = false;

    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'}; //up down left right
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        //load images
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();

        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        loadMap();
        loadLeaderboard();
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
        //how long it takes to start timer, milliseconds gone between frames
        gameLoop = new Timer(50, this); //20fps (1000/50)
        gameLoop.start();

    }

    public void loadLeaderboard() {
        highScores.clear();
        File file = new File(LEADERBOARD_FILE);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    try {
                        String name = parts[0].trim();
                        int score = Integer.parseInt(parts[1].trim());
                        highScores.add(new HighScore(name, score));
                    } catch (NumberFormatException e) {
                        // Skip malformed lines
                    }
                }
            }
            Collections.sort(highScores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveLeaderboard() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LEADERBOARD_FILE))) {
            for (HighScore hs : highScores) {
                writer.write(hs.name + "," + hs.score);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkAndAddHighScore() {
        if (hasPromptedHighScore) {
            return;
        }
        hasPromptedHighScore = true;

        boolean qualifies = false;
        if (highScores.size() < 5) {
            qualifies = true;
        } else if (score > highScores.get(highScores.size() - 1).score) {
            qualifies = true;
        }

        if (qualifies) {
            String playerName = showCustomHighScoreDialog(score);
            playerName = playerName.replace(",", " "); // Prevent CSV corruption
            highScores.add(new HighScore(playerName.trim(), score));
            Collections.sort(highScores);
            while (highScores.size() > 5) {
                highScores.remove(highScores.size() - 1);
            }
            saveLeaderboard();
        }
    }

    private String showCustomHighScoreDialog(int score) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New High Score!", true);
        dialog.setUndecorated(true); // Borderless for a modern look
        dialog.setSize(300, 190);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.BLACK);
        panel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));

        panel.add(Box.createVerticalStrut(15));

        JLabel titleLabel = new JLabel("NEW HIGH SCORE!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(10));

        JLabel scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(scoreLabel);

        panel.add(Box.createVerticalStrut(15));

        JTextField nameField = new JTextField(15);
        nameField.setMaximumSize(new Dimension(200, 30));
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.setBackground(Color.DARK_GRAY);
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(Color.WHITE);
        nameField.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 1));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(nameField);

        panel.add(Box.createVerticalStrut(15));

        JButton submitButton = new JButton("SUBMIT");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setBackground(Color.YELLOW);
        submitButton.setForeground(Color.BLACK);
        submitButton.setFocusPainted(false);
        submitButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        final String[] result = { "Anonymous" };
        ActionListener submitAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = nameField.getText().trim();
                if (!input.isEmpty()) {
                    result[0] = input;
                }
                dialog.dispose();
            }
        };
        submitButton.addActionListener(submitAction);
        nameField.addActionListener(submitAction);

        panel.add(submitButton);
        panel.add(Box.createVerticalStrut(15));

        dialog.add(panel);
        
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                nameField.requestFocusInWindow();
            }
        });

        dialog.setVisible(true);

        return result[0];
    }


    public void loadMap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c*tileSize;
                int y = r*tileSize;

                if (tileMapChar == 'X') { //block wall
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                }
                else if (tileMapChar == 'b') { //blue ghost
                    Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'o') { //orange ghost
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'p') { //pink ghost
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'r') { //red ghost
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'P') { //pacman
                    pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                }
                else if (tileMapChar == ' ') { //food
                    Block food = new Block(null, x + 14, y + 14, 4, 4);
                    foods.add(food);
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }
        //score
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver) {
            // Draw semi-transparent background
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, 0, boardWidth, boardHeight);

            // GAME OVER Header
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.setColor(Color.RED);
            String gameOverText = "GAME OVER";
            int textWidth = g.getFontMetrics().stringWidth(gameOverText);
            g.drawString(gameOverText, (boardWidth - textWidth) / 2, boardHeight / 5);

            // Current Score info
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.setColor(Color.WHITE);
            String scoreText = "Your Score: " + score;
            textWidth = g.getFontMetrics().stringWidth(scoreText);
            g.drawString(scoreText, (boardWidth - textWidth) / 2, boardHeight / 5 + 45);

            // LEADERBOARD header
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.setColor(Color.YELLOW);
            String leaderboardTitle = "LEADERBOARD (TOP 5)";
            textWidth = g.getFontMetrics().stringWidth(leaderboardTitle);
            g.drawString(leaderboardTitle, (boardWidth - textWidth) / 2, boardHeight / 5 + 110);

            // Draw table header
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.setColor(Color.CYAN);
            int startY = boardHeight / 5 + 150;
            int rowHeight = 35;
            
            // Draw column headers
            g.drawString("Rank", boardWidth / 2 - 150, startY);
            g.drawString("Name", boardWidth / 2 - 50, startY);
            g.drawString("Score", boardWidth / 2 + 100, startY);

            // Draw line underneath header
            g.setColor(Color.GRAY);
            g.drawLine(boardWidth / 2 - 170, startY + 5, boardWidth / 2 + 170, startY + 5);

            // Draw scores
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.setColor(Color.WHITE);
            int currentY = startY + 35;
            for (int i = 0; i < 5; i++) {
                g.setColor(Color.GRAY);
                g.drawString((i + 1) + ".", boardWidth / 2 - 140, currentY);

                if (i < highScores.size()) {
                    HighScore hs = highScores.get(i);
                    g.setColor(Color.WHITE);
                    g.drawString(hs.name, boardWidth / 2 - 50, currentY);
                    g.drawString(String.valueOf(hs.score), boardWidth / 2 + 100, currentY);
                } else {
                    g.setColor(Color.DARK_GRAY);
                    g.drawString("---", boardWidth / 2 - 50, currentY);
                    g.drawString("---", boardWidth / 2 + 100, currentY);
                }
                currentY += rowHeight;
            }

            // Restart notice
            g.setFont(new Font("Arial", Font.ITALIC, 16));
            g.setColor(Color.YELLOW);
            String restartText = "Press any key to restart";
            textWidth = g.getFontMetrics().stringWidth(restartText);
            g.drawString(restartText, (boardWidth - textWidth) / 2, boardHeight - 80);
        }
        else {
            g.drawString("x" + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
    }

    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        //check wall collisions
        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        //check ghost collisions
        for (Block ghost : ghosts) {
            if (collision(ghost, pacman)) {
                lives -= 1;
                if (lives == 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
            }

            if (ghost.y == tileSize*9 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for (Block wall : walls) {
                if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }

        //check food collision
        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);

        if (foods.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }

    public boolean collision(Block a, Block b) {
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
            checkAndAddHighScore();
            repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            hasPromptedHighScore = false;
            gameLoop.start();
        }
        // System.out.println("KeyEvent: " + e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
        }

        if (pacman.direction == 'U') {
            pacman.image = pacmanUpImage;
        }
        else if (pacman.direction == 'D') {
            pacman.image = pacmanDownImage;
        }
        else if (pacman.direction == 'L') {
            pacman.image = pacmanLeftImage;
        }
        else if (pacman.direction == 'R') {
            pacman.image = pacmanRightImage;
        }
    }
}

