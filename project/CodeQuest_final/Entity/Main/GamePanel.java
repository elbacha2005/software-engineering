package CodeQuest.Main;

import CodeQuest.Entity.ChestSystem;
import CodeQuest.Entity.HealthSystem;
import CodeQuest.Entity.KeySystem;
import CodeQuest.Entity.MessageSystem;
import CodeQuest.Entity.NPC;
import CodeQuest.Entity.NPCManager;
import CodeQuest.Entity.Player;
import CodeQuest.Tiles.MapObject;
import CodeQuest.Tiles.ObjectManager;
import CodeQuest.Tiles.TileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Main game panel that handles game loop and rendering
public class GamePanel extends JPanel implements Runnable {

    // Screen settings
    final int gameTiles = 32; // Base tile size
    final int scale = 2; // Scale factor
    public final int gameTileSize = gameTiles * scale; // Scaled tile size (64px)
    public final int maxScreenCol = 16; // Screen columns
    public final int maxScreenRow = 10; // Screen rows
    public final int screenWidth = gameTileSize * maxScreenCol; // Screen width in pixels
    public final int screenHeight = gameTileSize * maxScreenRow; // Screen height in pixels
    int fps = 60; // Target frames per second
    
    // Game components
    public KeyHandler keyH = new KeyHandler(this); // Keyboard input handler
    Thread gameThread; // Game loop thread
    public Player player = new Player(this, keyH); // Player entity

    TileManager tileM = new TileManager(this); // Tile rendering manager
    public ObjectManager objM = new ObjectManager(this); // Map objects manager
    public CollisionChecker collisionChecker = new CollisionChecker(this); // Collision detection
    public NPCManager npcM = new NPCManager(this); // NPC manager

    public HealthSystem healthSystem = new HealthSystem(); // Player health system
    public KeySystem keySystem = new KeySystem(); // Key collection system
    public ChestSystem chestSystem = new ChestSystem(); // Chest counter system
    public MessageSystem messageSystem; // NPC message display system
    public GameUI gameUI; // UI observer for health, keys, and chests

    // World settings
    public final int maxWorldCol = 50; // World width in tiles
    public final int maxWorldRow = 50; // World height in tiles

    public CommandParser commandParser; // Command parser for Python commands
    public SoundManager soundManager; // Sound manager for music and effects

    // Win screen menu selection
    public int selectedWinOption = 0; // 0 = Restart, 1 = Main Menu

    // Game over screen menu selection
    public int selectedGameOverOption = 0; // 0 = Restart, 1 = Main Menu

    // Game states
    public int gameState = 1; // Current game state
    public final int titleState = 0; // Title screen
    public final int menuState = 1; // Main menu
    public final int playState = 2; // Playing game
    public final int pauseState = 3; // Paused
    public final int optionsState = 4; // Options menu
    public final int gameOverState = 5; // Game over
    public final int winState = 6; // Victory screen

