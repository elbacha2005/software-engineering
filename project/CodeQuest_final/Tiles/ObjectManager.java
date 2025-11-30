package CodeQuest.Tiles;

import CodeQuest.Main.GamePanel;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

// Manages all map objects like trees, bushes, chests, and keys
public class ObjectManager {
    GamePanel gamePanel; // Reference to game panel
    public List<MapObject> objects = new ArrayList<>(); // List of all objects in world

    // Constructor initializes object manager
    public ObjectManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        loadObjects("/CodeQuest/res/Maps/Objects.txt"); // Load objects from file
    }

    // Loads objects from text file
    public void loadObjects(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            if (is == null) return; // no objects file
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            // Read each object line
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" "); // Format: name x y
                if (parts.length >= 3) {
                    String name = parts[0];
                    if (name.startsWith("npc")) {continue;} // Skip NPCs, handled by NPCManager
                    int x = Integer.parseInt(parts[1]); // X position in tiles
                    int y = Integer.parseInt(parts[2]); // Y position in tiles
                    MapObject obj = createObject(name, 0, 0); // Create object based on type (dummy coords)
                    obj.worldX = x * gamePanel.gameTileSize; // Convert to pixels
                    obj.worldY = y * gamePanel.gameTileSize;
                    // Special handling for beach border tiles
                    if (name.equals("beach")) {
                        String tileKey;
                        // Determine beach tile variant based on position
                        if (x == 0 && y == 0) tileKey = "beach_top_left";
                        else if (x == gamePanel.maxWorldCol - 1 && y == 0) tileKey = "beach_top_right";
                        else if (x == 0 && y == gamePanel.maxWorldRow - 1) tileKey = "beach_bottom_left";
                        else if (x == gamePanel.maxWorldCol - 1 && y == gamePanel.maxWorldRow - 1) tileKey = "beach_bottom_right";
                        else if (y == 0) tileKey = "beach_up";
                        else if (y == gamePanel.maxWorldRow - 1) tileKey = "beach_down";
                        else if (x == 0) tileKey = "beach_left";
                        else if (x == gamePanel.maxWorldCol - 1) tileKey = "beach_right";
                        else tileKey = "beach_down";  // Fallback
                        obj.image = AssetHandler.getInstance().getImage(tileKey);
                        obj.collision = true; // Beach blocks movement
                         // Set collision area for beach tiles
                         switch (tileKey) {
                             case "beach_up" :
                             case "beach_down" :
                             case "beach_left" :
                             case "beach_right" :
                             case "beach_top_left" :
                             case "beach_top_right" :
                             case "beach_bottom_left" :
                             case "beach_bottom_right" :
                                 obj.solidArea = new Rectangle(0, 0, 64, 64); // Full tile collision
                                 break;
                         }
                        obj.solidAreaDefaultX = obj.solidArea.x; // Store default position
                        obj.solidAreaDefaultY = obj.solidArea.y;
                    }
                    objects.add(obj); // Add to objects list
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Creates object using the factory
    private MapObject createObject(String name, int x, int y) {
        return GameObjectFactory.createObject(name, x, y);
    }

    // Updates animated objects like keys
    public void update() {
        for (MapObject obj : objects) {
            if (obj.sprites != null) { // Has animation frames
                obj.updateAnimation(); // Update animation
            }
        }
    }
}