package wg_test.chat.server.user;

import wg_test.chat.server.entity.User;
import wg_test.chat.utils.Hex;

public class UserImpl implements User
{
    /** Айди пользователя */
    final private int id;

    /** Логин пользователя */
    final private String login;

    /** Хэш пароля пользователя */
    final private byte[] passwordHash;

    /** Соль пароля пользователя */
    final private byte[] passwordSalt;

    /** Привязанный к текущему пользователю инстанс пользователя онлайн */
    private OnlineUserImpl onlineUser;

    /**
     * Использование конструктора по умолчанию запрещено
     */
    private UserImpl()
    {
        this.id = Integer.MIN_VALUE;
        this.login = null;
        this.passwordHash = null;
        this.passwordSalt = null;
        this.onlineUser = null;
        throw new RuntimeException("User default constructor not allowed");
    }

    /**
     * Конструктор, устанавливает айди и имя пользователя, их установка возможна только через конструктор
     * @param id Уникальный айди пользователя
     * @param userName Имя пользователя
     */
    public UserImpl(int id, final String userName, String passwordHash, String passwordSalt)
    {
        this.id = id;
        this.login = userName;
        this.passwordHash = Hex.hexToBytes(passwordHash);
        this.passwordSalt = Hex.hexToBytes(passwordSalt);
        this.onlineUser = null;
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
        return login;
    }

    /**
     * Возвращает хэш пароля пользователя в виде массива байтов
     * @return Хэш пароля пользователя
     */
    @Override
    public byte[] getPasswordHash()
    {
        return passwordHash;
    }

    /**
     * Возвращает соль пароля пользователя в виде массива байтов
     * @return Соль пароля пользователя
     */
    @Override
    public byte[] getPasswordSalt()
    {
        return passwordSalt;
    }

    /**
     * Проверяет, находится ли пользователь онлайн
     * @return True если пользователь онлайн, false если нет
     */
    @Override
    public boolean isOnline()
    {
        return onlineUser != null && onlineUser.isOnline();
    }

    public UserImpl setOnlineUser(OnlineUserImpl value)
    {
        if (value.getId() != id) {
            throw new RuntimeException("Invalid online user");
        }
        this.onlineUser = value;
        return this;
    }

    public OnlineUserImpl getOnlineUser()
    {
        return onlineUser;
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
        return "{User#" + id + " '" + login + "'}";
    }
}
