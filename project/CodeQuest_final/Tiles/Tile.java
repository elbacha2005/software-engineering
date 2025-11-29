package CodeQuest.Tiles;

import java.awt.image.BufferedImage;

// Represents a single tile type in the game world (grass, water, wall, etc.)
public class Tile {
    public BufferedImage image; // Tile sprite image
    public boolean collision = false; // True if tile blocks movement (walls, water, etc.)
}
