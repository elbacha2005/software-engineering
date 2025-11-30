package CodeQuest.Entity;

import CodeQuest.Main.Observer;
import CodeQuest.Main.Subject;
import CodeQuest.Tiles.AssetHandler;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

// Manages player health, displays hearts, and shows game over screen
public class HealthSystem implements Subject {

    private List<Observer> observers = new ArrayList<>();

    private int maxHealth = 5; // Maximum health (5 hearts)
    private int currentHealth = 5; // Current health remaining

    private BufferedImage heartFull; // Full heart sprite
    private BufferedImage heartEmpty; // Empty/broken heart sprite

    private int heartSize = 32; // Heart display size in pixels
    private int heartSpacing = 8; // Space between hearts
    private int screenX = 20; // X position on screen to draw hearts
    private int screenY = 20; // Y position on screen to draw hearts

    private boolean isDead = false; // True when health reaches 0

    // Constructor: loads heart images from asset handler
    public HealthSystem() {
        heartFull = AssetHandler.getInstance().getImage("heart");
        heartEmpty = AssetHandler.getInstance().getImage("broken_heart");
    }

    // Reduce health by damage amount, returns false if already dead
    public boolean takeDamage(int amount) {
        if (isDead) {return false;} // Can't damage a dead player
        currentHealth -= amount; // Reduce current health
        if (currentHealth <= 0) {
            currentHealth = 0; // Clamp to 0
            isDead = true; // Mark as dead
        }
        notifyObservers();
        return true; // Damage applied successfully
    }

    // Restore health to maximum and revive player
    public void resetHealth() {
        currentHealth = maxHealth;
        isDead = false;
        notifyObservers();
    }



    // Helper method to draw a styled button (menu style) with centered text
    private void drawStyledButton(Graphics2D g2, int x, int y, int w, int h, String text, boolean selected) {
        // Draw selection arrow if selected
        if (selected) {
            g2.setColor(new Color(255, 255, 100));
            g2.fillPolygon(
                new int[]{x - 30, x - 50, x - 30},
                new int[]{y + h/2 - 10, y + h/2, y + h/2 + 10},
                3
            );
        }

        // Draw rounded rectangle background (brighter if selected)
        if (selected) {
            g2.setColor(new Color(255, 255, 100, 80));
        } else {
            g2.setColor(new Color(255, 255, 100, 50));
        }
        g2.fillRoundRect(x, y, w, h, 10, 10);

        // Draw button border
        g2.setColor(new Color(255, 255, 100));
        g2.drawRoundRect(x, y, w, h, 10, 10);

        // Draw text shadow
        g2.setFont(new Font("Arial", Font.BOLD, 30));
        g2.setColor(new Color(0, 0, 0, 200));
        FontMetrics fm = g2.getFontMetrics();
        int tx = x + (w - fm.stringWidth(text)) / 2; // Center text horizontally
        int ty = y + (h + fm.getAscent()) / 2 - 5; // Center text vertically
        g2.drawString(text, tx + 2, ty + 2);

        // Draw text
        g2.setColor(new Color(255, 255, 100));
        g2.drawString(text, tx, ty);
    }

    // Helper method to draw a button with centered text (old style for game over)
    private void drawButton(Graphics2D g2, int x, int y, int w, int h, String text) {
        g2.setColor(Color.GRAY);
        g2.fillRect(x, y, w, h); // Button background
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, w, h); // Button border
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        int tx = x + (w - fm.stringWidth(text)) / 2; // Center text horizontally
        int ty = y + (h + fm.getAscent()) / 2; // Center text vertically
        g2.drawString(text, tx, ty);
    }

    // Draw the game over screen with restart and menu buttons
    public void drawGameOver(Graphics2D g2, int screenWidth, int screenHeight, int selectedOption) {
        BufferedImage gameOverImg = AssetHandler.getInstance().getImage("game_over");
        if (gameOverImg != null) {
            g2.drawImage(gameOverImg, 0, 0, screenWidth, screenHeight, null); // Full screen game over image
        }

        // Button dimensions and positions (same as win screen)
        int bw = 200, bh = 60; // Slightly larger buttons
        int rx = screenWidth / 2 - bw / 2;
        int ry = screenHeight / 2 + 100;

        // Draw Restart button with menu style (highlighted if selected)
        drawStyledButton(g2, rx, ry, bw, bh, "Restart", selectedOption == 0);

        // Draw Main Menu button below Restart (highlighted if selected)
        int mx = screenWidth / 2 - bw / 2;
        int my = ry + bh + 30;
        drawStyledButton(g2, mx, my, bw, bh, "Main Menu", selectedOption == 1);

        // Draw navigation instructions
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        String instructions = "↑↓ to navigate  |  ENTER to select";
        FontMetrics fm = g2.getFontMetrics();
        int instX = (screenWidth - fm.stringWidth(instructions)) / 2;

        g2.setColor(new Color(0, 0, 0, 200));
        g2.drawString(instructions, instX + 1, screenHeight - 29);

        g2.setColor(new Color(150, 150, 150));
        g2.drawString(instructions, instX, screenHeight - 30);
    }

    // Draw the victory screen with win image and styled buttons
    public void drawWinScreen(Graphics2D g2, int screenWidth, int screenHeight, int selectedOption) {
        // Draw win image (same structure as drawGameOver)
        BufferedImage winImg = AssetHandler.getInstance().getImage("win");
        if (winImg != null) {
            g2.drawImage(winImg, 0, 0, screenWidth, screenHeight, null); // Full screen win image
        }

        // Button dimensions and positions
        int bw = 200, bh = 60; // Slightly larger buttons
        int rx = screenWidth / 2 - bw / 2;
        int ry = screenHeight / 2 + 100;

        // Draw Restart button with menu style (highlighted if selected)
        drawStyledButton(g2, rx, ry, bw, bh, "Restart", selectedOption == 0);

        // Draw Main Menu button below Restart (highlighted if selected)
        int mx = screenWidth / 2 - bw / 2;
        int my = ry + bh + 30;
        drawStyledButton(g2, mx, my, bw, bh, "Main Menu", selectedOption == 1);

        // Draw navigation instructions
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        String instructions = "↑↓ to navigate  |  ENTER to select";
        FontMetrics fm = g2.getFontMetrics();
        int instX = (screenWidth - fm.stringWidth(instructions)) / 2;

        g2.setColor(new Color(0, 0, 0, 200));
        g2.drawString(instructions, instX + 1, screenHeight - 29);

        g2.setColor(new Color(150, 150, 150));
        g2.drawString(instructions, instX, screenHeight - 30);
    }

    // Get current health value
    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    // Check if player is dead
    public boolean isDead() {
        return isDead;
    }

    // Set screen position where hearts will be drawn
    public void setScreenPosition(int x, int y) {
        this.screenX = x;
        this.screenY = y;
    }

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void unregisterObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
}
