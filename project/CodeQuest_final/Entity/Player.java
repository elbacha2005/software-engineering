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
        worldX = 1000; // Starting world position
        worldY = 1000;
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

    // Check if player will collide when moving in direction at speed
    private boolean checkCollision(String dir, int spd) {
        collisionOn = false;
        gamePanel.collisionChecker.checkTile(this); // Check tile collision
        if (collisionOn) return true; // Blocked by tile

        // Calculate future position rectangle
        Rectangle futureRect = new Rectangle(worldX + solidArea.x, worldY + solidArea.y, solidArea.width, solidArea.height);
        switch (dir) {
            case "up": futureRect.y -= spd; break;
            case "down": futureRect.y += spd; break;
            case "left": futureRect.x -= spd; break;
            case "right": futureRect.x += spd; break;
        }
        // Check collision with all NPCs
        for (NPC npc : gamePanel.npcM.npcs) {
            Rectangle npcRect = new Rectangle(npc.worldX + npc.solidArea.x, npc.worldY + npc.solidArea.y, npc.solidArea.width, npc.solidArea.height);
            if (futureRect.intersects(npcRect)) {
                return true; // Collision with NPC
            }
        }
        return false; // No collision
    }

    // Move player in direction at speed
    private void move(String dir, int spd) {
        switch (dir) {
            case "up": worldY -= spd; break;
            case "down": worldY += spd; break;
            case "left": worldX -= spd; break;
            case "right": worldX += spd; break;
        }
    }

    // Update animation frame based on delay
    private void updateAnimation(long delay) {
        long now = System.nanoTime();
        if (now - lastFrameTime > delay) { // Time for next frame?
            spriteNum++; // Next frame
            if (spriteNum > 4) spriteNum = 1; // Loop back to frame 1
            lastFrameTime = now;
        }
    }

    // Update player each frame - handles both command and keyboard movement
    public void update() {
        // Priority 1: Execute command-based movement if active
        if (commandDirection != null && System.currentTimeMillis() < commandMoveEndTime) {
            direction = commandDirection; // Set direction from command
            if (!checkCollision(direction, commandSpeed)) { // Check if path is clear
                move(direction, commandSpeed); // Move at command speed
                updateAnimation(commandFrameDelay); // Animate at command speed
            } else {
                // Hit obstacle, cancel command
                commandDirection = null;
                commandMoveEndTime = 0;
            }
        } else if (commandDirection != null) {
            // Command time expired
            commandDirection = null;
        }

        // Check if command system is currently executing commands
        boolean commandsExecuting = (gamePanel.commandParser != null && gamePanel.commandParser.isExecuting());

        // Priority 2: Keyboard input (only if commands are executing)
        if (commandsExecuting && (keyH.UpPressed || keyH.DownPressed || keyH.LeftPressed || keyH.RightPressed)) {
            if (keyH.UpPressed) direction = "up";
            if (keyH.DownPressed) direction = "down";
            if (keyH.LeftPressed) direction = "left";
            if (keyH.RightPressed) direction = "right";

            if (!checkCollision(direction, speed)) { // Check if path is clear
                move(direction, speed); // Move at keyboard speed
            }
            updateAnimation(frameDelay); // Animate
        } else if (!commandsExecuting) {
            // No commands executing - show idle animation
            direction = "idle";
            updateAnimation(frameDelay);
        } else {
            // Commands executing but no keyboard input - still animate
            updateAnimation(frameDelay);
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
