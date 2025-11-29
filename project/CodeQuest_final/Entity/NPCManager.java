package CodeQuest.Entity;

import CodeQuest.Main.GamePanel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

// Manages all NPCs in the game - loading from file and updating them
public class NPCManager {
    GamePanel gamePanel; // Reference to game panel
    public List<NPC> npcs = new ArrayList<>(); // List of all NPCs in the game

    // Constructor: creates manager and loads NPCs from file
    public NPCManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        loadNPCs("/CodeQuest/res/Maps/NPCs.txt"); // Load NPC data from text file
    }

    // Load NPCs from text file - each line defines one NPC
    public void loadNPCs(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            if (is == null) return; // File not found, exit quietly

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;

            while ((line = br.readLine()) != null) {
                // Format: npcName x y stationary dialogue
                // Example: villager1 5 10 true Hello traveler!
                String[] parts = line.split(" ", 5); // Split max 5 parts (dialogue can have spaces)
                if (parts.length >= 4) { // Need at least name, x, y, stationary
                    String name = parts[0]; // NPC identifier
                    int x = Integer.parseInt(parts[1]); // Tile X position
                    int y = Integer.parseInt(parts[2]); // Tile Y position
                    boolean stationary = Boolean.parseBoolean(parts[3]); // Can NPC move?
                    String dialogue = parts.length > 4 ? parts[4] : ""; // Optional dialogue text

                    NPC npc = new NPC(gamePanel, name); // Create new NPC
                    npc.worldX = x * gamePanel.gameTileSize; // Convert tile to pixel coordinates
                    npc.worldY = y * gamePanel.gameTileSize;
                    npc.stationary = stationary; // Set movement behavior
                    npc.dialogue = dialogue; // Set dialogue
                    npc.hasDialogue = !dialogue.isEmpty(); // Flag if dialogue exists

                    npcs.add(npc); // Add to NPC list
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace(); // Print error if file reading fails
        }
    }

    // Update all NPCs each frame
    public void update() {
        for (NPC npc : npcs) {
            npc.update(); // Call update on each NPC (handles movement and animation)
        }
    }
}