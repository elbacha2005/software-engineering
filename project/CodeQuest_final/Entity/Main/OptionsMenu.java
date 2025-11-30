package CodeQuest.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Displays the options menu for adjusting settings
public class OptionsMenu extends JPanel {

    private GamePanel gamePanel; // Reference to game panel
    private String[] options = { // Menu options
        "Music Volume",
        "SFX Volume",
        "Command Speed",
        "Movement Speed",
        "Back"
    };
    private int[] values = {50, 50, 3, 10}; // Current values for each setting
    private int selectedOption = 0; // Currently selected option
    private Font menuFont; // Font for menu items
    private Font titleFont; // Font for title
    private Font valueFont; // Font for values
    
    // Constructor initializes options menu
    public OptionsMenu(GamePanel gamePanel) {
        this.gamePanel = gamePanel;

        setPreferredSize(new Dimension(gamePanel.screenWidth, gamePanel.screenHeight));
        setBackground(new Color(25, 35, 55));
        setFocusable(true);

        // Set up fonts
        menuFont = new Font("Arial", Font.BOLD, 26);
        titleFont = new Font("Arial", Font.BOLD, 44);
        valueFont = new Font("Arial", Font.PLAIN, 24);

        // Handle keyboard input for menu navigation
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();

                // Navigate up
                if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
                    selectedOption--;
                    if (selectedOption < 0) {
                        selectedOption = options.length - 1;
                    }
                    repaint();
                }

