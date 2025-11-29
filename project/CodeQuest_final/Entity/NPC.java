package CodeQuest.Entity;

import CodeQuest.Main.Drawable;
import CodeQuest.Main.GamePanel;
import CodeQuest.Tiles.AssetHandler;

import java.awt.*;
import java.awt.image.BufferedImage;

// Non-Player Character class - represents NPCs that can move randomly and have dialogue
public class NPC extends entity implements Drawable {
    GamePanel gamePanel; // Reference to the game panel
    public String name; // NPC identifier
    public String dialogue; // Text dialogue for this NPC
    public boolean hasDialogue = false; // True if NPC has dialogue

    // NPC behavior
    public boolean stationary = true; // If true, NPC doesn't move
    public int actionCounter = 0; // Counts frames for AI decision making
    public int actionInterval = 120; // Change direction every 2 seconds at 60fps
    private BufferedImage[][] sprites; // 2D array: [direction][frame] for animations

    // Constructor: creates NPC with game panel reference and name
    public NPC(GamePanel gamePanel, String name) {
        this.gamePanel = gamePanel;
        this.name = name;
        setDefaultValues();
        getNPCImage();
    }

    // Set default NPC properties
    public void setDefaultValues() {
        speed = 2; // NPC movement speed
        direction = "down"; // Start facing down

        // Default collision area - lower half of sprite for realistic collision
        solidArea = new Rectangle(8, 32, gamePanel.gameTileSize / 2, gamePanel.gameTileSize / 2);
    }

    // Load all NPC sprite images from asset handler
    public void getNPCImage() {
        sprites = new BufferedImage[5][4]; // 5 directions, 4 frames each
        // Load up direction sprites (index 0)
        sprites[0][0] = AssetHandler.getInstance().getImage("NPC_up1");
        sprites[0][1] = AssetHandler.getInstance().getImage("NPC_up2");
        sprites[0][2] = AssetHandler.getInstance().getImage("NPC_up3");
        sprites[0][3] = AssetHandler.getInstance().getImage("NPC_up4");
        // Load down direction sprites (index 1)
        sprites[1][0] = AssetHandler.getInstance().getImage("NPC_down1");
        sprites[1][1] = AssetHandler.getInstance().getImage("NPC_down2");
        sprites[1][2] = AssetHandler.getInstance().getImage("NPC_down3");
        sprites[1][3] = AssetHandler.getInstance().getImage("NPC_down4");
        // Load left direction sprites (index 2)
        sprites[2][0] = AssetHandler.getInstance().getImage("NPC_left1");
        sprites[2][1] = AssetHandler.getInstance().getImage("NPC_left2");
        sprites[2][2] = AssetHandler.getInstance().getImage("NPC_left3");
        sprites[2][3] = AssetHandler.getInstance().getImage("NPC_left4");
        // Load right direction sprites (index 3)
        sprites[3][0] = AssetHandler.getInstance().getImage("NPC_right1");
        sprites[3][1] = AssetHandler.getInstance().getImage("NPC_right2");
        sprites[3][2] = AssetHandler.getInstance().getImage("NPC_right3");
        sprites[3][3] = AssetHandler.getInstance().getImage("NPC_right4");
        // Load idle sprites (index 4)
        sprites[4][0] = AssetHandler.getInstance().getImage("NPC_idle1");
        sprites[4][1] = AssetHandler.getInstance().getImage("NPC_idle2");
        sprites[4][2] = AssetHandler.getInstance().getImage("NPC_idle3");
        sprites[4][3] = AssetHandler.getInstance().getImage("NPC_idle4");
    }

    // Update NPC position and animation each frame
    public void update() {
        if (!stationary) {
            // Random movement AI for moving NPCs
            actionCounter++; // Increment frame counter
            if (actionCounter >= actionInterval) {
                // Pick random direction every actionInterval frames
                int rand = (int)(Math.random() * 100);
                if (rand < 25) direction = "up";
                else if (rand < 50) direction = "down";
                else if (rand < 75) direction = "left";
                else direction = "right";

                actionCounter = 0; // Reset counter
            }

            // Check for collisions with tiles and other NPCs
            collisionOn = false;
            gamePanel.collisionChecker.checkTile(this); // Check tile collision
            gamePanel.collisionChecker.checkNPCCollision(this); // Check NPC-to-NPC collision

            // Move if no collision detected
            if (!collisionOn) {
                switch (direction) {
                    case "up": worldY -= speed; break;
                    case "down": worldY += speed; break;
                    case "left": worldX -= speed; break;
                    case "right": worldX += speed; break;
                }
            }

            // Update animation frame
            long now = System.nanoTime();
            if (now - lastFrameTime > frameDelay) {
                spriteNum++; // Next frame
                if (spriteNum > 4) spriteNum = 1; // Loop back to frame 1
                lastFrameTime = now;
            }
        } else {
            // Stationary NPC - only play idle animation
            direction = "idle";
            long now = System.nanoTime();
            if (now - lastFrameTime > frameDelay) {
                spriteNum++; // Next frame
                if (spriteNum > 4) spriteNum = 1; // Loop back to frame 1
                lastFrameTime = now;
            }
        }
    }

    // Get Y position for draw order sorting (entities at bottom of screen drawn last)
    @Override
    public int getSortY() {
        return worldY + solidArea.y + solidArea.height; // Bottom of collision box
    }

    // Draw the NPC sprite on screen
    @Override
    public void draw(Graphics2D g2, int screenX, int screenY) {
        // Map direction string to sprite array index
        int dirIndex = switch (direction) {
            case "up" -> 0;
            case "down" -> 1;
            case "left" -> 2;
            case "right" -> 3;
            case "idle" -> 4;
            default -> 4;
        };
        BufferedImage image = sprites[dirIndex][spriteNum - 1]; // Get current frame
        if (image != null) {
            g2.drawImage(image, screenX, screenY, 60, 90, null); // Draw NPC sprite (60x90 pixels)
        } else {
            // Fallback if sprite is missing - draw red rectangle
            g2.setColor(Color.RED);
            g2.fillRect(screenX, screenY, 100, 100);
        }
    }
}
