package CodeQuest.Entity;

import CodeQuest.Main.Drawable;
import CodeQuest.Main.GamePanel;
import CodeQuest.Main.KeyHandler;
import CodeQuest.Entity.NPC;
import CodeQuest.Tiles.AssetHandler;

import java.awt.*;
import java.awt.image.BufferedImage;

// Player character class - controlled by keyboard and text commands
public class Player extends entity implements Drawable {
    GamePanel gamePanel; // Reference to game panel
    KeyHandler keyH; // Keyboard input handler
    public final int screenX; // Player's fixed X position on screen (camera follows player)
    public final int screenY; // Player's fixed Y position on screen
    private BufferedImage[][] sprites; // 2D array: [direction][frame] for animations

    // Command-based movement properties (for text command system)
    public String commandDirection; // Direction from text command
    public long commandMoveEndTime; // When command movement should stop
    public int commandMoveDuration; // How long commands last (ms)
    public int commandSpeed; // Speed when moving via commands
    public long commandFrameDelay; // Animation speed for command movement

    public int keys = 0; // Number of keys collected by player

    // Constructor: initializes player with game panel and keyboard handler
    public Player(GamePanel gamePanel, KeyHandler keyH) {
        this.gamePanel = gamePanel;
        this.keyH = keyH;
        sprites = new BufferedImage[5][4]; // 5 directions, 4 frames each
        setDefault(); // Set starting position and load sprites
        screenX = (int) (gamePanel.gameTileSize * 7.5); // Center player horizontally on screen
        screenY = (int) (gamePanel.gameTileSize * 5); // Center player vertically on screen
        solidArea = new Rectangle(); // Create collision hitbox
        solidArea.x = 8; // Hitbox offset from sprite edge
        solidArea.y = 16;
        solidArea.width = gamePanel.gameTileSize / 2; // Hitbox smaller than sprite for realistic collision
        solidArea.height = gamePanel.gameTileSize / 2;

        // Initialize command movement system
        commandDirection = null; // No command active at start
        commandMoveEndTime = 0;
        commandMoveDuration = 500; // Commands last 500ms
        commandSpeed = 2; // Slower speed for command movement
        commandFrameDelay = 100_000_000; // Faster animation for commands (100ms)

    }

    // Reset player to default starting state
    public void setDefault() {
        worldX = 1536; // Starting world position (24*64)
        worldY = 1600; // (25*64)
        speed = 4; // Keyboard movement speed
        direction = "down"; // Start facing down
        getPlayerImage(); // Load sprites
    }

    // Load all player sprite images from asset handler
    public void getPlayerImage() {
        // Load up direction sprites (index 0)
        sprites[0][0] = AssetHandler.getInstance().getImage("player_up1");
        sprites[0][1] = AssetHandler.getInstance().getImage("player_up2");
        sprites[0][2] = AssetHandler.getInstance().getImage("player_up3");
        sprites[0][3] = AssetHandler.getInstance().getImage("player_up4");
        // Load down direction sprites (index 1)
        sprites[1][0] = AssetHandler.getInstance().getImage("player_down1");
        sprites[1][1] = AssetHandler.getInstance().getImage("player_down2");
        sprites[1][2] = AssetHandler.getInstance().getImage("player_down3");
        sprites[1][3] = AssetHandler.getInstance().getImage("player_down4");
        // Load left direction sprites (index 2)
        sprites[2][0] = AssetHandler.getInstance().getImage("player_left1");
        sprites[2][1] = AssetHandler.getInstance().getImage("player_left2");
        sprites[2][2] = AssetHandler.getInstance().getImage("player_left3");
        sprites[2][3] = AssetHandler.getInstance().getImage("player_left4");
        // Load right direction sprites (index 3)
        sprites[3][0] = AssetHandler.getInstance().getImage("player_right1");
        sprites[3][1] = AssetHandler.getInstance().getImage("player_right2");
        sprites[3][2] = AssetHandler.getInstance().getImage("player_right3");
        sprites[3][3] = AssetHandler.getInstance().getImage("player_right4");
        // Load idle sprites (index 4)
        sprites[4][0] = AssetHandler.getInstance().getImage("player_idle1");
        sprites[4][1] = AssetHandler.getInstance().getImage("player_idle2");
        sprites[4][2] = AssetHandler.getInstance().getImage("player_idle3");
        sprites[4][3] = AssetHandler.getInstance().getImage("player_idle4");
    }

    public void update() {
        // Handle command movement
        if (commandDirection != null && System.currentTimeMillis() < commandMoveEndTime) {
            direction = commandDirection;
            // Predict future position by 5 pixels
            int predictX = worldX;
            int predictY = worldY;
            switch (direction) {
                case "up": predictY -= 5; break;
                case "down": predictY += 5; break;
                case "left": predictX -= 5; break;
                case "right": predictX += 5; break;
            }

            // Check if future position would collide
            if (!gamePanel.collisionChecker.checkAllCollisions(this, predictX, predictY)) {
                // Move if clear
                switch (direction) {
                    case "up": worldY -= commandSpeed; break;
                    case "down": worldY += commandSpeed; break;
                    case "left": worldX -= commandSpeed; break;
                    case "right": worldX += commandSpeed; break;
                }
            } else {
                // Stop command on predicted collision
                commandDirection = null;
                commandMoveEndTime = 0;
            }
            // Update animation with faster delay
            long now = System.nanoTime();
            if (now - lastFrameTime > commandFrameDelay) {
                spriteNum++;
                if (spriteNum > 4) spriteNum = 1;
                lastFrameTime = now;
            }
        } else if (commandDirection != null) {
            // Time expired, stop command movement
            commandDirection = null;
        }

        // No keyboard control - only commands or idle
        boolean commandsExecuting = (gamePanel.commandParser != null && gamePanel.commandParser.isExecuting());

        if (!commandsExecuting) {
            // Only set to idle if not moving and commands aren't executing
            direction = "idle";
            long now = System.nanoTime();
            if (now - lastFrameTime > frameDelay) {
                spriteNum++;
                if (spriteNum > 4) {
                    spriteNum = 1;
                }
                lastFrameTime = now;
            }
        } else {
            // Commands are executing - just update animation
            long now = System.nanoTime();
            if (now - lastFrameTime > frameDelay) {
                spriteNum++;
                if (spriteNum > 4) {
                    spriteNum = 1;
                }
                lastFrameTime = now;
            }
        }

    }

    // Get Y position for draw order sorting (entities at bottom drawn last)
    @Override
    public int getSortY() {
        return worldY + solidArea.y + solidArea.height; // Bottom of collision box
    }

    // Draw player sprite on screen
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
            g2.drawImage(image, screenX, screenY, gamePanel.gameTileSize, gamePanel.gameTileSize, null); // Draw player sprite
        }
    }
}
