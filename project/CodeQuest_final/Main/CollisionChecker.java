package CodeQuest.Main;

import CodeQuest.Entity.NPC;
import CodeQuest.Entity.entity;
import CodeQuest.Tiles.MapObject;

import java.awt.Rectangle;

// Handles collision detection for entities with tiles, objects, and other entities
public class CollisionChecker {
    GamePanel gamePanel;

    public CollisionChecker(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public boolean checkAllCollisions(entity entity, int futureX, int futureY) {
        Rectangle futureSolid = new Rectangle(
                futureX + entity.solidArea.x,
                futureY + entity.solidArea.y,
                entity.solidArea.width,
                entity.solidArea.height
        );

        // Check collision with objects
        for (MapObject obj : gamePanel.objM.objects) {
            if (obj.collision) {
                obj.solidArea.x = obj.worldX + obj.solidAreaDefaultX;
                obj.solidArea.y = obj.worldY + obj.solidAreaDefaultY;
                if (futureSolid.intersects(obj.solidArea)) {
                    Rectangle intersection = futureSolid.intersection(obj.solidArea);
                    if (intersection.width > 5 && intersection.height > 5) {
                        obj.solidArea.x = obj.solidAreaDefaultX;
                        obj.solidArea.y = obj.solidAreaDefaultY;
                        return true;
                    }
                }
                obj.solidArea.x = obj.solidAreaDefaultX;
                obj.solidArea.y = obj.solidAreaDefaultY;
            }
        }

        // Check collision with player (if entity is not player)
        if (entity != gamePanel.player) {
            Rectangle playerRect = new Rectangle(
                    gamePanel.player.worldX + gamePanel.player.solidArea.x,
                    gamePanel.player.worldY + gamePanel.player.solidArea.y,
                    gamePanel.player.solidArea.width,
                    gamePanel.player.solidArea.height
            );
            if (futureSolid.intersects(playerRect)) {
                Rectangle intersection = futureSolid.intersection(playerRect);
                if (intersection.width > 5 && intersection.height > 5) {
                    return true;
                }
            }
        }

        // Check collision with other NPCs
        for (NPC npc : gamePanel.npcM.npcs) {
            if (npc != entity) {
                Rectangle npcRect = new Rectangle(
                        npc.worldX + npc.solidArea.x,
                        npc.worldY + npc.solidArea.y,
                        npc.solidArea.width,
                        npc.solidArea.height
                );
                if (futureSolid.intersects(npcRect)) {
                    Rectangle intersection = futureSolid.intersection(npcRect);
                    if (intersection.width > 5 && intersection.height > 5) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
