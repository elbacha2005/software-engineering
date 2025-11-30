package CodeQuest.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Displays the pause menu overlay
public class PauseMenu extends JPanel {

    private GamePanel gamePanel; // Reference to game panel
    private String[] menuOptions = {"Resume", "Restart", "Options", "Main Menu", "Exit"}; // Menu options
    private int selectedOption = 0; // Currently selected option
    private Font menuFont; // Font for menu items
    private Font titleFont; // Font for title
    
    // Constructor initializes pause menu
    public PauseMenu(GamePanel gamePanel) {
        this.gamePanel = gamePanel;

        setPreferredSize(new Dimension(gamePanel.screenWidth, gamePanel.screenHeight));
        setOpaque(false); // Transparent background
        setFocusable(true);

        // Set up fonts
        menuFont = new Font("Arial", Font.BOLD, 28);
        titleFont = new Font("Arial", Font.BOLD, 48);

        // Handle keyboard input for menu navigation
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();

                // Navigate up
                if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
                    selectedOption--;
                    if (selectedOption < 0) {
                        selectedOption = menuOptions.length - 1;
                    }
                    if (gamePanel.soundManager != null) {
                        gamePanel.soundManager.playNavigationSound();
                    }
                    repaint();
                }

                // Navigate down
                if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
                    selectedOption++;
                    if (selectedOption >= menuOptions.length) {
                        selectedOption = 0;
                    }
                    if (gamePanel.soundManager != null) {
                        gamePanel.soundManager.playNavigationSound();
                    }
                    repaint();
                }

                // Select option
                if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
                    selectOption();
                }

                // Resume game
                if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_P) {
                    gamePanel.gameState = gamePanel.playState;
                    gamePanel.requestFocusInWindow();
                }
            }
        });
    }

    // Handles menu option selection
    private void selectOption() {
        switch (selectedOption) {
            case 0: // Resume
                gamePanel.gameState = gamePanel.playState;
                gamePanel.requestFocusInWindow();
                System.out.println("▶️ Resuming game...");
                break;

            case 1: // Restart
                gamePanel.player.setDefault();
                gamePanel.gameState = gamePanel.playState;
                gamePanel.requestFocusInWindow();
                System.out.println("🔄 Restarting game...");
                break;

            case 2: // Options
                gamePanel.gameState = gamePanel.optionsState;
                System.out.println("⚙️ Opening options...");
                break;

            case 3: // Main Menu
                gamePanel.gameState = gamePanel.menuState;
                System.out.println("🏠 Returning to main menu...");
                break;

            case 4: // Exit
                System.out.println("👋 Exiting game...");
                System.exit(4);
                break;
        }
    }
    
    // Renders the pause menu
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Define menu box dimensions
        int boxWidth = 400;
        int boxHeight = 450;
        int boxX = (getWidth() - boxWidth) / 2;
        int boxY = (getHeight() - boxHeight) / 2;

        // Draw shadow
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRoundRect(boxX + 5, boxY + 5, boxWidth, boxHeight, 20, 20);

        // Draw menu box background
        g2.setColor(new Color(30, 40, 60, 230));
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        // Draw box border
        g2.setColor(new Color(100, 150, 200));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        // Draw "PAUSED" title
        g2.setFont(titleFont);
        g2.setColor(new Color(255, 255, 100));
        String title = "PAUSED";
        FontMetrics fm = g2.getFontMetrics();
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        g2.drawString(title, titleX, boxY + 70);

        // Draw menu options
        g2.setFont(menuFont);
        int startY = boxY + 130;
        int spacing = 55;

        for (int i = 0; i < menuOptions.length; i++) {
            int y = startY + (i * spacing);

            // Highlight selected option
            if (i == selectedOption) {
                // Draw highlight box
                g2.setColor(new Color(255, 255, 100, 80));
                g2.fillRoundRect(boxX + 30, y - 25, boxWidth - 60, 40, 10, 10);

                // Draw selection arrow
                g2.setColor(new Color(255, 255, 100));
                g2.fillPolygon(
                    new int[]{boxX + 50, boxX + 40, boxX + 50},
                    new int[]{y - 10, y, y + 10},
                    3
                );

                g2.setColor(new Color(255, 255, 100));
            } else {
                g2.setColor(new Color(220, 220, 220));
            }

            // Draw menu option text
            g2.drawString(menuOptions[i], boxX + 80, y);
        }

        // Draw instructions at bottom
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.setColor(new Color(180, 180, 180));
        String instructions = "↑↓ Navigate  |  ENTER Select  |  ESC Resume";
        fm = g2.getFontMetrics();
        int instX = (getWidth() - fm.stringWidth(instructions)) / 2;
        g2.drawString(instructions, instX, boxY + boxHeight - 20);

        g2.dispose();
    }
}