    // Constructor initializes game panel
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(new Color(0x156c99));
        this.setLayout(null);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        // Handle game over screen clicks
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameState == gameOverState) {
                    int mx = e.getX();
                    int my = e.getY();
                    int buttonWidth = 150;
                    int buttonHeight = 50;
                    int restartX = screenWidth / 2 - buttonWidth / 2;
                    int restartY = screenHeight / 2 + 100;
                    // Check restart button click
                    if (mx >= restartX && mx <= restartX + buttonWidth && my >= restartY && my <= restartY + buttonHeight) {
                        restartGame();
                    }
                    // Check main menu button click
                    int menuX = screenWidth / 2 - buttonWidth / 2;
                    int menuY = restartY + buttonHeight + 20;
                    if (mx >= menuX && mx <= menuX + buttonWidth && my >= menuY && my <= menuY + buttonHeight) {
                        gameState = menuState;
                    }
                }
            }
        });
        player.setDefault();

        // Initialize command parser
        commandParser = new CommandParser(this);
        commandParser.setCommandDelay(300);

        // Initialize sound manager
        soundManager = new SoundManager();

        // Set health display position
        healthSystem.setScreenPosition(20, 20);

        // Set key display position (below hearts)
        keySystem.setScreenPosition(20, 60);

        // Set chest display position (below keys)
        chestSystem.setScreenPosition(20, 100);

        // Initialize message system
        messageSystem = new MessageSystem(screenWidth, screenHeight);

        // Initialize GameUI and register it with the systems
        gameUI = new GameUI(healthSystem, keySystem, chestSystem);
        healthSystem.registerObserver(gameUI);
        keySystem.registerObserver(gameUI);
        chestSystem.registerObserver(gameUI);

        // Start at title screen
        this.gameState = titleState;
        this.setVisible(false);
    }

    // Starts the game loop thread
    void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    // Main game loop
    @Override
    public void run() {
        double drawInterval = (double) 1000000000 / fps;
        double deltaTime = 0;
        long lastTime = System.nanoTime();
        long now;
        long timer = 0;
        int frames = 0;

        // Game loop
        while (gameThread != null) {
            now = System.nanoTime();
            deltaTime += (now - lastTime) / drawInterval;
            timer += now - lastTime;
            lastTime = now;

            // Update and render at target FPS
            if (deltaTime >= 1) {
                try {
                    update();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                repaint();
                deltaTime--;
                frames++;
            }

            if (timer >= 1000000000) {
                frames = 0;
                timer = 0;
            }
        }
    }

    // Updates game state
    public void update() throws InterruptedException {
        if (gameState == playState) {
            player.update(); // Update player
            npcM.update(); // Update NPCs
            objM.update(); // Update objects

            // Check for key collection
            Rectangle playerRect = new Rectangle(player.worldX + player.solidArea.x, player.worldY + player.solidArea.y, player.solidArea.width, player.solidArea.height);
            for (MapObject obj : objM.objects) {
                if (obj.name.equals("key") && !obj.collected) {
                    Rectangle objRect = new Rectangle(obj.worldX + obj.solidArea.x, obj.worldY + obj.solidArea.y, obj.solidArea.width, obj.solidArea.height);
                    if (playerRect.intersects(objRect)) {
                        obj.collected = true; // Collect key
                        player.keys++; // Increment player key count
                        keySystem.addKey(); // Add key to visual display
                    }
                }
                // Check for chest opening (check proximity, not intersection, since chests block movement)
                if (obj.name.startsWith("chest") && !obj.opened && keySystem.getCurrentKeys() > 0) {
                    // Calculate distance between player center and chest center
                    int playerCenterX = player.worldX + player.solidArea.x + player.solidArea.width / 2;
                    int playerCenterY = player.worldY + player.solidArea.y + player.solidArea.height / 2;
                    int chestCenterX = obj.worldX + gameTileSize / 2;
                    int chestCenterY = obj.worldY + gameTileSize / 2;

                    int distance = (int) Math.sqrt(Math.pow(playerCenterX - chestCenterX, 2) + Math.pow(playerCenterY - chestCenterY, 2));

                    // Open chest if player is within 1.5 tiles distance
                    if (distance < gameTileSize ) {
                        obj.opened = true; // Open chest
                        player.keys--; // Decrement key count
                        keySystem.resetKeys(); // Reset visual display
                        // Re-add remaining keys to visual display
                        for (int i = 0; i < player.keys; i++) {
                            keySystem.addKey();
                        }
                        obj.collision = false; // Remove collision so player can walk through
                        chestSystem.openChest(); // Decrement chest counter

                    }
                }
            }

            // Check for nearby NPCs to show dialogue
            boolean nearNPC = false;
            for (NPC npc : npcM.npcs) {
                if (npc.hasDialogue) {
                    // Calculate distance to NPC
                    int playerCenterX = player.worldX + player.solidArea.x + player.solidArea.width / 2;
                    int playerCenterY = player.worldY + player.solidArea.y + player.solidArea.height / 2;
                    int npcCenterX = npc.worldX + gameTileSize / 2;
                    int npcCenterY = npc.worldY + gameTileSize / 2;

                    int distance = (int) Math.sqrt(Math.pow(playerCenterX - npcCenterX, 2) + Math.pow(playerCenterY - npcCenterY, 2));

                    // Show message if player is within 2 tiles of NPC
                    if (distance < gameTileSize * 1.5) {
                        messageSystem.showMessage(npc.dialogue);
                        nearNPC = true;
                        break; // Show only one message at a time
                    }
                }
            }
            // Hide message if not near any NPC
            if (!nearNPC) {
                messageSystem.hideMessage();
            }

            // Update command adapter
            if (commandParser != null && commandParser.adapter != null) {
                commandParser.adapter.update();
            }

            // Update message system to auto-hide print messages after duration
            messageSystem.update();

            // Check for victory (all chests opened) - check BEFORE death
            if (chestSystem.getRemainingChests() == 0 && gameState == playState) {
                gameState = winState;
                this.requestFocusInWindow(); // Request focus for keyboard input
                return; // Exit update immediately to prevent further processing
            }

            // Check for death
            if (healthSystem.isDead() && gameState == playState) {
                gameState = gameOverState;
                this.requestFocusInWindow(); // Request focus for keyboard input
                return; // Exit update immediately to prevent further processing
            }
        }
    }

    // Checks if position is visible on screen
    private boolean isVisible(int worldX, int worldY, int margin) {
        return worldX + margin > player.worldX - player.screenX &&
               worldX - margin < player.worldX + player.screenX &&
               worldY + margin > player.worldY - player.screenY &&
               worldY - margin < player.worldY + player.screenY;
    }

    // Gets screen position for drawable entity
    private int[] getScreenPos(Drawable d) {
        if (d instanceof Player) {
            return new int[]{player.screenX, player.screenY};
        } else if (d instanceof MapObject obj) {
            return new int[]{obj.worldX - player.worldX + player.screenX, obj.worldY - player.worldY + player.screenY};
        } else if (d instanceof NPC npc) {
            return new int[]{npc.worldX - player.worldX + player.screenX, npc.worldY - player.worldY + player.screenY};
        }
        return null;
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        // If game over or win state, only draw the overlay screen (skip super.paintComponent)
        if (gameState == gameOverState) {
            healthSystem.drawGameOver(g2, screenWidth, screenHeight, selectedGameOverOption);
            g2.dispose();
            return;
        }

        if (gameState == winState) {
            healthSystem.drawWinScreen(g2, screenWidth, screenHeight, selectedWinOption);
            g2.dispose();
            return;
        }

        // Normal game rendering - call super.paintComponent for background
        super.paintComponent(g);

        // Normal game rendering
        tileM.draw(g2);

        List<Drawable> drawables = new ArrayList<>();

        for (MapObject obj : objM.objects) {
            if (isVisible(obj.worldX, obj.worldY, gameTileSize * 3)) {
                drawables.add(obj);
            }
        }

        drawables.add(player);

        for (NPC npc : npcM.npcs) {
            if (isVisible(npc.worldX, npc.worldY, gameTileSize * 4)) {
                drawables.add(npc);
            }
        }

        drawables.sort(Comparator.comparingInt(Drawable::getSortY));

        for (Drawable d : drawables) {
            int[] pos = getScreenPos(d);
            if (pos != null) {
                d.draw(g2, pos[0], pos[1]);
            }
        }
//        g2.setColor(Color.RED);
//        for (Object o : objM.objects) {
//            if (o instanceof MapObject) {
//                MapObject obj = (MapObject) o;
//                if (obj.collision) {
//                    g2.setColor(Color.RED);
//                    int screenX = obj.worldX - player.worldX + player.screenX;
//                    int screenY = obj.worldY - player.worldY + player.screenY;
//                    if (obj.worldX + gameTileSize > player.worldX - player.screenX &&
//                            obj.worldX - gameTileSize < player.worldX + player.screenX) {
//                        g2.drawRect(screenX + obj.solidArea.x, screenY + obj.solidArea.y, obj.solidArea.width, obj.solidArea.height);
//                    }
//                } else {
//                    g2.setColor(Color.cyan);
//                    int screenX = obj.worldX - player.worldX + player.screenX;
//                    int screenY = obj.worldY - player.worldY + player.screenY;
//                    if (obj.worldX + gameTileSize > player.worldX - player.screenX &&
//                            obj.worldX - gameTileSize < player.worldX + player.screenX) {
//                        g2.drawRect(screenX + obj.solidArea.x, screenY + obj.solidArea.y, obj.solidArea.width, obj.solidArea.height);
//                    }
//                }
//            }
//        }
//        // Draw player collision rect
//        g2.setColor(Color.BLUE);
//        g2.drawRect(player.screenX + player.solidArea.x, player.screenY + player.solidArea.y, player.solidArea.width, player.solidArea.height);
//
//        // Draw NPC collision rect
//        g2.setColor(Color.GREEN);
//        for (NPC npc : npcM.npcs) {
//            if (npc.worldX + gameTileSize > player.worldX - player.screenX &&
//                    npc.worldX - gameTileSize < player.worldX + player.screenX) {
//                int screenX = npc.worldX - player.worldX + player.screenX;
//                int screenY = npc.worldY - player.worldY + player.screenY;
//                g2.drawRect(screenX + npc.solidArea.x, screenY + npc.solidArea.y, npc.solidArea.width, npc.solidArea.height);
//            }
//        }

        if (gameState == playState || gameState == pauseState) {
            gameUI.draw(g2);
        }

        // Draw message bar on top of everything (if active)
        if (gameState == playState) {
            messageSystem.draw(g2);
        }

        g2.dispose();
    }



    // Resets game to initial state
    public void restartGame() {
        player.setDefault(); // Reset player position
        player.keys = 0; // Reset key count
        healthSystem.resetHealth(); // Reset health
        keySystem.resetKeys(); // Reset key display
        chestSystem.resetChests(); // Reset chest counter

        // Reset all collected objects and chests
        for (MapObject obj : objM.objects) {
            obj.collected = false; // Reset keys
            obj.opened = false; // Reset chests
            // Restore collision for chests
            if (obj.name.startsWith("chest")) {
                obj.collision = true;
            }
        }

        // Clear command queue
        if (commandParser != null && commandParser.adapter != null) {
            commandParser.adapter.clearQueue();
        }

        gameState = playState; // Return to play state
    }
}
