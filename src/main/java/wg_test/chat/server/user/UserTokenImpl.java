package wg_test.chat.server.user;

import io.netty.channel.ChannelHandlerContext;
import wg_test.chat.server.entity.OnlineUser;
import wg_test.chat.server.entity.UserToken;

import java.util.Date;

public class UserTokenImpl implements UserToken
{
    final private String token;
    final private OnlineUserImpl user;
    private Date validBefore;

    private ChannelHandlerContext ctx;

    public UserTokenImpl(OnlineUserImpl user, final String token)
    {
        this.user = user;
        this.token = token;
        this.validBefore = new Date(System.currentTimeMillis() + 1000 * 3600 * 24);
        this.ctx = null;
    }

    /**
     * Возвращает инстанс пользователя для которого был создан токен
     * @return Инстанс пользователя
     */
    @Override
    public OnlineUser getUser()
    {
        return user;
    }

    /**
     * Возвращает значение токена в виде строки
     * @return Строка токена пользователя
     */
    @Override
    public String getTokenValue()
    {
        return token;
    }

    /**
     * Возвращает дату, до которой токен быдут валиден
     * @return Дата, до которой токен валиден, после которой токен будет удалён
     */
    @Override
    public Date getValidBefore()
    {
        return validBefore;
    }

    public UserTokenImpl setValidBefore(Date value)
    {
        validBefore = value;
        return this;
    }

    /**
     * Проверяет, активно ли сетевое соединение для текущего токена
     * @return True если сетевое соединение активно, false если нет
     */
    @Override
    public boolean hasActiveConnection()
    {
        return ctx != null;
    }

    /**
     * Возвращает хэндлер сетевого соединения, связанного с текущим токеном или null если соединение не активно
     * @return Хэндлер сетевого соединения или null
     */
    @Override
    public ChannelHandlerContext getChannelHandlerContext()
    {
        return ctx;
    }

    /**
     * Удаляет связь токена с сетевым соединением, удаляет себя из списка онлайн токенов пользователя
     */
    @Override
    public void invalidateConnectionContext()
    {
        ctx = null;
        user.removeToken(this);
    }

    /**
     * Проверяет, валиден ли токен
     * @return True если токен валиден, false если нет
     */
    @Override
    public boolean isValid()
    {
        return validBefore.getTime() - System.currentTimeMillis() > 0;
    }

    public UserTokenImpl setChannelHandlerContext(ChannelHandlerContext value)
    {
        ctx = value;
        return this;
    }

    @Override
    public int hashCode()
    {
        return (user.getId() << 16) | (token.hashCode() & 0x0000FFFF);
    }

    @Override
    public boolean equals(Object another)
    {
        if (another instanceof UserTokenImpl) {
            if (user.getId() == ((UserTokenImpl) another).user.getId()) {
                return token.equals(((UserTokenImpl) another).token);
            }
        }
        return false;
    }
}
