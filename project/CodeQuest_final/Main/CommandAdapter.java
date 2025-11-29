package CodeQuest.Main;

import CodeQuest.Entity.Player;
import CodeQuest.Entity.NPC;
import java.awt.Rectangle;
import java.util.*;

// Executes queued commands with smooth movement
public class CommandAdapter {
    private GamePanel gamePanel; // Reference to game panel
    private Player player; // Reference to player
    private Queue<Runnable> actionQueue; // Queue of actions to execute
    private boolean isExecuting; // Whether commands are being executed
    private int actionDelay = 0; // Delay between actions in ms
    private long lastActionTime = 0; // Time of last action
    private long lastFrameTime = 0; // Time of last animation frame
    private long frameDelay = 100_000_000; // Delay between animation frames
    private boolean isMoving; // Whether player is currently moving
    private int targetX, targetY; // Target position for smooth movement
    private int moveSpeed = 3; // Movement speed in pixels per frame
    
    // Constructor initializes adapter
    public CommandAdapter(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.player = gamePanel.player;
        this.actionQueue = new LinkedList<>();
        this.isExecuting = false;
        this.isMoving = false;
        this.targetX = player.worldX;
        this.targetY = player.worldY;
    }

    // Updates command execution and smooth movement
    public void update() {
        // Handle smooth movement
        if (isMoving) {
            smoothMove();
            return;
        }

        // Check if queue is empty
        if (actionQueue.isEmpty()) {
            isExecuting = false;
            return;
        }

        // Check if delay has elapsed
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastActionTime < actionDelay) {
            return;
        }

        // Execute next action
        isExecuting = true;
        Runnable action = actionQueue.poll();
        if (action != null) {
            action.run();
            lastActionTime = currentTime;
        }
    }
    
    // Handles smooth movement towards target position
    private void smoothMove() {
        int dx = targetX - player.worldX;
        int dy = targetY - player.worldY;

        double distance = Math.sqrt(dx * dx + dy * dy);

        // Snap to target if close enough
        if (distance <= moveSpeed) {
            player.worldX = targetX;
            player.worldY = targetY;
            isMoving = false;
        } else {
            // Move towards target
            double ratio = moveSpeed / distance;
            int stepX = (int)(dx * ratio);
            int stepY = (int)(dy * ratio);

            int oldX = player.worldX;
            int oldY = player.worldY;
            player.worldX += stepX;
            player.worldY += stepY;

            // Check collisions
            player.collisionOn = false;
            gamePanel.collisionChecker.checkTile(player);

            // Check NPC collisions
            Rectangle playerRect = new Rectangle(
                player.worldX + player.solidArea.x,
                player.worldY + player.solidArea.y,
                player.solidArea.width,
                player.solidArea.height
            );

            for (NPC npc : gamePanel.npcM.npcs) {
                Rectangle npcRect = new Rectangle(
                    npc.worldX + npc.solidArea.x,
                    npc.worldY + npc.solidArea.y,
                    npc.solidArea.width,
                    npc.solidArea.height
                );
                if (playerRect.intersects(npcRect)) {
                    player.collisionOn = true;
                    break;
                }
            }

            // Revert if collision detected
            if (player.collisionOn) {
                player.worldX = oldX;
                player.worldY = oldY;
                isMoving = false;
                return;
            }

            // Update animation frame
            long now = System.nanoTime();
            if (now - lastFrameTime > frameDelay) {
                player.spriteNum++;
                if (player.spriteNum > 4) {
                    player.spriteNum = 1;
                }
                lastFrameTime = now;
            }
        }
    }

    // Starts smooth movement to target position
    private void startSmoothMove(int newTargetX, int newTargetY, String direction) {
        targetX = newTargetX;
        targetY = newTargetY;
        player.direction = direction;
        isMoving = true;
    }
    
    // Returns whether commands are executing
    public boolean isExecuting() {
        return isExecuting || !actionQueue.isEmpty() || isMoving;
    }

    // Returns number of queued actions
    public int getQueueSize() {
        return actionQueue.size();
    }

    // Sets delay between actions
    public void setActionDelay(int delayMs) {
        this.actionDelay = delayMs;
    }

    // Sets movement speed
    public void setMoveSpeed(int speed) {
        this.moveSpeed = speed;
    }

    // Clears action queue and stops movement
    public void clearQueue() {
        actionQueue.clear();
        isExecuting = false;
        isMoving = false;
        targetX = player.worldX;
        targetY = player.worldY;
    }

    // Queues move up action
    public void executeMoveUp() {
        actionQueue.add(() -> {
            int newY = player.worldY - 64; // One tile up
            player.direction = "up";
            player.collisionOn = false;

            // Check collision at target position
            int oldY = player.worldY;
            player.worldY = newY;
            gamePanel.collisionChecker.checkTile(player);
            player.worldY = oldY;

            if (!player.collisionOn) {
                startSmoothMove(player.worldX, newY, "up");
            }
        });
    }

    // Queues move down action
    public void executeMoveDown() {
        actionQueue.add(() -> {
            int newY = player.worldY + 64; // One tile down
            player.direction = "down";
            player.collisionOn = false;

            // Check collision at target position
            int oldY = player.worldY;
            player.worldY = newY;
            gamePanel.collisionChecker.checkTile(player);
            player.worldY = oldY;

            if (!player.collisionOn) {
                startSmoothMove(player.worldX, newY, "down");
            }
        });
    }

    // Queues move left action
    public void executeMoveLeft() {
        actionQueue.add(() -> {
            int newX = player.worldX - 64; // One tile left
            player.direction = "left";
            player.collisionOn = false;

            // Check collision at target position
            int oldX = player.worldX;
            player.worldX = newX;
            gamePanel.collisionChecker.checkTile(player);
            player.worldX = oldX;

            if (!player.collisionOn) {
                startSmoothMove(newX, player.worldY, "left");
            }
        });
    }

    // Queues move right action
    public void executeMoveRight() {
        actionQueue.add(() -> {
            int newX = player.worldX + 64; // One tile right
            player.direction = "right";
            player.collisionOn = false;

            // Check collision at target position
            int oldX = player.worldX;
            player.worldX = newX;
            gamePanel.collisionChecker.checkTile(player);
            player.worldX = oldX;

            if (!player.collisionOn) {
                startSmoothMove(newX, player.worldY, "right");
            }
        });
    }

    // Queues turn action
    public void executeTurn(String direction) {
        actionQueue.add(() -> {
            player.direction = direction; // Change direction without moving
        });
    }

    // Queues wait action
    public void executeWait(int milliseconds) {
        actionQueue.add(() -> {
            lastActionTime = System.currentTimeMillis() + milliseconds - actionDelay;
        });
    }

    // Queues print action
    public void executePrint(String message) {
        actionQueue.add(() -> {
            System.out.println(message); // Print to console
        });
    }
}
