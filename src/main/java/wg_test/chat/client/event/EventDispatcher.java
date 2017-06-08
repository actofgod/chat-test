package wg_test.chat.client.event;

import javafx.application.Platform;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventDispatcher
{
    private ConcurrentHashMap<Class, LinkedList<EventListener>> listeners;

    public EventDispatcher()
    {
        listeners = new ConcurrentHashMap<>();
    }

    public <T extends Event> void addEventListener(Class cls, EventListener<T> listener)
    {
        LinkedList<EventListener> l = listeners.get(cls);
        if (l == null) {
            l = new LinkedList<>();
            listeners.put(cls, l);
        }
        l.add(listener);
    }


    public void dispatchEvent(Event event)
    {
        Platform.runLater(() -> dispatch(event));
    }

    @SuppressWarnings("unchecked")
    private void dispatch(Event event)
    {
        System.out.println("Dispatch event " + event);
        LinkedList<EventListener> l = listeners.get(event.getClass());
        for (EventListener listener: l) {
            listener.processEvent(event);
        }
    }
}
