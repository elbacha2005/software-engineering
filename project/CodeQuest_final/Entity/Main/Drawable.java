package CodeQuest.Main;

import java.awt.Graphics2D;

// Interface for game entities that can be drawn on screen (Player, NPCs, Objects)
public interface Drawable {
    int getSortY(); // Get Y position for draw order sorting (bottom entities drawn last)
    void draw(Graphics2D g2, int screenX, int screenY); // Draw the entity at screen position
}
