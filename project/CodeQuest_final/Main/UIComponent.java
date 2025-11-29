package CodeQuest.Main;

import java.awt.*;

// Interface for UI elements like buttons and menus
public interface UIComponent {
    void draw(Graphics2D g2); // Draw the UI component
    void update(int mouseX, int mouseY); // Update component state based on mouse position
    boolean handleMouseInput(int mouseX, int mouseY); // Handle mouse clicks, returns true if handled
}
