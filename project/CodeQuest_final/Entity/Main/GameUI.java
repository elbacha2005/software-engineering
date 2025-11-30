package CodeQuest.Main;

import CodeQuest.Entity.ChestSystem;
import CodeQuest.Entity.HealthSystem;
import CodeQuest.Entity.KeySystem;
import CodeQuest.Tiles.AssetHandler;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GameUI implements Observer {

    private HealthSystem healthSystem;
    private KeySystem keySystem;
    private ChestSystem chestSystem;

    private BufferedImage heartFull;
    private BufferedImage heartEmpty;
    private BufferedImage keyFull;
    private BufferedImage chestIcon;

    public GameUI(HealthSystem healthSystem, KeySystem keySystem, ChestSystem chestSystem) {
        this.healthSystem = healthSystem;
        this.keySystem = keySystem;
        this.chestSystem = chestSystem;

        // Load images
        heartFull = AssetHandler.getInstance().getImage("heart");
        heartEmpty = AssetHandler.getInstance().getImage("broken_heart");
        keyFull = AssetHandler.getInstance().getImage("full_key");
        chestIcon = AssetHandler.getInstance().getImage("chest1");
    }

    public void draw(Graphics2D g2) {
        drawHealth(g2);
        drawKeys(g2);
        drawChests(g2);
    }

    private void drawHealth(Graphics2D g2) {
        int x = 20;
        int y = 20;
        int heartSize = 32;
        int heartSpacing = 8;

        for (int i = 0; i < healthSystem.getMaxHealth(); i++) {
            BufferedImage heartImage = (i < healthSystem.getCurrentHealth()) ? heartFull : heartEmpty;
            g2.drawImage(heartImage, x, y, heartSize, heartSize, null);
            x += heartSize + heartSpacing;
        }
    }

    private void drawKeys(Graphics2D g2) {
        int x = 20;
        int y = 60;
        int keySize = 32;

        if (keyFull != null) {
            g2.drawImage(keyFull, x, y, keySize, keySize, null);
        }

        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.setColor(Color.WHITE);
        String text = ": " + keySystem.getCurrentKeys();
        g2.drawString(text, x + keySize + 5, y + 22);
    }

    private void drawChests(Graphics2D g2) {
        int x = 20;
        int y = 100;
        int iconSize = 32;

        if (chestIcon != null) {
            g2.drawImage(chestIcon, x, y, iconSize, iconSize, null);
        }

        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.setColor(Color.WHITE);
        String text = ": " + chestSystem.getRemainingChests();
        g2.drawString(text, x + iconSize + 5, y + 22);
    }

    @Override
    public void update() {
        // This will be used to refresh UI elements if needed,
        // but for now, drawing is handled in the main game loop.
    }
}
