package CodeQuest.Entity;

import CodeQuest.Main.Observer;
import CodeQuest.Main.Subject;
import CodeQuest.Tiles.AssetHandler;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

// Manages chest counter display - counts down as chests are opened
public class ChestSystem implements Subject {

    private List<Observer> observers = new ArrayList<>();

    private int maxChests = 4; // Total chests in game
    private int remainingChests = 4; // Chests not yet opened

    private BufferedImage chestIcon; // Chest icon sprite

    private int iconSize = 32; // Icon display size in pixels
    private int screenX = 20; // X position on screen
    private int screenY = 100; // Y position on screen (below keys)

    // Constructor: loads chest image from asset handler
    public ChestSystem() {
        chestIcon = AssetHandler.getInstance().getImage("chest1"); // Use chest1 as icon
    }

    // Decrease chest count (when a chest is opened)
    public void openChest() {
        if (remainingChests > 0) {
            remainingChests--;
            notifyObservers();
        }
    }

    // Reset chest count to maximum
    public void resetChests() {
        remainingChests = maxChests;
        notifyObservers();
    }



    // Get current number of remaining chests
    public int getRemainingChests() {
        return remainingChests;
    }

    // Set screen position where chest icon will be drawn
    public void setScreenPosition(int x, int y) {
        this.screenX = x;
        this.screenY = y;
    }

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void unregisterObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
}
