package CodeQuest.Entity;

import java.awt.*;
import java.awt.image.BufferedImage;

// Base class for all game entities (Player, NPCs)
public abstract class entity {
    public int worldX,worldY; // Entity position in the game world
    public int speed; // Movement speed in pixels
    public BufferedImage up1, up2, up3, up4, down1, down2, down3, down4, left1,
            left2, left3, left4, right1, right2, right3, right4, idle1, idle2, idle3, idle4; // Sprite images for animation (4 frames per direction)
    public String direction; // Current facing direction
    public int spriteNum = 1; // Current sprite frame number (1-4)
    public long lastFrameTime = 0; // Last time sprite animation updated
    public long frameDelay = 200_000_000; // Delay between animation frames (200ms)
    public Rectangle solidArea; // Collision hitbox
    public boolean collisionOn = false; // True if entity is colliding

}
