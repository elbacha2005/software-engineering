package CodeQuest.Main;

/**
 * Observer interface - part of the Observer Design Pattern
 * Observers are notified when the Subject they're watching changes state
 * Used for updating UI elements when game systems (health, keys, chests) change
 */
public interface Observer {
    /**
     * Called when the observed Subject's state changes
     * Implementing classes should update their display or state accordingly
     */
    void update();
}
