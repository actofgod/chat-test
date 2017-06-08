package wg_test.chat.server.entity;

import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

/**
 * Интерфейс токена пользователя
 */
public interface UserToken
{
    /**
     * Возвращает инстанс пользователя для которого был создан токен
     * @return Инстанс пользователя
     */
    OnlineUser getUser();

    /**
     * Возвращает значение токена в виде строки
     * @return Строка токена пользователя
     */
    String getTokenValue();

    /**
     * Возвращает дату, до которой токен быдут валиден
     * @return Дата, до которой токен валиден, после которой токен будет удалён
     */
    Date getValidBefore();

    /**
     * Проверяет, активно ли сетевое соединение для текущего токена
     * @return True если сетевое соединение активно, false если нет
     */
    boolean hasActiveConnection();

    /**
     * Возвращает хэндлер сетевого соединения, связанного с текущим токеном или null если соединение не активно
     * @return Хэндлер сетевого соединения или null
     */
    ChannelHandlerContext getChannelHandlerContext();

    /**
     * Удаляет связь токена с сетевым соединением, удаляет себя из списка онлайн токенов пользователя
     */
    void invalidateConnectionContext();

    /**
     * Проверяет, валиден ли токен
     * @return True если токен валиден, false если нет
     */
    boolean isValid();
}
