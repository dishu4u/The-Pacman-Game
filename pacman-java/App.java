import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32;
        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;

        JFrame frame = new JFrame("Pac Man");

//        Set icon for taskbar and title bar.
        ImageIcon icon = new ImageIcon(App.class.getResource("pacmanRight.png"));
        frame.setIconImage(icon.getImage());

        // frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        WelcomeScreen welcomeScreen = new WelcomeScreen(frame);

        frame.add(welcomeScreen);
        frame.pack();
        frame.setVisible(true);
    }
}
