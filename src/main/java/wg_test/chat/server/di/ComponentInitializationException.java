package wg_test.chat.server.di;

/**
 * Класс исключений генерируемых при инициализации контейнера и объектов внутри него
 */
public class ComponentInitializationException extends RuntimeException
{
    /**
     * Компонент при инициализации которого произошла ошибка
     */
    final Component component;

    /**
     * @param component Компонент при инициализации которого пргизошла ошибка
     * @param message Сообщение, описывающее исключение
     */
    public ComponentInitializationException(Component component, String message)
    {
        super(message);
        this.component = component;
    }

    /**
     * @param component Компонент при инициализации которого пргизошла ошибка
     * @param message Сообщение, описывающее исключение
     * @param cause Исключение, произошедшее внутри компонента при инициализации
     */
    public ComponentInitializationException(Component component, String message, Throwable cause)
    {
        super(message, cause);
        this.component = component;
    }

    /**
     * Возвращает компонент, при инициализации которого произошла ошибка
     * @return Инстанс объекта внутри контейнера
     */
    public Component getComponent()
    {
        return component;
    }
}
