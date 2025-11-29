package CodeQuest.Tiles;

import CodeQuest.Main.Drawable;
import java.awt.*;
import java.awt.image.BufferedImage;

// Represents interactive objects in the game world (trees, bushes, keys, chests, etc.)
public class MapObject implements Drawable {
    public BufferedImage image; // Single sprite for static objects (or closed chest)
    public BufferedImage imageOpen; // Opened chest sprite
    public BufferedImage[] sprites; // Multiple sprites for animated objects (keys)
    public int spriteNum = 0; // Current animation frame
    public long lastFrameTime = 0; // Last animation update time
    public long frameDelay = 100_000_000L; // Animation delay (100ms)
    public float hoverY = 0; // Vertical hover offset for floating objects (keys)
    public boolean collected = false; // True if object collected by player
    public boolean opened = false; // True if chest has been opened
    public Rectangle solidArea; // Collision hitbox
    public int solidAreaDefaultX = 0; // Default X offset for hitbox
    public int solidAreaDefaultY = 0; // Default Y offset for hitbox
    public boolean collision = false; // True if object blocks movement
    public int worldX, worldY; // Position in world coordinates
    public String name; // Object type identifier
    public int state = 0; // Object state (for interactive objects)

    // Constructor: sets default collision area
    public MapObject() {
        solidArea = new Rectangle(0, 0, 48, 48); // Default 48x48 hitbox
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    // Update animation and hover effects
    public void updateAnimation() {
        long now = System.nanoTime();
        if (sprites != null && now - lastFrameTime > frameDelay) {
            spriteNum = (spriteNum + 1) % sprites.length; // Cycle through animation frames
            lastFrameTime = now;
        }
        if (name.equals("key")) {
            hoverY = (float) Math.sin(now / 1_000_000_000.0 * 2) * 5; // Floating sine wave motion
        }
    }

    // Get Y position for draw order sorting
    @Override
    public int getSortY() {
        return worldY + solidArea.y + (int)hoverY; // Include hover offset
    }

    // Draw object with custom rendering for different object types
    @Override
    public void draw(Graphics2D g2, int screenX, int screenY) {
        if (name.equals("tree")) {
            // Draw large tree sprite (256x256) centered on tile
            if (image != null) {
                g2.drawImage(image, screenX - 64, screenY - 64, 256, 256, null);
            } else {
                g2.setColor(Color.BLUE); // Fallback color
                g2.fillRect(screenX - 64, screenY - 64, 256, 256);
            }
        } else if (name.equals("bush")) {
            if (image != null) {
                // Center the 47x42 bush in 64x64 tile
                int offsetX = (64 - 47) / 2;
                int offsetY = (64 - 42) / 2;
                g2.drawImage(image, screenX + offsetX, screenY + offsetY, 47, 42, null);
            } else {
                g2.setColor(Color.GREEN); // Fallback color
                g2.fillRect(screenX, screenY, 64, 64);
            }
        } else if (name.equals("key")) {
            // Draw animated key with hover effect, only if not collected
            if (!collected && sprites != null && sprites[spriteNum] != null) {
                g2.drawImage(sprites[spriteNum], screenX + 16, screenY + 16 + (int)hoverY, 32, 32, null);
            }
        } else if (name.startsWith("chest")) {
            // Draw chest - show opened version if opened, otherwise show closed
            BufferedImage chestImage = opened ? imageOpen : image;
            if (chestImage != null) {
                g2.drawImage(chestImage, screenX, screenY, 64, 64, null);
            } else {
                g2.setColor(Color.RED); // Fallback color
                g2.fillRect(screenX, screenY, 64, 64);
            }
        } else {
            // Default rendering for other objects
            if (image != null) {
                g2.drawImage(image, screenX, screenY, 64, 64, null);
            } else {
                g2.setColor(Color.RED); // Fallback color
                g2.fillRect(screenX, screenY, 64, 64);
            }
        }
    }
}