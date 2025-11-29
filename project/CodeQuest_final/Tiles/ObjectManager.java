package CodeQuest.Tiles;

import CodeQuest.Main.GamePanel;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
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
                    if (name.startsWith("npc")) continue; // Skip NPCs, handled by NPCManager
                    int x = Integer.parseInt(parts[1]); // X position in tiles
                    int y = Integer.parseInt(parts[2]); // Y position in tiles
                    MapObject obj = createObject(name); // Create object based on type
                    obj.name = name;
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

    // Creates object with properties based on type
    private MapObject createObject(String name) {
        MapObject obj = new MapObject();
        switch (name) {
            case "wall" :
                obj.collision = true;
                obj.solidArea = new Rectangle(0, 0, 64, 64);  // Full tile collision

                obj.solidAreaDefaultX = obj.solidArea.x;
                obj.solidAreaDefaultY = obj.solidArea.y;
                // Image assigned later based on position
                break;
            case "tree" :
                obj.collision = true; // Trees block movement
                obj.solidArea = new Rectangle(50, 133, 32, 50);  // Trunk collision area

                obj.solidAreaDefaultX = obj.solidArea.x;
                obj.solidAreaDefaultY = obj.solidArea.y;
                // Randomly choose tree variant
                String treeKey = (Math.random() < 0.5) ? "tree" : "tree1";
                obj.image = AssetHandler.getInstance().getImage(treeKey);
                break;
            case "bush" :
                obj.collision = false; // Bushes don't block movement
                obj.solidArea = new Rectangle(8, 11, 47, 42);  // Bush collision area

                obj.solidAreaDefaultX = obj.solidArea.x;
                obj.solidAreaDefaultY = obj.solidArea.y;
                // Randomly choose bush variant
                String bushKey = (Math.random() < 0.5) ? "bush1" : "bush2";
                obj.image = AssetHandler.getInstance().getImage(bushKey);
                break;
            case "key" :
                obj.collision = false; // Keys can be walked over
                obj.sprites = new BufferedImage[4]; // Animation frames
                // Load key animation frames
                for (int i = 0; i < 4; i++) {
                    obj.sprites[i] = AssetHandler.getInstance().getImage("key" + (i + 1));
                }
                obj.solidArea = new Rectangle(16, 16, 32, 32); // Center collision
                obj.solidAreaDefaultX = obj.solidArea.x;
                obj.solidAreaDefaultY = obj.solidArea.y;
                break;
            case "chest1":
                obj.collision = true; // Chests block movement
                obj.solidAreaDefaultX = obj.solidArea.x;
                obj.solidAreaDefaultY = obj.solidArea.y;
                obj.image = AssetHandler.getInstance().getImage("chest1");
                obj.imageOpen = AssetHandler.getInstance().getImage("chest1_open");
                break;
            case "chest2":
                obj.collision = true;
                obj.solidAreaDefaultX = obj.solidArea.x;
                obj.solidAreaDefaultY = obj.solidArea.y;
                obj.image = AssetHandler.getInstance().getImage("chest2");
                obj.imageOpen = AssetHandler.getInstance().getImage("chest2_open");
                break;
            case "chest3":
                obj.collision = true;
                obj.solidAreaDefaultX = obj.solidArea.x;
                obj.solidAreaDefaultY = obj.solidArea.y;
                obj.image = AssetHandler.getInstance().getImage("chest3");
                obj.imageOpen = AssetHandler.getInstance().getImage("chest3_open");
                break;
            case "chest4":
                obj.collision = true;
                obj.solidAreaDefaultX = obj.solidArea.x;
                obj.solidAreaDefaultY = obj.solidArea.y;
                obj.image = AssetHandler.getInstance().getImage("chest4");
                obj.imageOpen = AssetHandler.getInstance().getImage("chest4_open");
                break;
        }

        return obj;
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