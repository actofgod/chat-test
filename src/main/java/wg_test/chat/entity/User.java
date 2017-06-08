package wg_test.chat.entity;

public interface User
{
    /**
     * Возвращает уникальный идентификатор пользователя
     * @return Айди пользователя
     */
    int getId();

    /**
     * Возвращает имя пользователя
     * @return Имя пользователя
     */
    String getUserName();

    /**
     * Проверяет, находится ли пользователь онлайн
     * @return True если пользователь онлайн, false если нет
     */
    boolean isOnline();
}
