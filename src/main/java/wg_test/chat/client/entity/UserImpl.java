package wg_test.chat.client.entity;

import wg_test.chat.entity.User;
import wg_test.chat.proto.UserResponse;

public class UserImpl implements User
{
    private int id;
    private String userName;
    private boolean online;

    public UserImpl(int id, String userName)
    {
        this.id = id;
        this.userName = userName;
        this.online = true;
    }

    public UserImpl(UserResponse.User user)
    {
        this.id = user.getId();
        this.userName = user.getUsername();
        this.online = user.getOnline();
    }

    /**
     * Возвращает уникальный идентификатор пользователя
     * @return Айди пользователя
     */
    @Override
    public int getId()
    {
        return id;
    }

    /**
     * Возвращает имя пользователя
     * @return Имя пользователя
     */
    @Override
    public String getUserName()
    {
        return userName;
    }

    /**
     * Проверяет, находится ли пользователь онлайн
     * @return True если пользователь онлайн, false если нет
     */
    @Override
    public boolean isOnline()
    {
        return online;
    }

    public void setIsOnline(boolean value)
    {
        online = value;
    }

    @Override
    public int hashCode()
    {
        return id;
    }

    @Override
    public boolean equals(Object another)
    {
        if (another instanceof UserImpl) {
            return ((UserImpl) another).id == id;
        }
        return false;
    }

    @Override
    public String toString()
    {
        if (id <= 0) {
            return userName;
        }
        return userName + "[" + (online ? "online" : "offline") + "]";
    }
}
