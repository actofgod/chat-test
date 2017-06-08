package wg_test.chat.server;

import io.netty.channel.ChannelHandlerContext;
import wg_test.chat.server.entity.UserToken;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Класс карты соотвествия контеквтос соединений и токенов пользователей
 */
public class TokenContextMap
{
    /**
     * Хэш таблица для поиска по контексту соединений токенов пользователей
     */
    private ConcurrentHashMap<ChannelHandlerContext, UserToken> map;

    /**
     * Конструктор, инициализирует хэш таблицу
     */
    public TokenContextMap()
    {
        map = new ConcurrentHashMap<>();
    }

    /**
     * Добавляет соответствие контекста соединения и токена пользователя
     * @param ctx Контекст соединения с клиентом
     * @param token Токен пользователя, которому принадлежит соединение
     */
    public void add(ChannelHandlerContext ctx, UserToken token)
    {
        if (map.containsKey(ctx)) {
            throw new RuntimeException("Context already in map");
        }
        System.out.println("Add user#" + token.getUser().getId() + " with token '" + token.getTokenValue() + "'");
        map.put(ctx, token);
    }

    /**
     * Удаляет из хэш таблицы контекст соединения и токен пользователя
     * @param ctx Удаляемый контекст соединения
     */
    public void remove(ChannelHandlerContext ctx)
    {
        UserToken token = map.remove(ctx);

        Logger logger = Logger.getLogger(TokenContextMap.class.getName());
        if (token == null) {
            logger.info("Remove unknown channel context");
        } else {
            logger.info("Remove user#" + token.getUser().getId() + " with token '" + token.getTokenValue() + "'");
        }
    }

    /**
     * Возвращает токен пользователя исходя из контекста соединения
     * @param ctx Контекст соединения клиента и сервера
     * @return Токен пользователя или null если контекст соединения в хэш таблице не найден
     */
    public UserToken getTokenByContext(ChannelHandlerContext ctx)
    {
        return map.get(ctx);
    }

    /**
     * Возвращает сет всех открытых соединений, зарегестрированных в хэш таблице
     * @return Сет всех открытых и авторизованных соединений
     */
    public Set<ChannelHandlerContext> getContextList()
    {
        return map.keySet();
    }
}
