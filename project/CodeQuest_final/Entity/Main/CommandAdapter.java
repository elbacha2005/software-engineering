package CodeQuest.Main;

import CodeQuest.Entity.Player;

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

        // Calculate distance
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= moveSpeed) {
            // Close enough - snap to target
            player.worldX = targetX;
            player.worldY = targetY;
            isMoving = false;
            // Keep direction for next command
        } else {
            // Move toward target
            double ratio = moveSpeed / distance;
            int stepX = (int)(dx * ratio);
            int stepY = (int)(dy * ratio);

            // Predict future position by 5 pixels ahead of step
            int predictX = player.worldX + stepX + (stepX > 0 ? 5 : stepX < 0 ? -5 : 0);
            int predictY = player.worldY + stepY + (stepY > 0 ? 5 : stepY < 0 ? -5 : 0);

            // Check if predicted position would collide
            if (gamePanel.collisionChecker.checkAllCollisions(player, predictX, predictY)) {
                isMoving = false;
                return;
            }

            // Move if clear
            player.worldX += stepX;
            player.worldY += stepY;

            // Update sprite animation with timing
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
    // Clears action queue and stops movement
    private void startSmoothMove(int newTargetX, int newTargetY, String direction) {
        targetX = newTargetX;
        targetY = newTargetY;
        player.direction = direction;
        isMoving = true;
    }

    public boolean isExecuting() {
        return isExecuting || !actionQueue.isEmpty() || isMoving;
    }

    public int getQueueSize() {
        return actionQueue.size();
    }

    public void setActionDelay(int delayMs) {
        this.actionDelay = delayMs;
    }

    public void clearQueue() {
        actionQueue.clear();
        isExecuting = false;
        isMoving = false;
        targetX = player.worldX;
        targetY = player.worldY;
    }

    // ========== Movement Execution Methods ==========

    public void executeMoveUp() {
        actionQueue.add(() -> {
            int newY = player.worldY - 64;
            player.direction = "up";
            startSmoothMove(player.worldX, newY, "up");
        });
    }

    public void executeMoveDown() {
        actionQueue.add(() -> {
            int newY = player.worldY + 64;
            player.direction = "down";
            startSmoothMove(player.worldX, newY, "down");
        });
    }

    public void executeMoveLeft() {
        actionQueue.add(() -> {
            int newX = player.worldX - 64;
            player.direction = "left";
            startSmoothMove(newX, player.worldY, "left");
        });
    }

    public void executeMoveRight() {
        actionQueue.add(() -> {
            int newX = player.worldX + 64;
            player.direction = "right";
            startSmoothMove(newX, player.worldY, "right");
        });
    }

    public void executePrint(String message) {
        actionQueue.add(() -> {
            gamePanel.messageSystem.showPrintMessage(message);
        });
    }
}
