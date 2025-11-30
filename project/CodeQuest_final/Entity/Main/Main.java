package CodeQuest.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Main class - entry point for CodeQuest game
public class Main {
    public static void main(String[] args) {
        // Create main window
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("CodeQuest");

        // Create game panel and command input panel
        GamePanel gamePanel = new GamePanel();
        CommandInputPanel commandPanel = new CommandInputPanel(gamePanel.commandParser);

        // Create layered pane for menus
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(gamePanel.screenWidth, gamePanel.screenHeight));

        // Add game panel to layered pane
        gamePanel.setBounds(0, 0, gamePanel.screenWidth, gamePanel.screenHeight);
        layeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);

        // Create menu manager
        MenuManager menuManager = new MenuManager(gamePanel, layeredPane);

        // Create container layout
        JPanel container = new JPanel(new BorderLayout());
        container.add(layeredPane, BorderLayout.CENTER);
        container.add(commandPanel, BorderLayout.EAST);

        // Setup and show window
        frame.add(container);
        frame.pack();
        frame.setLocationRelativeTo(null); // Center window
        frame.setVisible(true);

        // Global keyboard event dispatcher for ESC and R keys
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
            new KeyEventDispatcher() {
                @Override
                public boolean dispatchKeyEvent(KeyEvent e) {
                    if (e.getID() == KeyEvent.KEY_PRESSED) {
                        // ESC key - pause game or exit on game over
                        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                            if (gamePanel.gameState == gamePanel.playState) {
                                gamePanel.gameState = gamePanel.pauseState;
                                menuManager.update();
                                return true;
                            } else if (gamePanel.gameState == gamePanel.gameOverState) {
                                gamePanel.gameState = gamePanel.menuState;
                                menuManager.update();
                                return true;
                            } else if (gamePanel.gameState == gamePanel.winState) {
                                gamePanel.gameState = gamePanel.menuState;
                                menuManager.update();
                                return true;
                            }
                        }
                        // R key - restart on game over
                        if (e.getKeyCode() == KeyEvent.VK_R) {
                            if (gamePanel.gameState == gamePanel.gameOverState) {
                                gamePanel.restartGame();
                                return true;
                            } else if (gamePanel.gameState == gamePanel.winState) {
                                gamePanel.restartGame();
                                return true;
                            }
                        }
                    }
                    return false;
                }
            }
        );

        // Start game thread
        gamePanel.startGameThread();

        // Timer to update menu visibility
        Timer menuTimer = new Timer(50, e -> menuManager.update());
        menuTimer.start();

        // Timer to update command queue label
        Timer queueTimer = new Timer(100, e -> commandPanel.updateQueueLabel());
        queueTimer.start();

    }
}
