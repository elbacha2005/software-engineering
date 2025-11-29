package CodeQuest.Main;

import java.awt.*;

// Handles GUI rendering for game states (pause screen, etc.)
public class GUI {
    GamePanel gamePanel; // Reference to game panel
    Graphics2D g2; // Graphics context

    // Constructor
    public GUI(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    // Main draw method called each frame
    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setFont(new Font("Arial", Font.BOLD, 20)); // Set default font

        if (gamePanel.gameState == gamePanel.pauseState) {
            drawPauseScreen(); // Show pause overlay
        }
    }

    // Draw semi-transparent pause screen with centered text
    public void drawPauseScreen() {
        String text = "GAME PAUSE";
        int length = (int)(g2.getFontMetrics().getStringBounds(text, g2).getWidth());
        int x = gamePanel.screenWidth / 2 - length / 2; // Center horizontally
        int y = gamePanel.screenHeight / 2; // Center vertically

        // Draw semi-transparent black overlay
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); // 50% opacity
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gamePanel.screenWidth, gamePanel.screenHeight);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); // Reset to 100% opacity
        g2.setColor(Color.WHITE);
        g2.drawString(text, x, y); // Draw centered text
    }
}
