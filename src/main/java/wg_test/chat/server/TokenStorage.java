package wg_test.chat.server;

import com.lambdaworks.redis.RedisConnection;
import wg_test.chat.server.di.Component;
import wg_test.chat.server.entity.UserToken;

import java.util.Date;

/**
 * Класс хранилища токенов пользователей
 */
public class TokenStorage extends Component
{
    /**
     * Префикс ключей в редисе
     */
    private final static String KEY_PREFIX = "chat.token.";

    /**
     * Инстанс соединения с редисом
     */
    private RedisConnection<String, String> redis;

    public TokenStorage(ServiceLocator container)
    {
        super(container);
        redis = null;
    }

    /**
     * Добавляет токен в хранилище токенов
     * @param token Инстанс сохраняемого токена
     * @return True если токен был сохранен, false если нет
     */
    public boolean addToken(UserToken token)
    {
        String result = redis.psetex(
                KEY_PREFIX + token.getTokenValue(),
                token.getValidBefore().getTime() - System.currentTimeMillis(),
                Integer.toString(token.getUser().getId())
        );
        return "OK".equals(result);
    }

    /**
     * Возвращает айди пользователя по токену
     * @param tokenValue Токен в виде строки
     * @return Айди пользователя или ноль, если токен не найден
     */
    public int getTokenUserId(String tokenValue)
    {
        String userId = redis.get(KEY_PREFIX + tokenValue);
        if (userId != null && !userId.isEmpty()) {
            return Integer.parseInt(userId);
        }
        return 0;
    }

    /**
     * Возвращает дату, до которой токен будет валиден
     * @param tokenValue Токен в виде строки
     * @return Дата до которой токен валиден или null если токен не найден
     */
    public Date getTokenValidBefore(String tokenValue)
    {
        long ttl = redis.pttl(KEY_PREFIX + tokenValue);
        if (ttl <= 0) {
            return null;
        }
        return new Date(System.currentTimeMillis() + ttl);
    }

    /**
     * Удаляет токен из списка токенов
     * @param token Инстанс удаляемого токена
     */
    public void removeToken(UserToken token)
    {
        redis.del(KEY_PREFIX + token.getTokenValue());
    }

    @Override
    protected ServiceLocator getContainer()
    {
        return (ServiceLocator) super.getContainer();
    }

    @Override
    protected void init()
    {
        redis = getContainer().getRedisConnection();
    }
}
