import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener {

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

    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'}; //up down left right
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    boolean isPaused = false;
    String[] pauseMenuOptions = {"Resume", "Controls", "Quit"};
    int pauseMenuIndex = 0;
    boolean showControls = false;

    // Fonts and Colors for Pause Menu
    private Color pauseOverlayColor;
    private Font headerFont;
    private Font controlFont;
    private Font promptFont;
    private Font menuFont;
    private Font menuInstructionFont;

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
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
        //how long it takes to start timer, milliseconds gone between frames
        gameLoop = new Timer(50, this); //20fps (1000/50)
        gameLoop.start();

        // Initialize Pause Menu Fonts and Colors
        pauseOverlayColor = new Color(0, 0, 0, 150);
        headerFont = new Font("Monospaced", Font.BOLD, 40);
        controlFont = new Font("Monospaced", Font.PLAIN, 20);
        promptFont = new Font("Monospaced", Font.ITALIC, 16);
        menuFont = new Font("Monospaced", Font.PLAIN, 24);
        menuInstructionFont = new Font("Monospaced", Font.PLAIN, 14);

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
            g.drawString("Game Over: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
        else {
            g.drawString("x" + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize/2, tileSize/2);
        }

        if (isPaused) {
            drawPauseMenu(g);
        }
    }

    public void drawPauseMenu(Graphics g) {
        // Transparent Overlay
        g.setColor(pauseOverlayColor);
        g.fillRect(0, 0, boardWidth, boardHeight);

        g.setFont(headerFont);
        g.setColor(Color.YELLOW);
        
        if (showControls) {
            String text;
            int x;
            FontMetrics fm;

            // Header
            g.setFont(headerFont);
            g.setColor(Color.YELLOW);
            fm = g.getFontMetrics();
            text = "CONTROLS";
            x = (boardWidth - fm.stringWidth(text)) / 2;
            g.drawString(text, x, boardHeight / 2 - 150);
            
            // Movement Controls
            g.setFont(controlFont);
            g.setColor(Color.WHITE);
            fm = g.getFontMetrics();
            String[] controlLines = {
                "^ : Move Up",
                "v : Move Down",
                "<- : Move Left",
                "-> : Move Right",
                "P or ESC to Pause"
            };
            
            for (int i = 0; i < controlLines.length; i++) {
                text = controlLines[i];
                x = (boardWidth - fm.stringWidth(text)) / 2;
                g.drawString(text, x, boardHeight / 2 - 60 + (i * 40));
            }
            
            // Return Prompt
            g.setFont(promptFont);
            g.setColor(Color.YELLOW);
            fm = g.getFontMetrics();
            text = "Press ESC or BACKSPACE to return";
            x = (boardWidth - fm.stringWidth(text)) / 2;
            g.drawString(text, x, boardHeight / 2 + 160);
        } else {
            g.drawString("PAUSED", boardWidth / 2 - 70, boardHeight / 2 - 100);

            g.setFont(menuFont);
            for (int i = 0; i < pauseMenuOptions.length; i++) {
                if (i == pauseMenuIndex) {
                    g.setColor(Color.YELLOW);
                    g.drawString("> " + pauseMenuOptions[i], boardWidth / 2 - 60, boardHeight / 2 + (i * 40));
                } else {
                    g.setColor(Color.WHITE);
                    g.drawString("  " + pauseMenuOptions[i], boardWidth / 2 - 60, boardHeight / 2 + (i * 40));
                }
            }
            
            g.setFont(menuInstructionFont);
            g.setColor(Color.WHITE);
            g.drawString("UP/DOWN to Navigate, ENTER to Select", boardWidth / 2 - 140, boardHeight - 50);
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
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
            return;
        }

        int keyCode = e.getKeyCode();

        // Toggle Pause
        if (keyCode == KeyEvent.VK_P || keyCode == KeyEvent.VK_ESCAPE) {
            if (showControls) {
                showControls = false;
            } else {
                isPaused = !isPaused;
                if (isPaused) {
                    gameLoop.stop();
                } else {
                    gameLoop.start();
                }
                pauseMenuIndex = 0; // Reset index when opening menu
            }
            repaint();
            return;
        }

        if (isPaused) {
            if (showControls) {
                if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_BACK_SPACE) {
                    showControls = false;
                }
            } else {
                if (keyCode == KeyEvent.VK_UP) {
                    pauseMenuIndex = (pauseMenuIndex - 1 + pauseMenuOptions.length) % pauseMenuOptions.length;
                } else if (keyCode == KeyEvent.VK_DOWN) {
                    pauseMenuIndex = (pauseMenuIndex + 1) % pauseMenuOptions.length;
                } else if (keyCode == KeyEvent.VK_ENTER) {
                    if (pauseMenuOptions[pauseMenuIndex].equals("Resume")) {
                        isPaused = false;
                        gameLoop.start();
                    } else if (pauseMenuOptions[pauseMenuIndex].equals("Controls")) {
                        showControls = true;
                    } else if (pauseMenuOptions[pauseMenuIndex].equals("Quit")) {
                        java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(this);
                        if (window != null) {
                            window.dispose();
                        }
                    }
                }
            }
            repaint();
        } else {
            // Game movement
            if (keyCode == KeyEvent.VK_UP) {
                pacman.updateDirection('U');
            } else if (keyCode == KeyEvent.VK_DOWN) {
                pacman.updateDirection('D');
            } else if (keyCode == KeyEvent.VK_LEFT) {
                pacman.updateDirection('L');
            } else if (keyCode == KeyEvent.VK_RIGHT) {
                pacman.updateDirection('R');
            }

            if (pacman.direction == 'U') {
                pacman.image = pacmanUpImage;
            } else if (pacman.direction == 'D') {
                pacman.image = pacmanDownImage;
            } else if (pacman.direction == 'L') {
                pacman.image = pacmanLeftImage;
            } else if (pacman.direction == 'R') {
                pacman.image = pacmanRightImage;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}