package CodeQuest.Main;

/**
 * Subject interface - part of the Observer Design Pattern
 * Subjects maintain a list of Observers and notify them when state changes
 * Used by game systems (HealthSystem, KeySystem, ChestSystem) to notify UI updates
 */
public interface Subject {
    /**
     * Add an Observer to the notification list
     * @param o The Observer to register
     */
    void registerObserver(Observer o);

    /**
     * Remove an Observer from the notification list
     * @param o The Observer to unregister
     */
    void unregisterObserver(Observer o);

    /**
     * Notify all registered Observers that state has changed
     * Calls update() on each Observer
     */
    void notifyObservers();
}
