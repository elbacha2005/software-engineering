package CodeQuest.Main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// Handles keyboard input for player movement and game controls
public class KeyHandler implements KeyListener {
    GamePanel gamePanel; // Reference to game panel for state checks
    public boolean UpPressed, DownPressed, LeftPressed, RightPressed; // Arrow key states

    // Constructor
    public KeyHandler(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode(); // Get key code

        // Handle game over state keys
        if (gamePanel.gameState == gamePanel.gameOverState) {
            // Navigate up
            if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
                gamePanel.selectedGameOverOption--;
                if (gamePanel.selectedGameOverOption < 0) {
                    gamePanel.selectedGameOverOption = 1; // Wrap to Main Menu
                }
                if (gamePanel.soundManager != null) {
                    gamePanel.soundManager.playNavigationSound(); // Play navigation sound
                }
                gamePanel.repaint(); // Update display
                return;
            }
            // Navigate down
            if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
                gamePanel.selectedGameOverOption++;
                if (gamePanel.selectedGameOverOption > 1) {
                    gamePanel.selectedGameOverOption = 0; // Wrap to Restart
                }
                if (gamePanel.soundManager != null) {
                    gamePanel.soundManager.playNavigationSound(); // Play navigation sound
                }
                gamePanel.repaint(); // Update display
                return;
            }
            // Select option
            if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
                if (gamePanel.selectedGameOverOption == 0) {
                    gamePanel.restartGame(); // Restart game
                } else if (gamePanel.selectedGameOverOption == 1) {
                    gamePanel.gameState = gamePanel.menuState; // Go to main menu
                    gamePanel.healthSystem.resetHealth();
                    gamePanel.selectedGameOverOption = 0; // Reset selection
                }
                return;
            }
            // Legacy shortcuts
            if (code == KeyEvent.VK_R) {
                gamePanel.restartGame(); // Restart on R key
                return;
            }
            if (code == KeyEvent.VK_ESCAPE) {
                gamePanel.gameState = gamePanel.menuState; // Return to menu
                gamePanel.healthSystem.resetHealth();
                return;
            }
            return; // Ignore other keys in game over state
        }

        // Handle win state keys (keyboard navigation)
        if (gamePanel.gameState == gamePanel.winState) {
            // Navigate up
            if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
                gamePanel.selectedWinOption--;
                if (gamePanel.selectedWinOption < 0) {
                    gamePanel.selectedWinOption = 1; // Wrap to Main Menu
                }
                if (gamePanel.soundManager != null) {
                    gamePanel.soundManager.playNavigationSound(); // Play navigation sound
                }
                gamePanel.repaint(); // Update display
                return;
            }
            // Navigate down
            if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
                gamePanel.selectedWinOption++;
                if (gamePanel.selectedWinOption > 1) {
                    gamePanel.selectedWinOption = 0; // Wrap to Restart
                }
                if (gamePanel.soundManager != null) {
                    gamePanel.soundManager.playNavigationSound(); // Play navigation sound
                }
                gamePanel.repaint(); // Update display
                return;
            }
            // Select option
            if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
                if (gamePanel.selectedWinOption == 0) {
                    gamePanel.restartGame(); // Restart game
                } else if (gamePanel.selectedWinOption == 1) {
                    gamePanel.gameState = gamePanel.menuState; // Go to main menu
                    gamePanel.selectedWinOption = 0; // Reset selection
                }
                return;
            }
            return; // Ignore other keys in win state
        }

        // Handle play state arrow keys for movement
        if (gamePanel.gameState == gamePanel.playState) {
            if (code == KeyEvent.VK_UP) {
                UpPressed = true;
            }
            if (code == KeyEvent.VK_DOWN) {
                DownPressed = true;
            }
            if (code == KeyEvent.VK_LEFT) {
                LeftPressed = true;
            }
            if (code == KeyEvent.VK_RIGHT) {
                RightPressed = true;
            }
        }

        // Handle ESC key - toggle pause
        if (code == KeyEvent.VK_ESCAPE) {
            if (gamePanel.gameState == gamePanel.playState) {
                gamePanel.gameState = gamePanel.pauseState; // Pause game
            } else if (gamePanel.gameState == gamePanel.pauseState) {
                gamePanel.gameState = gamePanel.playState; // Unpause game
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode(); // Get key code
        // Reset arrow key states when released
        if (code == KeyEvent.VK_UP) {
            UpPressed = false;
        }
        if (code == KeyEvent.VK_DOWN) {
            DownPressed = false;
        }
        if (code == KeyEvent.VK_LEFT) {
            LeftPressed = false;
        }
        if (code == KeyEvent.VK_RIGHT) {
            RightPressed = false;
        }
    }
}
