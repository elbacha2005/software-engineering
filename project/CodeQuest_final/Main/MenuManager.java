package CodeQuest.Main;

import javax.swing.*;
import java.awt.*;

// Manages all game menus and their visibility
public class MenuManager {

    private GamePanel gamePanel; // Reference to game panel
    private TitleScreen titleScreen; // Title screen menu
    private MainMenu mainMenu; // Main menu
    private PauseMenu pauseMenu; // Pause menu
    private OptionsMenu optionsMenu; // Options menu
    private JLayeredPane layeredPane; // Container for layered menus
    
    // Constructor initializes all menus
    public MenuManager(GamePanel gamePanel, JLayeredPane layeredPane) {
        this.gamePanel = gamePanel;
        this.layeredPane = layeredPane;

        // Create menu instances
        titleScreen = new TitleScreen(gamePanel);
        mainMenu = new MainMenu(gamePanel);
        pauseMenu = new PauseMenu(gamePanel);
        optionsMenu = new OptionsMenu(gamePanel);

        // Set menu sizes to match screen
        Dimension size = new Dimension(gamePanel.screenWidth, gamePanel.screenHeight);
        titleScreen.setPreferredSize(size);
        titleScreen.setSize(size);
        mainMenu.setPreferredSize(size);
        mainMenu.setSize(size);
        pauseMenu.setPreferredSize(size);
        pauseMenu.setSize(size);
        optionsMenu.setPreferredSize(size);
        optionsMenu.setSize(size);

        // Add all menus to layered pane
        layeredPane.add(titleScreen, JLayeredPane.POPUP_LAYER);
        layeredPane.add(mainMenu, JLayeredPane.POPUP_LAYER);
        layeredPane.add(pauseMenu, JLayeredPane.POPUP_LAYER);
        layeredPane.add(optionsMenu, JLayeredPane.POPUP_LAYER);

        hideAll(); // Start with all menus hidden
    }
    
    // Updates menu visibility based on game state
    public void update() {
        hideAll(); // Hide all menus first

        // Show/hide game panel based on state
        if (gamePanel.gameState == gamePanel.playState ||
            gamePanel.gameState == gamePanel.gameOverState ||
            gamePanel.gameState == gamePanel.winState) {
            gamePanel.setVisible(true);
        } else {
            gamePanel.setVisible(false);
        }

        // Manage background music based on state
        if (gamePanel.soundManager != null) {
            if (gamePanel.gameState == gamePanel.playState) {
                // Play town music during gameplay (only if not already playing)
                gamePanel.soundManager.playMusic("town");
            } else if (gamePanel.gameState == gamePanel.pauseState) {
                // Stop music when paused
                gamePanel.soundManager.stopMusic();
            } else if (gamePanel.gameState == gamePanel.titleState ||
                       gamePanel.gameState == gamePanel.menuState ||
                       gamePanel.gameState == gamePanel.optionsState ||
                       gamePanel.gameState == gamePanel.gameOverState ||
                       gamePanel.gameState == gamePanel.winState) {
                // Play menu music for menu states (except pause)
                gamePanel.soundManager.playMusic("menu");
            }
        }

        // Show appropriate menu for current state
        switch (gamePanel.gameState) {
            case 0: // Title state
                titleScreen.setVisible(true);
                titleScreen.requestFocusInWindow();
                break;

            case 1: // Main menu state
                mainMenu.setVisible(true);
                mainMenu.requestFocusInWindow();
                break;

            case 2: // Play state
                break;

            case 3: // Pause state
                pauseMenu.setVisible(true);
                pauseMenu.requestFocusInWindow();
                break;

            case 4: // Options state
                optionsMenu.setVisible(true);
                optionsMenu.requestFocusInWindow();
                break;

            case 5: // Game over state
                gamePanel.requestFocusInWindow();
                break;

            case 6: // Win state
                gamePanel.requestFocusInWindow();
                break;
        }
    }
    
    // Hides all menus
    private void hideAll() {
        titleScreen.setVisible(false);
        mainMenu.setVisible(false);
        pauseMenu.setVisible(false);
        optionsMenu.setVisible(false);
    }

    // Stops menu animations (like title screen fade)
    public void stopAnimations() {
        titleScreen.stop();
    }
}
