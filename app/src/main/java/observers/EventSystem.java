package observers;

import java.util.ArrayList;
import java.util.List;

import engine.GameObject;
import observers.events.Event;;

public class EventSystem {
    private static List<Observer> observers = new ArrayList<>();

    public static void addObservers(Observer observer) {
        observers.add(observer);
    }

    public static void notify(GameObject go, Event event) {
        for (Observer observer : observers) {
            observer.onNotify(go, event);
        }
    }
}
