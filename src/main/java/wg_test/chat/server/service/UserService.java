package wg_test.chat.server.service;

import wg_test.chat.server.ServiceLocator;
import wg_test.chat.server.di.Component;
import wg_test.chat.server.di.ComponentInitializationException;
import wg_test.chat.server.entity.User;
import wg_test.chat.server.user.DatabaseMapper;
import wg_test.chat.server.user.UserImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class UserService extends Component
{
    /**
     * Текущее количество пользователей
     */
    private int usersCount;

    /**
     * Мапа для поиска пользователей по айди
     */
    private ConcurrentHashMap<Integer, UserImpl> usersIdMap;

    /**
     * Маппер информации о пользователях в БД
     */
    private DatabaseMapper databaseMapper;

    public UserService(ServiceLocator locator)
    {
        super(locator);
        usersIdMap = new ConcurrentHashMap<>();
    }

    /**
     * Возвращает список всех пользователей
     * @return Список пользователей
     */
    public Iterable<User> getAllUsers()
    {
        ArrayList<User> result = new ArrayList<>(usersCount);

        for (Map.Entry<Integer, UserImpl> e: usersIdMap.entrySet()) {
            result.add(e.getValue());
        }
        return result;
    }

    /**
     * Возвращает инстанс пользователя по его айди
     * @param userId Айди искомого пользователя
     * @return Инстанс пользователя или null если пользователь не найден
     */
    public User getUserById(int userId)
    {
        return usersIdMap.get(userId);
    }

    /**
     * Возвращает пользователя по его имени
     * @param userName Имя искомого пользователя
     * @return Инстанс пользователя или null если пользователь не найден
     */
    public User getUserByName(String userName)
    {
        int userId = 0;
        try {
            userId = databaseMapper.findUserIdByName(userName);
        } catch (Exception e) {
            Logger.getGlobal().info("Exception thrown: " + e.getMessage());
        }
        return getUserById(userId);
    }

    /**
     * Создайт нового пользователя
     * @param login Имя создаваемого пользователя
     * @param passwordHash Хэш пароля пользователя
     * @param passwordSalt Соль пароля пользователя
     * @return Инстанс пользователя или null если при создании произошла ошибка
     */
    public User createUser(String login, String passwordHash, String passwordSalt)
    {
        UserImpl user = null;
        try {
            user = databaseMapper.insert(login, passwordHash, passwordSalt);
            if (user != null) {
                usersIdMap.put(user.getId(), user);
                usersCount++;
            }
        } catch (Exception e) {
            Logger.getGlobal().info("Exception thrown: " + e.getMessage());
        }
        return user;
    }

    @Override
    protected ServiceLocator getContainer()
    {
        return (ServiceLocator) super.getContainer();
    }

    @Override
    protected void init() throws ComponentInitializationException
    {
        try {
            databaseMapper = new DatabaseMapper(getContainer().getDatabaseConnectionPool());
            usersCount = 0;
            for (UserImpl user: databaseMapper.findAllUsers()) {
                usersIdMap.put(user.getId(), user);
                usersCount++;
            }
        } catch (SQLException e) {
            throw new ComponentInitializationException(this, "Failed to fetch users", e);
        }
    }
}
