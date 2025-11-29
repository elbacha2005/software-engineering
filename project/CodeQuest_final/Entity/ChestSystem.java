package CodeQuest.Entity;

import CodeQuest.Tiles.AssetHandler;
import java.awt.*;
import java.awt.image.BufferedImage;

// Manages chest counter display - counts down as chests are opened
public class ChestSystem {

    private int maxChests = 4; // Total chests in game
    private int remainingChests = 4; // Chests not yet opened

    private BufferedImage chestIcon; // Chest icon sprite

    private int iconSize = 32; // Icon display size in pixels
    private int screenX = 20; // X position on screen
    private int screenY = 100; // Y position on screen (below keys)

    // Constructor: loads chest image from asset handler
    public ChestSystem() {
        chestIcon = AssetHandler.getInstance().getImage("chest1"); // Use chest1 as icon
    }

    // Decrease chest count (when a chest is opened)
    public void openChest() {
        if (remainingChests > 0) {
            remainingChests--;
        }
    }

    // Reset chest count to maximum
    public void resetChests() {
        remainingChests = maxChests;
    }

    // Draw chest icon with counter text next to it
    public void draw(Graphics2D g2) {
        // Draw chest icon
        if (chestIcon != null) {
            g2.drawImage(chestIcon, screenX, screenY, iconSize, iconSize, null);
        }

        // Draw counter text next to chest
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.setColor(Color.WHITE);
        String text = ": " + remainingChests;
        g2.drawString(text, screenX + iconSize + 5, screenY + 22); // Position text next to chest
    }

    // Get current number of remaining chests
    public int getRemainingChests() {
        return remainingChests;
    }

    // Set screen position where chest icon will be drawn
    public void setScreenPosition(int x, int y) {
        this.screenX = x;
        this.screenY = y;
    }
}
