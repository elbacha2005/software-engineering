package CodeQuest.Tiles;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * GameObjectFactory - Factory class for creating map objects
 * Uses Factory Design Pattern to centralize object creation and configuration
 * Handles walls, trees, bushes, keys, chests, and decorative objects
 */
public class GameObjectFactory {

    /**
     * Creates a map object based on its type name
     * Configures collision areas, sprites, and properties for each object type
     *
     * @param name The type of object to create (tree, bush, key, chest1-4, etc.)
     * @param x X position in world coordinates (not used in current implementation)
     * @param y Y position in world coordinates (not used in current implementation)
     * @return Fully configured MapObject ready to be placed in the world
     */
    public static MapObject createObject(String name, int x, int y) {
        MapObject obj = new MapObject();
        obj.name = name;

        switch (name) {
            case "wall":
                // Standard wall - full tile collision
                obj.collision = true;
                obj.solidArea = new Rectangle(0, 0, 64, 64);
                obj.solidAreaDefaultX = obj.solidArea.x;
                obj.solidAreaDefaultY = obj.solidArea.y;
                obj.image = AssetHandler.getInstance().getImage("wall");
                break;

            case "wall2":
                // Top corner wall - extended height (96px) with vertical offset
                obj.collision = true;
                obj.solidArea = new Rectangle(0, -32, 64, 96);
                obj.solidAreaDefaultX = obj.solidArea.x;
                obj.solidAreaDefaultY = obj.solidArea.y;
                obj.image = AssetHandler.getInstance().getImage("wall_top_corner");
                break;

            case "wall3":
                // Bottom corner wall - extended height (96px) with vertical offset
                obj.collision = true;
                obj.solidArea = new Rectangle(0, -32, 64, 96);
                obj.solidAreaDefaultX = obj.solidArea.x;
                obj.solidAreaDefaultY = obj.solidArea.y;
                obj.image = AssetHandler.getInstance().getImage("wall_bottom_corner");
                break;

            case "wall4":
                // Side wall - thin vertical collision area (10px wide)
                obj.collision = true;
                obj.solidArea = new Rectangle(28, 0, 10, 64);
                obj.solidAreaDefaultX = obj.solidArea.x;
                obj.solidAreaDefaultY = obj.solidArea.y;
                obj.image = AssetHandler.getInstance().getImage("wall_side");
                break;

            case "tree":
                // Tree - random variant selection, collision on trunk area only
                obj.collision = true;
                obj.solidArea = new Rectangle(50, 133, 32, 50); // Bottom center of tree sprite
                obj.solidAreaDefaultX = obj.solidArea.x;
                obj.solidAreaDefaultY = obj.solidArea.y;
                String treeKey = (Math.random() < 0.5) ? "tree" : "tree1"; // 50% chance each variant
                obj.image = AssetHandler.getInstance().getImage(treeKey);
                break;

            case "bush":
                // Bush - decorative, no collision, random variant
                obj.collision = false;
                obj.solidArea = new Rectangle(8, 11, 47, 42); // Matches bush sprite size
                obj.solidAreaDefaultX = obj.solidArea.x;
                obj.solidAreaDefaultY = obj.solidArea.y;
                String bushKey = (Math.random() < 0.5) ? "bush1" : "bush2"; // 50% chance each variant
                obj.image = AssetHandler.getInstance().getImage(bushKey);
                break;

            case "key":
                // Animated key - 4 frame animation, no collision (collected on overlap)
                obj.collision = false;
                obj.sprites = new BufferedImage[4];
                for (int i = 0; i < 4; i++) {
                    obj.sprites[i] = AssetHandler.getInstance().getImage("key" + (i + 1));
                }
                obj.solidArea = new Rectangle(16, 16, 32, 32); // Center of tile
                obj.solidAreaDefaultX = obj.solidArea.x;
                obj.solidAreaDefaultY = obj.solidArea.y;
                break;

            case "chest1":
                // Chest variant 1 - blocks movement, has open/closed states
                obj.collision = true;
                obj.solidAreaDefaultX = obj.solidArea.x;
                obj.solidAreaDefaultY = obj.solidArea.y;
                obj.image = AssetHandler.getInstance().getImage("chest1");
                obj.imageOpen = AssetHandler.getInstance().getImage("chest1_open");
                break;

            case "chest2":
                // Chest variant 2
                obj.collision = true;
                obj.solidAreaDefaultX = obj.solidArea.x;
                obj.solidAreaDefaultY = obj.solidArea.y;
                obj.image = AssetHandler.getInstance().getImage("chest2");
                obj.imageOpen = AssetHandler.getInstance().getImage("chest2_open");
                break;

            case "chest3":
                // Chest variant 3
                obj.collision = true;
                obj.solidAreaDefaultX = obj.solidArea.x;
                obj.solidAreaDefaultY = obj.solidArea.y;
                obj.image = AssetHandler.getInstance().getImage("chest3");
                obj.imageOpen = AssetHandler.getInstance().getImage("chest3_open");
                break;

            case "chest4":
                // Chest variant 4
                obj.collision = true;
                obj.solidAreaDefaultX = obj.solidArea.x;
                obj.solidAreaDefaultY = obj.solidArea.y;
                obj.image = AssetHandler.getInstance().getImage("chest4");
                obj.imageOpen = AssetHandler.getInstance().getImage("chest4_open");
                break;

            case "skeleton":
                // Decorative skeleton - blocks movement
                obj.collision = true;
                obj.solidArea = new Rectangle(0, 0, 64, 64);
                obj.solidAreaDefaultX = obj.solidArea.x;
                obj.solidAreaDefaultY = obj.solidArea.y;
                obj.image = AssetHandler.getInstance().getImage("skeleton");
                break;

            default:
                // Default object with no special properties (no collision, no sprite)
                break;
        }

        return obj;
    }
}
