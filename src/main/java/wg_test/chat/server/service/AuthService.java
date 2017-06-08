package wg_test.chat.server.service;

import io.netty.channel.ChannelHandlerContext;
import wg_test.chat.server.ServiceLocator;
import wg_test.chat.server.di.Component;
import wg_test.chat.server.di.ComponentInitializationException;
import wg_test.chat.server.entity.OnlineUser;
import wg_test.chat.server.entity.User;
import wg_test.chat.server.entity.UserToken;
import wg_test.chat.server.user.OnlineUserImpl;
import wg_test.chat.server.user.UserImpl;
import wg_test.chat.server.user.UserTokenImpl;
import wg_test.chat.utils.Hex;

import java.security.SecureRandom;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class AuthService extends Component
{
    /**
     * Сервис работы с пользователями
     */
    private UserService userService;

    /**
     * Сервис для работы с паролями пользователей
     */
    private PasswordService passwordService;

    /**
     * Генератор рандомных последовательностей, нужен для генерации токена
     */
    private SecureRandom randomGenerator;

    /**
     * Список пользователей онлайн
     */
    private ConcurrentHashMap<Integer, OnlineUserImpl> onlineUsers;

    public AuthService(ServiceLocator locator)
    {
        super(locator);
        this.randomGenerator = new SecureRandom();
        this.onlineUsers = new ConcurrentHashMap<>();
    }

    /**
     * Проверяет валидность пользователя и его пароля
     * @param userName Имя пользователя
     * @param plainPassword Пароль
     * @return Инстанс пользователя, если он найден, и пароль указан верно, null если пользователя нет, или был указан
     * не верный пароль
     */
    public User checkCredentials(String userName, String plainPassword)
    {
        User user = userService.getUserByName(userName);

        if (user != null) {
            if (!passwordService.comparePassword(plainPassword, user.getPasswordHash(), user.getPasswordSalt())) {
                user = null;
            }
        }
        return user;
    }

    /**
     * Производит аутентификацию пользователя - выдаёт ему токен, помещает пользователя в список пользователей онлайн,
     * добавляет контекст соединения и токен список авторизированных токенов.
     *
     * @param user Инстанс пользователя
     * @param ctx Контекст соединения пользователя
     * @return Инстанс выданного пользователю токена или null если произошла ошибка авторизации
     */
    public UserToken authenticate(User user, ChannelHandlerContext ctx)
    {
        OnlineUserImpl onlineUser = onlineUsers.get(user.getId());
        if (onlineUser == null) {
            onlineUser = new OnlineUserImpl(user);
            onlineUsers.put(user.getId(), onlineUser);
        }
        UserTokenImpl token = createTokenForUser(onlineUser);
        try {
            onlineUser.addToken(token);
            token.setChannelHandlerContext(ctx);
            ((UserImpl)user).setOnlineUser(onlineUser);
        } catch (Exception e) {
            token = null;
        }
        return token;
    }

    /**
     * Производит повторную аутентификацию токена
     * @param tokenValue Токен в виде строки
     * @param userId Айди пользователя для которого был выдан токен
     * @param validBefore Дата до которой токен валиден
     * @param ctx Контекст соединения пользователя
     * @return Инстанс выданного пользователю токена или null если произошла ошибка авторизации
     */
    public UserToken reAuthenticate(String tokenValue, int userId, Date validBefore, ChannelHandlerContext ctx)
    {
        User user = userService.getUserById(userId);
        if (user == null) {
            return null;
        }
        OnlineUserImpl onlineUser = onlineUsers.get(user.getId());
        if (onlineUser == null) {
            onlineUser = new OnlineUserImpl(user);
            onlineUsers.put(user.getId(), onlineUser);
        }
        UserTokenImpl copy = cloneToken(onlineUser, tokenValue, validBefore);
        try {
            onlineUser.addToken(copy);
            copy.setChannelHandlerContext(ctx);
            ((UserImpl)user).setOnlineUser(onlineUser);
        } catch (Exception e) {
            copy = null;
        }
        return copy;
    }

    /**
     * Возвращает пользователя онлайн по его айди
     * @param userId Айди пользователя
     * @return Инстанс пользователя онлайн или null если пользователь не найден
     */
    public OnlineUser getUserById(int userId)
    {
        return onlineUsers.get(userId);
    }

    /**
     * Создаёт новый токен для пользователя
     * @param user Инстанс пользователя для которого создаётся токен
     * @return Инстанс выданного пользователю токена
     */
    private UserTokenImpl createTokenForUser(OnlineUserImpl user)
    {
        StringBuilder tokenBuilder = new StringBuilder();
        Date date = new Date();
        byte[] randomBytes = new byte[8];
        randomGenerator.nextBytes(randomBytes);
        tokenBuilder
            .append(user.getId()).append('-')
            .append(Hex.bytesToHex(randomBytes)).append('-')
            .append(date.getTime());
        return new UserTokenImpl(user, tokenBuilder.toString());
    }

    /**
     * Создаёт токен для пользователя из строки и даты окончания валидности токена
     * @param user Инстанс пользователя, для которого генерируется токен
     * @param tokenValue Токен в виде строки
     * @param validBefore Дата до которой токен валиден
     * @return Инстанс выданного пользователю токена
     */
    private UserTokenImpl cloneToken(OnlineUserImpl user, String tokenValue, Date validBefore)
    {
        UserTokenImpl copy = new UserTokenImpl(user, tokenValue);
        copy.setValidBefore(validBefore);
        return copy;
    }

    @Override
    protected void init() throws ComponentInitializationException
    {
        userService = getContainer().getUserService();
        passwordService = getContainer().getPasswordService();
    }

    @Override
    protected ServiceLocator getContainer()
    {
        return (ServiceLocator) super.getContainer();
    }
}
