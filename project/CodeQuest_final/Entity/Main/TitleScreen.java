package CodeQuest.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Displays the title screen with animated prompt
public class TitleScreen extends JPanel {

    private GamePanel gamePanel; // Reference to game panel
    private Font titleFont; // Font for title text
    private Font promptFont; // Font for prompt text
    private int alpha = 0; // Alpha value for fade animation
    private boolean fadeIn = true; // Fade direction
    private Timer animationTimer; // Timer for fade animation
    
    // Constructor initializes title screen
    public TitleScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;

        setPreferredSize(new Dimension(gamePanel.screenWidth, gamePanel.screenHeight));
        setBackground(Color.BLACK);
        setFocusable(true);

        // Set up fonts
        titleFont = new Font("Arial", Font.BOLD, 60);
        promptFont = new Font("Arial", Font.PLAIN, 20);

        // Handle ENTER key to proceed
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    gamePanel.gameState = gamePanel.titleState + 1; // Go to main menu
                }
            }
        });

        // Create fade animation for "Press ENTER" text
        animationTimer = new Timer(30, e -> {
            if (fadeIn) {
                alpha += 5; // Fade in
                if (alpha >= 255) {
                    alpha = 255;
                    fadeIn = false; // Switch to fade out
                }
            } else {
                alpha -= 5; // Fade out
                if (alpha <= 0) {
                    alpha = 0;
                    fadeIn = true; // Switch to fade in
                }
            }
            repaint();
        });
        animationTimer.start();
    }
    
    // Renders the title screen
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setFont(titleFont);
        g2.setColor(new Color(100, 200, 255));

        // Draw title with shadow effect
        String title = "CODEQUEST";
        FontMetrics fm = g2.getFontMetrics();
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        int titleY = getHeight() / 3;

        // Shadow
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(title, titleX + 3, titleY + 3);

        // Main title
        g2.setColor(new Color(100, 200, 255));
        g2.drawString(title, titleX, titleY);

        // Draw subtitle
        g2.setFont(new Font("Arial", Font.ITALIC, 20));
        g2.setColor(new Color(150, 150, 150));
        String subtitle = "Learn Programming Through Adventure";
        int subtitleX = (getWidth() - g2.getFontMetrics().stringWidth(subtitle)) / 2;
        g2.drawString(subtitle, subtitleX, titleY + 50);

        // Draw animated prompt (fades in/out)
        g2.setFont(promptFont);
        g2.setColor(new Color(255, 255, 255, alpha)); // Use animated alpha
        String prompt = "Press ENTER to Start";
        int promptX = (getWidth() - g2.getFontMetrics().stringWidth(prompt)) / 2;
        g2.drawString(prompt, promptX, getHeight() * 2 / 3);

        g2.dispose();
    }

    // Stops the fade animation
    public void stop() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }
}
