package wg_test.chat.server.entity;

/**
 * Интерфейс пользователя чата на сервере
 */
public interface User extends wg_test.chat.entity.User
{
    /**
     * Возвращает хэш пароля пользователя в виде массива байтов
     * @return Хэш пароля пользователя
     */
    byte[] getPasswordHash();

    /**
     * Возвращает соль пароля пользователя в виде массива байтов
     * @return Соль пароля пользователя
     */
    byte[] getPasswordSalt();
}
