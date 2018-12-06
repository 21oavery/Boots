import java.util.*;

public class EventManager {
    private static EventManager instance = null;
    private static HashMap<String, Event> events = new HashMap<>();
    
    private Thread task = null;
    private boolean isON = true;
    private String id;
    
    public EventManager(String idIn, Thread tIn) throws IllegalArgumentException {
        if (idIn == null) throw new IllegalArgumentException("Event ID cannot be null");
        synchronized (EventManager.events) {
            if (EventManager.events.contains(idIn)) throw new IllegalArgumentException("Event ID already exists");
            EventManager.events.put(idIn, this);
            id = idIn;
        }
    }
    
    public static void stop() {
        synchronized (EventManager.instance) {
            if (EventManager.instance == null) return;
            EventManager.instance.stopUnsafe();
            EventManager.instance = null;
        }
    }
    
    private void stopUnsafe() {
        this.isOn = false;
    }
}
