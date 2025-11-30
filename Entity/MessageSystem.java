package CodeQuest.Entity;

import java.awt.*;

// Displays NPC dialogue messages in a message bar
public class MessageSystem {

    private String currentMessage = ""; // Current message to display
    private boolean showMessage = false; // Whether to show the message bar
    private boolean isPrintMessage = false; // Whether message is from print() command
    private long printMessageStartTime = 0; // When print message started
    private long printMessageDuration = 2000; // Duration to show print messages (2 seconds)

    private int screenWidth; // Screen width for centering
    private int screenHeight; // Screen height for positioning
    private int barHeight = 80; // Height of message bar
    private int padding = 20; // Padding inside bar

    // Constructor
    public MessageSystem(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    // Set and show a new message (for NPCs)
    public void showMessage(String message) {
        this.currentMessage = message;
        this.showMessage = true;
        this.isPrintMessage = false;
    }

    // Set and show a print() command message with duration
    public void showPrintMessage(String message) {
        this.currentMessage = message;
        this.showMessage = true;
        this.isPrintMessage = true;
        this.printMessageStartTime = System.currentTimeMillis();
    }

    // Hide the message bar (only hides NPC messages, not print messages)
    public void hideMessage() {
        if (!isPrintMessage) {
            this.showMessage = false;
            this.currentMessage = "";
        }
    }

    // Update message state (call this in game loop)
    public void update() {
        // Auto-hide print messages after duration
        if (isPrintMessage && showMessage) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - printMessageStartTime >= printMessageDuration) {
                this.showMessage = false;
                this.currentMessage = "";
                this.isPrintMessage = false;
            }
        }
    }

    // Draw the message bar at the bottom of the screen
    public void draw(Graphics2D g2) {
        if (showMessage && !currentMessage.isEmpty()) {
            // Calculate bar position (bottom of screen)
            int barY = screenHeight - barHeight - 10;
            int barX = 10;
            int barWidth = screenWidth - 20;

            // Draw semi-transparent background
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
            g2.setColor(new Color(0, 0, 0)); // Black background
            g2.fillRoundRect(barX, barY, barWidth, barHeight, 15, 15); // Rounded corners

            // Draw border
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2.setColor(new Color(255, 255, 255)); // White border
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(barX, barY, barWidth, barHeight, 15, 15);

            // Draw message text
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.setColor(Color.WHITE);

            // Word wrap if message is too long
            FontMetrics fm = g2.getFontMetrics();
            String[] words = currentMessage.split(" ");
            StringBuilder line = new StringBuilder();
            int y = barY + padding + fm.getAscent();
            int maxWidth = barWidth - (padding * 2);

            for (String word : words) {
                String testLine = line + word + " ";
                if (fm.stringWidth(testLine) > maxWidth && line.length() > 0) {
                    // Draw current line and start new one
                    g2.drawString(line.toString().trim(), barX + padding, y);
                    y += fm.getHeight();
                    line = new StringBuilder(word + " ");
                } else {
                    line.append(word).append(" ");
                }
            }
            // Draw last line
            g2.drawString(line.toString().trim(), barX + padding, y);

            // Reset composite
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }
}
