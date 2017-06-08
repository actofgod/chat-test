package wg_test.chat.server.entity;

import java.util.List;

/**
 * Интерфейс пользователя онлайн
 */
public interface OnlineUser extends User
{
    /**
     * Возвращает список валидных зарегестрированных токенов пользователя
     * @return Список валидных токенов пользователя
     */
    List<? extends UserToken> getActiveTokens();
}
