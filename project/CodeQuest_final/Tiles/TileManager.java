package CodeQuest.Tiles;

import CodeQuest.Main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

// Manages tile rendering and map loading for the game world
public class TileManager {
    public Tile[] tiles; // Array of available tile types
    GamePanel gamePanel; // Reference to game panel
    public int[][] mapTile; // 2D array storing tile IDs for each position

    // Constructor initializes tile manager
    public TileManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        tiles = new Tile[10]; // grass variants
        getTileImage(); // Load tile images
        mapTile = new int[gamePanel.maxWorldCol][gamePanel.maxWorldRow]; // Initialize map array
        loadMap("/CodeQuest/res/Maps/WorldMap2.txt"); // Load map from file
    }

    // Loads map data from text file
    public void loadMap(String name) {
        try {
            InputStream input = getClass().getResourceAsStream(name);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            int col =0;
            int row =0;
            // Read map row by row
            while (col < gamePanel.maxWorldCol && row < gamePanel.maxWorldRow) {
                String line = reader.readLine();
                // Parse each column in the row
                while (col < gamePanel.maxWorldCol) {
                    String[] Nums = line.split(" "); // Split by spaces
                    int num =  Integer.parseInt(Nums[col]); // Convert to tile ID
                    mapTile[col][row] = num; // Store in map array
                    col++;
                }
                // Move to next row
                if (col == gamePanel.maxWorldCol) {
                    row++;
                    col = 0;
                }
            }
            reader.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // Loads tile images from asset handler
    public void getTileImage() {
        // Terrain tile 1
        tiles[0] = new Tile();
        tiles[0].image = AssetHandler.getInstance().getImage("terrain1");

        // Terrain tile 2
        tiles[1] = new Tile();
        tiles[1].image = AssetHandler.getInstance().getImage("terrain2");

        // Terrain tile 3
        tiles[2] = new Tile();
        tiles[2].image = AssetHandler.getInstance().getImage("terrain3");

        // Water tile with collision
        tiles[3] = new Tile();
        tiles[3].image = AssetHandler.getInstance().getImage("water");
        tiles[3].collision = true; // Water blocks movement
    }

    // Renders visible tiles to the screen
    public void draw(Graphics2D g2) {
        int worldCol = 0;
        int worldRow = 0;

        // Loop through all tiles in the world
        while (worldCol < gamePanel.maxWorldCol && worldRow < gamePanel.maxWorldRow) {

            // Calculate world coordinates
            int worldX = gamePanel.gameTileSize * worldCol;
            int worldY = gamePanel.gameTileSize * worldRow;
            // Convert to screen coordinates based on player position
            int screenX = worldX - gamePanel.player.worldX + gamePanel.player.screenX;
            int screenY = worldY - gamePanel.player.worldY + gamePanel.player.screenY;

            // Only draw tiles visible on screen (optimization)
            if (worldX + gamePanel.gameTileSize > gamePanel.player.worldX - gamePanel.player.screenX &&
                    worldY + gamePanel.gameTileSize > gamePanel.player.worldY - gamePanel.player.screenY &&
                    worldX - gamePanel.gameTileSize < gamePanel.player.worldX + gamePanel.player.screenX &&
                    worldY - 4*gamePanel.gameTileSize < gamePanel.player.worldY + gamePanel.player.screenY) {
                 int tileNum = mapTile[worldCol][worldRow]; // Get tile ID
                 if ( 0 <= tileNum && tileNum <= 3 ) { // Validate tile ID
                     BufferedImage img = tiles[tileNum].image;
                     if (img != null) {
                         // Draw tile image
                         g2.drawImage(img, screenX, screenY, gamePanel.gameTileSize, gamePanel.gameTileSize, null);
                     } else {
                         // Fallback if image missing
                         g2.setColor(Color.GREEN);
                         g2.fillRect(screenX, screenY, gamePanel.gameTileSize, gamePanel.gameTileSize);
                     }
                 }
            }

            worldCol++; // Move to next column

            // Move to next row when column complete
            if (worldCol == gamePanel.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }
    }
}
