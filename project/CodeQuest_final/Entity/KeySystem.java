package CodeQuest.Entity;

import CodeQuest.Main.Observer;
import CodeQuest.Main.Subject;
import CodeQuest.Tiles.AssetHandler;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

// Manages key collection display with counter
public class KeySystem implements Subject {

    private List<Observer> observers = new ArrayList<>();

    private int maxKeys = 5; // Maximum keys to collect
    private int currentKeys = 0; // Current keys collected

    private BufferedImage keyFull; // Full key sprite

    private int keySize = 32; // Key display size in pixels
    private int screenX = 20; // X position on screen to draw key
    private int screenY = 60; // Y position on screen (below hearts)

    // Constructor: loads key image from asset handler
    public KeySystem() {
        keyFull = AssetHandler.getInstance().getImage("full_key");
    }

    // Add a key to the collection
    public void addKey() {
        if (currentKeys < maxKeys) {
            currentKeys++;
            notifyObservers();
        }
    }

    // Reset keys to 0
    public void resetKeys() {
        currentKeys = 0;
        notifyObservers();
    }



    // Get current number of keys collected
    public int getCurrentKeys() {
        return currentKeys;
    }

    // Set screen position where key will be drawn
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
