package CodeQuest.Main;

import CodeQuest.Entity.NPC;
import CodeQuest.Entity.entity;
import CodeQuest.Tiles.MapObject;

import java.awt.Rectangle;

// Handles collision detection for entities with tiles, objects, and other entities
public class CollisionChecker {
    GamePanel gamePanel; // Reference to game panel

    // Constructor
    public CollisionChecker(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    // Calculate where entity will be after moving in a direction
    private int[] getFuturePos(entity entity, String direction) {
        int futureX = entity.worldX;
        int futureY = entity.worldY;
        switch (direction) {
            case "up": futureY -= entity.speed; break;
            case "down": futureY += entity.speed; break;
            case "left": futureX -= entity.speed; break;
            case "right": futureX += entity.speed; break;
        }
        return new int[]{futureX, futureY}; // Return [x, y]
    }

    // Check if entity will collide with tiles or objects
    public void checkTile(entity entity) {
        switch (entity.direction) {
            case "up":
                if (checkObjectCollision(entity, "up") || checkTileCollision(entity, "up")) {
                    entity.collisionOn = true; // Mark collision
                }
                break;
            case "down":
                if (checkObjectCollision(entity, "down") || checkTileCollision(entity, "down")) {
                    entity.collisionOn = true;
                }
                break;
            case "left":
                if (checkObjectCollision(entity, "left") || checkTileCollision(entity, "left")) {
                    entity.collisionOn = true;
                }
                break;
            case "right":
                if (checkObjectCollision(entity, "right") || checkTileCollision(entity, "right")) {
                    entity.collisionOn = true;
                }
                break;
        }
    }

    // Check if entity will collide with any map objects
    private boolean checkObjectCollision(entity entity, String direction) {
        int[] future = getFuturePos(entity, direction); // Get future position

        // Create rectangle for future hitbox position
        Rectangle futureSolid = new Rectangle(
            future[0] + entity.solidArea.x,
            future[1] + entity.solidArea.y,
            entity.solidArea.width,
            entity.solidArea.height
        );

        // Check collision with all objects
        for (MapObject obj : gamePanel.objM.objects) {
            if (obj.collision) { // Only check solid objects
                // Temporarily set object solid area to world position
                obj.solidArea.x = obj.worldX + obj.solidAreaDefaultX;
                obj.solidArea.y = obj.worldY + obj.solidAreaDefaultY;
                boolean intersects = futureSolid.intersects(obj.solidArea);
                // Reset solid area to relative position
                obj.solidArea.x = obj.solidAreaDefaultX;
                obj.solidArea.y = obj.solidAreaDefaultY;

                if (intersects) {
                    System.out.println("Collision with " + obj.name + " at " + obj.worldX + "," + obj.worldY);
                    return true; // Collision detected
                }
            }
        }
        return false; // No collision
    }

    // Check if entity will collide with solid tiles
    private boolean checkTileCollision(entity entity, String direction) {
        int[] future = getFuturePos(entity, direction); // Get future position

        int tileCol = future[0] / gamePanel.gameTileSize; // Convert pixels to tile coordinates
        int tileRow = future[1] / gamePanel.gameTileSize;

        // Check map bounds - prevent going outside map
        if (tileCol < 0 || tileCol >= gamePanel.maxWorldCol || tileRow < 0 || tileRow >= gamePanel.maxWorldRow) {
            return true; // Out of bounds = collision
        }

        int tileNum = gamePanel.tileM.mapTile[tileCol][tileRow]; // Get tile type at position
        if (gamePanel.tileM.tiles[tileNum] != null && gamePanel.tileM.tiles[tileNum].collision) {
            return true; // Tile is solid
        }
        return false; // Tile allows movement
    }

    // Comprehensive collision check for NPCs (checks player, other NPCs, objects, and tiles)
    public void checkNPCCollision(entity entity) {
        int[] future = getFuturePos(entity, entity.direction); // Get future position

        // Create rectangle for future hitbox position
        Rectangle futureSolid = new Rectangle(
            future[0] + entity.solidArea.x,
            future[1] + entity.solidArea.y,
            entity.solidArea.width,
            entity.solidArea.height
        );

        // Check collision with player
        Rectangle playerRect = new Rectangle(
            gamePanel.player.worldX + gamePanel.player.solidArea.x,
            gamePanel.player.worldY + gamePanel.player.solidArea.y,
            gamePanel.player.solidArea.width,
            gamePanel.player.solidArea.height
        );

        if (futureSolid.intersects(playerRect)) {
            entity.collisionOn = true; // Colliding with player
            return;
        }

        // Check collision with other NPCs
        for (NPC npc : gamePanel.npcM.npcs) {
            if (npc != entity) { // Don't check collision with self
                Rectangle npcRect = new Rectangle(
                    npc.worldX + npc.solidArea.x,
                    npc.worldY + npc.solidArea.y,
                    npc.solidArea.width,
                    npc.solidArea.height
                );

                if (futureSolid.intersects(npcRect)) {
                    entity.collisionOn = true; // Colliding with another NPC
                    return;
                }
            }
        }

        // Check collision with objects
        for (MapObject obj : gamePanel.objM.objects) {
            if (obj.collision) { // Only check solid objects
                // Temporarily set object solid area to world position
                obj.solidArea.x = obj.worldX + obj.solidAreaDefaultX;
                obj.solidArea.y = obj.worldY + obj.solidAreaDefaultY;
                boolean intersects = futureSolid.intersects(obj.solidArea);
                // Reset solid area to relative position
                obj.solidArea.x = obj.solidAreaDefaultX;
                obj.solidArea.y = obj.solidAreaDefaultY;

                if (intersects) {
                    entity.collisionOn = true; // Colliding with object
                    return;
                }
            }
        }

        // Check tile collision
        int tileCol = future[0] / gamePanel.gameTileSize; // Convert to tile coordinates
        int tileRow = future[1] / gamePanel.gameTileSize;
        if (tileCol < 0 || tileCol >= gamePanel.maxWorldCol || tileRow < 0 || tileRow >= gamePanel.maxWorldRow) {
            entity.collisionOn = true; // Out of bounds
        } else {
            int tileNum = gamePanel.tileM.mapTile[tileCol][tileRow]; // Get tile type
            if (gamePanel.tileM.tiles[tileNum] != null && gamePanel.tileM.tiles[tileNum].collision) {
                entity.collisionOn = true; // Solid tile
            }
        }
    }
}
