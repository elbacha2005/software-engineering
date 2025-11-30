package CodeQuest.Tiles;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

// Singleton class that loads and manages all game images (Singleton pattern)
public class AssetHandler {
    private static AssetHandler instance; // Single instance
    private java.util.HashMap<String, BufferedImage> assets; // Map of name -> image

    // Private constructor prevents external instantiation
    private AssetHandler() {
        assets = new java.util.HashMap<>();
        loadAssets(); // Load all assets on creation
    }

    // Get the single instance (creates it if doesn't exist)
    public static AssetHandler getInstance() {
        if (instance == null) {
            instance = new AssetHandler();
        }
        return instance;
    }

    // Load all game assets (sprites, tiles, UI images)
    private void loadAssets() {
        loadImage("player_up1", "/CodeQuest/res/player/run_up/0.png");
        loadImage("player_down1", "/CodeQuest/res/player/run_down/0.png");
        loadImage("player_left1", "/CodeQuest/res/player/run_left/0.png");
        loadImage("player_right1", "/CodeQuest/res/player/run_right/0.png");
        loadImage("player_up2", "/CodeQuest/res/player/run_up/2.png");
        loadImage("player_down2", "/CodeQuest/res/player/run_down/2.png");
        loadImage("player_left2", "/CodeQuest/res/player/run_left/2.png");
        loadImage("player_right2", "/CodeQuest/res/player/run_right/2.png");
        loadImage("player_up3", "/CodeQuest/res/player/run_up/4.png");
        loadImage("player_down3", "/CodeQuest/res/player/run_down/4.png");
        loadImage("player_left3", "/CodeQuest/res/player/run_left/4.png");
        loadImage("player_right3", "/CodeQuest/res/player/run_right/4.png");
        loadImage("player_up4", "/CodeQuest/res/player/run_up/6.png");
        loadImage("player_down4", "/CodeQuest/res/player/run_down/6.png");
        loadImage("player_left4", "/CodeQuest/res/player/run_left/6.png");
        loadImage("player_right4", "/CodeQuest/res/player/run_right/6.png");
        loadImage("player_idle1", "/CodeQuest/res/player/idle_down/0.png");
        loadImage("player_idle2", "/CodeQuest/res/player/idle_down/2.png");
        loadImage("player_idle3", "/CodeQuest/res/player/idle_down/4.png");
        loadImage("player_idle4", "/CodeQuest/res/player/idle_down/6.png");

        loadImage("terrain", "/CodeQuest/res/tiles/terrain.png");

        loadImage("beach_up", "/CodeQuest/res/tiles/Beach/beach_up.png");
        loadImage("beach_down", "/CodeQuest/res/tiles/Beach/beach_down.png");
        loadImage("beach_left", "/CodeQuest/res/tiles/Beach/beach_left.png");
        loadImage("beach_right", "/CodeQuest/res/tiles/Beach/beach_right.png");
        loadImage("beach_top_left", "/CodeQuest/res/tiles/Beach/beach_top_left.png");
        loadImage("beach_top_right", "/CodeQuest/res/tiles/Beach/beach_top_right.png");
        loadImage("beach_bottom_left", "/CodeQuest/res/tiles/Beach/beach_bottom_left.png");
        loadImage("beach_bottom_right", "/CodeQuest/res/tiles/Beach/beach_bottom_right.png");
        loadImage("tree", "/CodeQuest/res/tiles/Nature/tree2.png");
        loadImage("terrain1", "/CodeQuest/res/tiles/Nature/terrain1.png");
        loadImage("tree1", "/CodeQuest/res/tiles/Nature/tree1.png");
        loadImage("terrain2", "/CodeQuest/res/tiles/Nature/terrain2.png");
        loadImage("terrain3", "/CodeQuest/res/tiles/Nature/terrain3.png");
        loadImage("bush1", "/CodeQuest/res/tiles/Nature/bush1.png");
        loadImage("bush2", "/CodeQuest/res/tiles/Nature/bush2.png");
        loadImage("path", "/CodeQuest/res/tiles/Nature/Path.png");
        loadImage("ground", "/CodeQuest/res/tiles/Nature/stone_tile.png");

        loadImage("NPC_idle1", "/CodeQuest/res/tiles/NPC/NPC1/NPC_idle1.png");
        loadImage("NPC_idle2", "/CodeQuest/res/tiles/NPC/NPC1/NPC_Idle2.png");
        loadImage("NPC_idle3", "/CodeQuest/res/tiles/NPC/NPC1/NPC_Idle3.png");
        loadImage("NPC_idle4", "/CodeQuest/res/tiles/NPC/NPC1/NPC_Idle4.png");

        loadImage("background", "/CodeQuest/res/tiles/Icons/MainMenue.png");

        loadImage("heart","/CodeQuest/res/tiles/Icons/full_heart.png");
        loadImage("broken_heart", "/CodeQuest/res/tiles/Icons/empty_heart.png");

        loadImage("full_key", "/CodeQuest/res/tiles/Icons/full_key.png");

        loadImage("game_over", "/CodeQuest/res/tiles/Icons/Game_over.png");
        loadImage("win", "/CodeQuest/res/tiles/Icons/win.png");

        loadImage("key1", "/CodeQuest/res/tiles/Props/Key1.png");
        loadImage("key2", "/CodeQuest/res/tiles/Props/key2.png");
        loadImage("key3", "/CodeQuest/res/tiles/Props/Key3.png");
        loadImage("key4", "/CodeQuest/res/tiles/Props/Key4.png");

        loadImage("chest1",  "/CodeQuest/res/tiles/Props/chest1.png");
        loadImage("chest2",  "/CodeQuest/res/tiles/Props/chest2.png");
        loadImage("chest3",  "/CodeQuest/res/tiles/Props/chest3.png");
        loadImage("chest4",  "/CodeQuest/res/tiles/Props/chest4.png");

        loadImage("chest1_open",  "/CodeQuest/res/tiles/Props/chest1_open.png");
        loadImage("chest2_open",  "/CodeQuest/res/tiles/Props/chest2_open.png");
        loadImage("chest3_open",  "/CodeQuest/res/tiles/Props/chest3_open.png");
        loadImage("chest4_open",  "/CodeQuest/res/tiles/Props/chest4_open.png");

        loadImage("wall", "/CodeQuest/res/tiles/Props/Wall_Tiles.png");
        loadImage("wall_top_corner","/CodeQuest/res/tiles/Props/top_corner.png");
        loadImage("wall_bottom_corner","/CodeQuest/res/tiles/Props/bottom_corner.png");
        loadImage("wall_side","/CodeQuest/res/tiles/Props/Wall_Tiles_side.png");
        loadImage("skeleton", "/CodeQuest/res/tiles/Props/Deco_skeleto_sitdown.png");

    }

    // Load a single image from resources and store in assets map
    private void loadImage(String key, String path) {
        try {
            InputStream input = getClass().getResourceAsStream(path);
            if (input != null) {
                BufferedImage img = ImageIO.read(input); // Load image from file
                assets.put(key, img); // Store in map
            } else {
                assets.put(key, null); // Placeholder if file not found
            }
         } catch (IOException e) {
             assets.put(key, null); // Placeholder on error
         }
    }

    // Get an image by its key name
    public BufferedImage getImage(String key) {
        return assets.get(key); // Return null if key doesn't exist
    }
}