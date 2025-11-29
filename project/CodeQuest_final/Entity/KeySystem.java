package CodeQuest.Entity;

import CodeQuest.Tiles.AssetHandler;
import java.awt.*;
import java.awt.image.BufferedImage;

// Manages key collection display with counter
public class KeySystem {

    private int maxKeys = 5; // Maximum keys to collect
    private int currentKeys = 0; // Current keys collected

    private BufferedImage keyFull; // Full key sprite

    private int keySize = 32; // Key display size in pixels
    private int screenX = 20; // X position on screen to draw key
    private int screenY = 60; // Y position on screen (below hearts)

    // Constructor: loads key image from asset handler
    public KeySystem() {
        keyFull = AssetHandler.getInstance().getImage("full_key");
    }

    // Add a key to the collection
    public void addKey() {
        if (currentKeys < maxKeys) {
            currentKeys++;
        }
    }

    // Reset keys to 0
    public void resetKeys() {
        currentKeys = 0;
    }

    // Draw key icon with counter text next to it
    public void draw(Graphics2D g2) {
        // Draw key icon
        if (keyFull != null) {
            g2.drawImage(keyFull, screenX, screenY, keySize, keySize, null);
        }

        // Draw counter text next to key
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.setColor(Color.WHITE);
        String text = ": " + currentKeys;
        g2.drawString(text, screenX + keySize + 5, screenY + 22); // Position text next to key
    }

    // Get current number of keys collected
    public int getCurrentKeys() {
        return currentKeys;
    }

    // Set screen position where key will be drawn
    public void setScreenPosition(int x, int y) {
        this.screenX = x;
        this.screenY = y;
    }
}
