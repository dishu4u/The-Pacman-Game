import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;
import java.util.Map;


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
    boolean showInstructions = true;

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
        
        if (showInstructions) {
            g.setColor(new Color(0, 0, 0, 210)); 
            g.fillRect(0, 0, boardWidth, boardHeight);

            String title = "PAC-MAN";
            String controlsTitle = "HOW TO PLAY";
            String controls = "↑ ↓ ← → Arrow Keys to Move";
            String objMsg = "Eat all the food, avoid the ghosts!";
            String startMsg = "Press ANY KEY to Start!";

            // Title with shadow
            g.setFont(new Font("Arial", Font.BOLD, 50));
            FontMetrics fm = g.getFontMetrics();
            g.setColor(Color.BLUE);
            g.drawString(title, (boardWidth - fm.stringWidth(title)) / 2 + 4, boardHeight / 2 - 146);
            g.setColor(Color.YELLOW);
            g.drawString(title, (boardWidth - fm.stringWidth(title)) / 2, boardHeight / 2 - 150);

            // Draw game sprites for decoration
            int imgY = boardHeight / 2 - 110;
            g.drawImage(pacmanRightImage, boardWidth / 2 - 90, imgY, tileSize + 10, tileSize + 10, null);
            g.drawImage(redGhostImage, boardWidth / 2 - 30, imgY, tileSize + 10, tileSize + 10, null);
            g.drawImage(blueGhostImage, boardWidth / 2 + 30, imgY, tileSize + 10, tileSize + 10, null);
            g.drawImage(pinkGhostImage, boardWidth / 2 + 90, imgY, tileSize + 10, tileSize + 10, null);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 26));
            fm = g.getFontMetrics();
            g.drawString(controlsTitle, (boardWidth - fm.stringWidth(controlsTitle)) / 2, boardHeight / 2 - 20);
            
            g.setColor(Color.CYAN);
            g.setFont(new Font("Arial", Font.PLAIN, 22));
            fm = g.getFontMetrics();
            g.drawString(controls, (boardWidth - fm.stringWidth(controls)) / 2, boardHeight / 2 + 20);
            
            g.setColor(Color.PINK);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            fm = g.getFontMetrics();
            g.drawString(objMsg, (boardWidth - fm.stringWidth(objMsg)) / 2, boardHeight / 2 + 60);
            
            // Blinking start message
            if ((System.currentTimeMillis() / 500) % 2 == 0) {
                g.setColor(Color.ORANGE);
                g.setFont(new Font("Arial", Font.BOLD, 24));
                fm = g.getFontMetrics();
                g.drawString(startMsg, (boardWidth - fm.stringWidth(startMsg)) / 2, boardHeight / 2 + 130);
            }
        }
    }

    public void move() {

    // ─── STEP 1: Move Pac-Man by its current velocity ───────────────────
    pacman.x += pacman.velocityX;
    pacman.y += pacman.velocityY;

    // ─── STEP 2: Pac-Man vs Wall Collision Detection ─────────────────────
    // If Pac-Man hits a wall, revert its position back
    for (Block wall : walls) {
        if (collision(pacman, wall)) {
            pacman.x -= pacman.velocityX;
            pacman.y -= pacman.velocityY;
            break;
        }
    }

    // ─── STEP 3: Pac-Man vs Ghost Collision Detection ────────────────────
    // If any ghost touches Pac-Man, reduce a life
    // If lives reach 0, trigger Game Over
    for (Block ghost : ghosts) {
        if (collision(ghost, pacman)) {
            lives -= 1;
            if (lives == 0) {
                gameOver = true;
                return;
            }
            resetPositions();
        }
    }
    Map<Block, int[]> nextPositions = new java.util.HashMap<>();

    for (Block ghost : ghosts) {
    nextPositions.put(ghost, new int[]{
        ghost.x + ghost.velocityX,
        ghost.y + ghost.velocityY
    });
}

    // ─── STEP 4: Ghost Movement Logic ────────────────────────────────────
    for (Block ghost : ghosts) {

        // Force ghost upward if stuck in ghost house row
        if (ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D') {
            ghost.updateDirection('U');
        }

        // Calculate ghost's next position based on current velocity
    int nextX = nextPositions.get(ghost)[0];
    int nextY = nextPositions.get(ghost)[1];


        // ── 4a: Ghost vs Ghost Collision Check (Pre-Move) ────────────────
        // Check if next position overlaps with any other ghost
        // If blocked, pick a new random direction and skip this frame
        boolean blockedByGhost = false;

for (Block other : ghosts) {
    if (other == ghost) continue;

    int[] otherNext = nextPositions.get(other);

    if (nextX < otherNext[0] + other.width &&
        nextX + ghost.width > otherNext[0] &&
        nextY < otherNext[1] + other.height &&
        nextY + ghost.height > otherNext[1]) {

        blockedByGhost = true;
        break;
    }
}

        if (blockedByGhost) {
            // Redirect ghost and do not move this frame
            ghost.updateDirection(directions[random.nextInt(4)]);
            continue;
        }

        // ── 4b: Ghost vs Wall Collision Check (Pre-Move) ─────────────────
        // Check if next position overlaps with any wall or board boundary
        // If blocked, choose a smart direction toward Pac-Man
        boolean blockedByWall = false;
for (Block wall : walls) {

    if (nextX < wall.x + wall.width &&
        nextX + ghost.width > wall.x &&
        nextY < wall.y + wall.height &&
        nextY + ghost.height > wall.y) {

        blockedByWall = true;
        break;
    }
}
        if (blockedByWall || nextX <= 0 || nextX + ghost.width >= boardWidth) {
            // Redirect ghost smartly toward Pac-Man
            ghost.updateDirection(getSmartDirection(ghost));
        } else {
            // No collision detected — move ghost to next position
            ghost.x = nextX;
            ghost.y = nextY;
        }
    }

    // ─── STEP 5: Pac-Man vs Food Collision Detection ─────────────────────
    // If Pac-Man eats a food dot, remove it and increase score by 10
    Block foodEaten = null;
    for (Block food : foods) {
        if (collision(pacman, food)) {
            foodEaten = food;
            score += 10;
        }
    }
    foods.remove(foodEaten);

    // ─── STEP 6: Level Reset Check ───────────────────────────────────────
    // If all food is eaten, reload the map and reset all positions
    if (foods.isEmpty()) {
        loadMap();
        resetPositions();
    }
}

    private char getSmartDirection(Block ghost) {

    int dx = pacman.x - ghost.x;
    int dy = pacman.y - ghost.y;

    // 70% chance to move toward Pac-Man
    if (random.nextInt(100) < 70) {

        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > 0 ? 'R' : 'L';
        } else {
            return dy > 0 ? 'D' : 'U';
        }
    }

    // 30% chance to choose a random direction
    return directions[random.nextInt(4)];
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
        if (showInstructions) {
            repaint();
            return;
        }
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (showInstructions) {
            showInstructions = false;
            return;
        }
        if (gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
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

