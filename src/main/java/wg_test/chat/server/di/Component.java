package wg_test.chat.server.di;

/**
 * Базовый класс объектов, хранимых в контейнере
 */
abstract public class Component
{
    /**
     * Инстанс контейнера, которому принадлежит компонент
     */
    private Container container;

    /**
     * Флаг инициализации компонента
     */
    private boolean isInitialized;

    public Component(Container container)
    {
        this.container = container;
        this.isInitialized = false;
    }

    /**
     * Производит инициализацию объекта внутри контейнера
     * @throws ComponentInitializationException Генерируется если при инициализации объекта произошла ошибка
     */
    public void initialize() throws ComponentInitializationException
    {
        if (!isInitialized) {
            init();
            isInitialized = true;
        }
    }

    /**
     * Возвращает контейнер, которому принадлежит текущий объект
     * @return Инстанс контейнера
     */
    protected Container getContainer()
    {
        return container;
    }

    /**
     * Инициализирует компонент
     * @throws ComponentInitializationException Генерируется если при инициализации произошла ошибка
     */
    protected void init() throws ComponentInitializationException
    {}
}
