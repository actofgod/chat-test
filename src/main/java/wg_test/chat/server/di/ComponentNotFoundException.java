package wg_test.chat.server.di;

/**
 * Класс исключений генерируемых, если объект внутри контейнера не найден
 */
public class ComponentNotFoundException extends IndexOutOfBoundsException
{
    /**
     * Имя запрашиваемого объекта в контейнере
     */
    final private String componentName;

    /**
     * @param componentName Имя запрашиваемого компонента в контейнере
     */
    public ComponentNotFoundException(final String componentName)
    {
        super("Component with name \"" + componentName + "\" not exists in container");
        this.componentName = componentName;
    }

    /**
     * Возвращает имя объекта, не найденного в контейнере
     * @return Имя запрашиваемого объекта
     */
    public String getComponentName()
    {
        return componentName;
    }
}