                // Navigate down
                if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
                    selectedOption++;
                    if (selectedOption >= options.length) {
                        selectedOption = 0;
                    }
                    repaint();
                }

                // Decrease value
                if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
                    adjustValue(-1);
                }

                // Increase value
                if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
                    adjustValue(1);
                }

                // Select "Back" option
                if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
                    if (selectedOption == options.length - 1) {
                        goBack();
                    }
                }

                // Go back
                if (code == KeyEvent.VK_ESCAPE) {
                    goBack();
                }
            }
        });
    }
    
    // Adjusts the value of the selected option
    private void adjustValue(int delta) {
        if (selectedOption < values.length) {
            values[selectedOption] += delta * getIncrement(selectedOption);

            // Clamp values and apply settings
            switch (selectedOption) {
                case 0: // Music volume
                case 1: // SFX volume
                    values[selectedOption] = Math.max(0, Math.min(100, values[selectedOption]));
                    break;
                case 2: // Command speed
                    values[selectedOption] = Math.max(1, Math.min(10, values[selectedOption]));
                    applyCommandSpeed();
                    break;
                case 3: // Movement speed
                    values[selectedOption] = Math.max(5, Math.min(30, values[selectedOption]));
                    applyMovementSpeed();
                    break;
            }

            repaint();
        }
    }

    // Returns increment amount for each option
    private int getIncrement(int optionIndex) {
        switch (optionIndex) {
            case 0: // Music volume
            case 1: // SFX volume
                return 5;
            case 2: // Command speed
                return 1;
            case 3: // Movement speed
                return 5;
            default:
                return 1;
        }
    }

    // Applies command speed setting to parser
    private void applyCommandSpeed() {
        int delay = 550 - (values[2] * 50); // Convert speed level to delay
        if (gamePanel.commandParser != null && gamePanel.commandParser.adapter != null) {
            gamePanel.commandParser.adapter.setActionDelay(delay);
            System.out.println("⚙️ Command speed set to level " + values[2] + " (" + delay + "ms)");
        }
    }

    // Applies movement speed setting to player
    private void applyMovementSpeed() {
        gamePanel.player.speed = values[3];
        System.out.println("⚙️ Movement speed set to " + values[3] + " pixels");
    }

    // Returns to previous menu
    private void goBack() {
        if (gamePanel.gameState == gamePanel.optionsState) {
            gamePanel.gameState = gamePanel.menuState;
        }
        System.out.println("⬅️ Returning from options...");
    }
    
    // Renders the options menu
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw gradient background
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(25, 35, 55),
            0, getHeight(), new Color(45, 25, 70)
        );
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Draw title
        g2.setFont(titleFont);
        g2.setColor(new Color(100, 200, 255));
        String title = "OPTIONS";
        FontMetrics fm = g2.getFontMetrics();
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        g2.drawString(title, titleX, 80);

        // Draw options with value bars
        int startY = 180;
        int spacing = 70;
        
        for (int i = 0; i < options.length; i++) {
            int y = startY + (i * spacing);
            boolean isSelected = (i == selectedOption);

            // Highlight selected option
            if (isSelected && i < options.length - 1) {
                g2.setColor(new Color(100, 150, 200, 50));
                g2.fillRoundRect(100, y - 35, getWidth() - 200, 50, 10, 10);
            }

            // Draw options with value bars
            if (i < values.length) {
                // Draw option label
                g2.setFont(menuFont);
                g2.setColor(isSelected ? new Color(255, 255, 100) : new Color(220, 220, 220));
                g2.drawString(options[i], 150, y);
                g2.setFont(valueFont);

                // Draw value bar
                int barX = getWidth() / 2 + 50;
                int barY = y - 15;
                int barWidth = 200;
                int barHeight = 20;

                // Bar background
                g2.setColor(new Color(60, 60, 80));
                g2.fillRoundRect(barX, barY, barWidth, barHeight, 10, 10);

                // Calculate fill width based on value
                int fillWidth;
                if (i < 2) { // Volume (0-100)
                    fillWidth = (int)((values[i] / 100.0) * barWidth);
                } else if (i == 2) { // Command speed (1-10)
                    fillWidth = (int)(((values[i] - 1) / 9.0) * barWidth);
                } else { // Movement speed (5-30)
                    fillWidth = (int)(((values[i] - 5) / 25.0) * barWidth);
                }

                // Draw filled portion
                g2.setColor(isSelected ? new Color(100, 255, 100) : new Color(100, 200, 150));
                g2.fillRoundRect(barX, barY, fillWidth, barHeight, 10, 10);

                // Draw value text
                String valueText;
                if (i < 2) {
                    valueText = values[i] + "%";
                } else {
                    valueText = String.valueOf(values[i]);
                }

                g2.setColor(Color.WHITE);
                g2.drawString(valueText, barX + barWidth + 20, y);

                // Draw adjustment arrows when selected
                if (isSelected) {
                    g2.setColor(new Color(255, 255, 100));
                    // Left arrow
                    g2.fillPolygon(
                        new int[]{barX - 20, barX - 10, barX - 20},
                        new int[]{y - 10, y, y + 10},
                        3
                    );
                    // Right arrow
                    g2.fillPolygon(
                        new int[]{barX + barWidth + 10, barX + barWidth + 20, barX + barWidth + 10},
                        new int[]{y - 10, y, y + 10},
                        3
                    );
                }
            } else {
                // Draw "Back" option
                if (isSelected) {
                    g2.setColor(new Color(255, 255, 100, 80));
                    int buttonWidth = 200;
                    int buttonX = (getWidth() - buttonWidth) / 2;
                    g2.fillRoundRect(buttonX, y - 30, buttonWidth, 40, 10, 10);
                }

                g2.setFont(menuFont);
                g2.setColor(isSelected ? new Color(255, 255, 100) : new Color(220, 220, 220));
                String backText = options[i];
                fm = g2.getFontMetrics();
                int backX = (getWidth() - fm.stringWidth(backText)) / 2;
                g2.drawString(backText, backX, y);
            }
        }

        // Draw instructions at bottom
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.setColor(new Color(180, 180, 180));
        String instructions = "↑↓ Navigate  |  ←→ Adjust  |  ENTER Confirm  |  ESC Back";
        fm = g2.getFontMetrics();
        int instX = (getWidth() - fm.stringWidth(instructions)) / 2;
        g2.drawString(instructions, instX, getHeight() - 30);

        g2.dispose();
    }
}
