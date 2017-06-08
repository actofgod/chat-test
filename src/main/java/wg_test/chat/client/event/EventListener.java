package wg_test.chat.client.event;

public interface EventListener<T extends Event>
{
    void processEvent(T event);
}
