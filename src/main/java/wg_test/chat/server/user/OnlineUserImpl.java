package wg_test.chat.server.user;

import wg_test.chat.server.entity.OnlineUser;
import wg_test.chat.server.entity.User;
import wg_test.chat.server.entity.UserToken;

import java.util.LinkedList;
import java.util.List;

public class OnlineUserImpl implements OnlineUser
{
    /** Инстанс пользователя */
    final private User linkedUser;

    /** Список токенов, выданных пользователю */
    private List<UserTokenImpl> tokens;

    public OnlineUserImpl(User user)
    {
        this.linkedUser = user;
        this.tokens = new LinkedList<>();
    }

    /**
     * Возвращает уникальный идентификатор пользователя
     * @return Айди пользователя
     */
    @Override
    public int getId()
    {
        return linkedUser.getId();
    }

    /**
     * Возвращает имя пользователя
     * @return Имя пользователя
     */
    @Override
    public String getUserName()
    {
        return linkedUser.getUserName();
    }

    /**
     * Проверяет, находится ли пользователь онлайн
     * @return True если пользователь онлайн, false если нет
     */
    @Override
    public boolean isOnline()
    {
        return !tokens.isEmpty();
    }

    /**
     * Возвращает хэш пароля пользователя в виде массива байтов
     * @return Хэш пароля пользователя
     */
    @Override
    public byte[] getPasswordHash()
    {
        return linkedUser.getPasswordHash();
    }

    /**
     * Возвращает соль пароля пользователя в виде массива байтов
     * @return Соль пароля пользователя
     */
    @Override
    public byte[] getPasswordSalt()
    {
        return linkedUser.getPasswordSalt();
    }

    /**
     * Возвращает список валидных зарегестрированных токенов пользователя
     * @return Список валидных токенов пользователя
     */
    @Override
    public List<? extends UserToken> getActiveTokens()
    {
        return tokens;
    }

    public UserTokenImpl addToken(UserTokenImpl token) throws Exception
    {
        if (token.getUser() != this) {
            throw new Exception("Invalid user token");
        }
        for (UserTokenImpl t: tokens) {
            if (token.getTokenValue().equals(t.getTokenValue())) {
                throw new Exception("Token already exists");
            }
        }
        tokens.add(token);
        return token;
    }

    public void removeToken(UserTokenImpl token)
    {
        tokens.remove(token);
    }

    @Override
    public String toString()
    {
        return "{OnlineUserImpl#" + getId() + " '" + getUserName() + "'}";
    }
}
