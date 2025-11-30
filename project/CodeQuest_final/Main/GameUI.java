package CodeQuest.Main;

import CodeQuest.Entity.ChestSystem;
import CodeQuest.Entity.HealthSystem;
import CodeQuest.Entity.KeySystem;
import CodeQuest.Tiles.AssetHandler;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * GameUI - Displays the HUD (Heads-Up Display) for the game
 * Shows health hearts, key count, and chest count in the top-left corner
 * Implements Observer pattern to react to changes in game systems
 */
public class GameUI implements Observer {

    // Game systems being observed
    private HealthSystem healthSystem;
    private KeySystem keySystem;
    private ChestSystem chestSystem;

    // UI sprites
    private BufferedImage heartFull;   // Full heart sprite
    private BufferedImage heartEmpty;  // Empty/broken heart sprite
    private BufferedImage keyFull;     // Key icon sprite
    private BufferedImage chestIcon;   // Chest icon sprite

    /**
     * Constructor: initializes UI and loads sprites
     * @param healthSystem The health management system
     * @param keySystem The key collection system
     * @param chestSystem The chest tracking system
     */
    public GameUI(HealthSystem healthSystem, KeySystem keySystem, ChestSystem chestSystem) {
        this.healthSystem = healthSystem;
        this.keySystem = keySystem;
        this.chestSystem = chestSystem;

        // Load UI sprites from asset handler
        heartFull = AssetHandler.getInstance().getImage("heart");
        heartEmpty = AssetHandler.getInstance().getImage("broken_heart");
        keyFull = AssetHandler.getInstance().getImage("full_key");
        chestIcon = AssetHandler.getInstance().getImage("chest1");
    }

    /**
     * Main draw method - renders all UI elements
     * @param g2 Graphics context for rendering
     */
    public void draw(Graphics2D g2) {
        drawHealth(g2);
        drawKeys(g2);
        drawChests(g2);
    }

    /**
     * Draws health hearts in the top-left corner
     * Shows full hearts for remaining health, empty hearts for lost health
     * @param g2 Graphics context for rendering
     */
    private void drawHealth(Graphics2D g2) {
        int x = 20;
        int y = 20;
        int heartSize = 32;
        int heartSpacing = 8;

        // Draw each heart (full or empty based on current health)
        for (int i = 0; i < healthSystem.getMaxHealth(); i++) {
            BufferedImage heartImage = (i < healthSystem.getCurrentHealth()) ? heartFull : heartEmpty;
            g2.drawImage(heartImage, x, y, heartSize, heartSize, null);
            x += heartSize + heartSpacing;
        }
    }

    /**
     * Draws key count display below health hearts
     * Shows key icon followed by number of keys collected
     * @param g2 Graphics context for rendering
     */
    private void drawKeys(Graphics2D g2) {
        int x = 20;
        int y = 60;
        int keySize = 32;

        // Draw key icon
        if (keyFull != null) {
            g2.drawImage(keyFull, x, y, keySize, keySize, null);
        }

        // Draw key count text
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.setColor(Color.WHITE);
        String text = ": " + keySystem.getCurrentKeys();
        g2.drawString(text, x + keySize + 5, y + 22);
    }

    /**
     * Draws remaining chest count below key display
     * Shows chest icon followed by number of chests remaining to open
     * @param g2 Graphics context for rendering
     */
    private void drawChests(Graphics2D g2) {
        int x = 20;
        int y = 100;
        int iconSize = 32;

        // Draw chest icon
        if (chestIcon != null) {
            g2.drawImage(chestIcon, x, y, iconSize, iconSize, null);
        }

        // Draw chest count text
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.setColor(Color.WHITE);
        String text = ": " + chestSystem.getRemainingChests();
        g2.drawString(text, x + iconSize + 5, y + 22);
    }

    /**
     * Observer pattern update method
     * Called when observed systems (health, keys, chests) change state
     * Currently unused as drawing happens in main game loop
     */
    @Override
    public void update() {
        // This will be used to refresh UI elements if needed,
        // but for now, drawing is handled in the main game loop.
    }
}
