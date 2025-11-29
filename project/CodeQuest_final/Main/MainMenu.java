package CodeQuest.Main;

import CodeQuest.Tiles.AssetHandler;

import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Displays the main menu with options
public class MainMenu extends JPanel {
    private BufferedImage backgroundImage; // Background image
    private GamePanel gamePanel; // Reference to game panel
    private String[] menuOptions = {"Play Game", "Options", "Exit"}; // Menu options
    private int selectedOption = 0; // Currently selected option
    private Font menuFont; // Font for menu items
    private Font titleFont; // Font for title
    
    // Constructor initializes main menu
    public MainMenu(GamePanel gamePanel) {
        this.gamePanel = gamePanel;

        setPreferredSize(new Dimension(gamePanel.screenWidth, gamePanel.screenHeight));
        setBackground(new Color(20, 30, 50));
        setFocusable(true);

        // Set up fonts
        menuFont = new Font("Arial", Font.BOLD, 30);
        titleFont = new Font("Arial", Font.BOLD, 50);

        loadBackgroundImage();

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

                // Go back to title
                if (code == KeyEvent.VK_ESCAPE) {
                    gamePanel.gameState = gamePanel.titleState;
                }
            }
        });
    }

    // Loads background image from asset handler
    private void loadBackgroundImage() {
        backgroundImage = AssetHandler.getInstance().getImage("background");
    }

    // Handles menu option selection
    private void selectOption() {
        switch (selectedOption) {
            case 0: // Play Game
                gamePanel.gameState = gamePanel.playState;
                gamePanel.requestFocusInWindow();
                System.out.println("🎮 Starting game...");
                break;

            case 1: // Options
                gamePanel.gameState = gamePanel.optionsState;
                System.out.println("⚙️ Opening options...");
                break;

            case 2: // Exit
                System.out.println("👋 Exiting game...");
                System.exit(0);
                break;
        }
    }
    
    // Renders the main menu
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
            // Add dark overlay
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillRect(0, 0, getWidth(), getHeight());
        } else {
            // Fallback gradient if no background
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(20, 30, 50),
                0, getHeight(), new Color(40, 20, 60)
            );
            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        
        // Draw title (empty in this menu)
        g2.setFont(titleFont);
        String title = "";
        FontMetrics fm = g2.getFontMetrics();
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.drawString(title, titleX + 3, 103);

        g2.setColor(new Color(100, 200, 255));
        g2.drawString(title, titleX, 100);

        // Draw menu options
        g2.setFont(menuFont);
        int startY = getHeight() / 2 - 50;
        int spacing = 60;

        for (int i = 0; i < menuOptions.length; i++) {
            int y = startY + (i * spacing);

            // Highlight selected option
            if (i == selectedOption) {
                g2.setColor(new Color(255, 255, 100));

                // Draw selection arrow
                g2.fillPolygon(
                    new int[]{getWidth() / 2 - 150, getWidth() / 2 - 170, getWidth() / 2 - 150},
                    new int[]{y - 15, y - 5, y + 5},
                    3
                );

                // Draw highlight box
                g2.setColor(new Color(255, 255, 100, 50));
                g2.fillRoundRect(getWidth() / 2 - 140, y - 30, 280, 40, 10, 10);

                g2.setColor(new Color(255, 255, 100));
            } else {
                g2.setColor(new Color(200, 200, 200));
            }

            // Draw text shadow
            g2.setColor(new Color(0, 0, 0, 200));
            fm = g2.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(menuOptions[i])) / 2;
            g2.drawString(menuOptions[i], textX + 2, y + 2);

            // Draw text
            if (i == selectedOption) {
                g2.setColor(new Color(255, 255, 100));
            } else {
                g2.setColor(new Color(200, 200, 200));
            }
            g2.drawString(menuOptions[i], textX, y);
        }

        // Draw instructions at bottom
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        String instructions = "↑↓ to navigate  |  ENTER to select  |  ESC to go back";
        fm = g2.getFontMetrics();
        int instX = (getWidth() - fm.stringWidth(instructions)) / 2;

        g2.setColor(new Color(0, 0, 0, 200));
        g2.drawString(instructions, instX + 1, getHeight() - 29);

        g2.setColor(new Color(150, 150, 150));
        g2.drawString(instructions, instX, getHeight() - 30);

        g2.dispose();
    }
}
