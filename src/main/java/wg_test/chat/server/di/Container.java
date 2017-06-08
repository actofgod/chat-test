package wg_test.chat.server.di;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Примитивный класс DI контейнера
 */
public class Container
{
    /**
     * Хэш таблица с объектами внутри контейнера
     */
    private ConcurrentHashMap<String, Object> container;

    /**
     * Флаг инициализации контейнера
     */
    private boolean isInitialized;

    public Container()
    {
        container = new ConcurrentHashMap<>();
        isInitialized = false;
    }

    /**
     * Инициализирует контейнер, если он ещё не был инициализирован
     */
    protected void initialize()
    {
        if (isInitialized) {
            return;
        }
        for (Map.Entry<String, Object> e: container.entrySet()) {
            if (e.getValue() instanceof Component) {
                ((Component) e.getValue()).initialize();
            }
        }
        isInitialized = true;
    }

    /**
     * Возвращает инстанс объекта внутри контейнера по его имени
     * @param name Имя запрашиваемого компонента
     * @return Объект компонента
     * @throws ComponentNotFoundException Генерируется если по имени объект внутри контейнера не был найден
     */
    public Object get(String name) throws ComponentNotFoundException
    {
        Object value = container.get(name);

        if (value == null) {
            throw new ComponentNotFoundException(name);
        }
        return value;
    }

    /**
     * Добавляет в контейнер новый объект
     * @param name Имя добавляемого объекта
     * @param value Инстанс добавляемого значения
     */
    protected void registerComponent(String name, Object value)
    {
        container.put(name, value);
    }
}
