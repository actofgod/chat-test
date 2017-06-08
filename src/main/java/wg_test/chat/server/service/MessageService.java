package wg_test.chat.server.service;

import wg_test.chat.entity.User;
import wg_test.chat.server.ServiceLocator;
import wg_test.chat.server.di.Component;
import wg_test.chat.server.di.ComponentInitializationException;
import wg_test.chat.server.entity.OnlineUser;
import wg_test.chat.server.message.DatabaseMapper;
import wg_test.chat.server.message.PrivateMessage;
import wg_test.chat.server.message.PublicMessage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class MessageService extends Component
{
    /**
     * Маппер получения и редактирования информации о сообщениях в базе данных
     */
    private DatabaseMapper databaseMapper;

    public MessageService(ServiceLocator locator)
    {
        super(locator);
    }

    /**
     * Возвращает инстанс сообщения в общем чатике по айди сообщения
     * @param messageId Айди сообщения
     * @return Инстанс сообщения или null если указанное сообщение не было найдено
     */
    public PublicMessage getPublicMessageById(int messageId)
    {
        PublicMessage result = null;
        try {
            result = databaseMapper.findPublicMessageById(messageId);
        } catch (SQLException e) {
            Logger.getGlobal().warning("Failed to fetch public message#" + messageId + ": " + e.getMessage());
        }
        return result;
    }

    /**
     * Возвращает инстанс сообщения в приватном чатике по айди сообщения
     * @param messageId Айди сообщения
     * @return Инстанс сообщения или null если указанное сообщение не было найдено
     */
    public PrivateMessage getPrivateMessageById(int messageId)
    {
        PrivateMessage result = null;
        try {
            result = databaseMapper.findPrivateMessageById(messageId);
        } catch (SQLException e) {
            Logger.getGlobal().warning("Failed to fetch private message#" + messageId + ": " + e.getMessage());
        }
        return result;
    }

    /**
     * Редактирует информацию о сообщении в общем чатике
     * @param message Инстанс редактируемого сообщения
     * @param newMessageValue Новый текст сообщения
     * @return True если сообщение было отредактировано и оно действительно изменилось, false если произошла ошибка или
     * если текст сообшения не изменился
     */
    public boolean updatePublicMessage(PublicMessage message, String newMessageValue)
    {
        boolean result = false;
        try {
            result = databaseMapper.updatePublicMessage(message, newMessageValue);
        } catch (SQLException e) {
            Logger.getGlobal().warning("Failed to update public message#" + message.getId() + ": " + e.getMessage());
        }
        return result;
    }

    /**
     * Изменяет информацию о сообщении в приватном чатике
     * @param message Инстанс редактируемого сообщения
     * @param newMessageValue Новый текст сообщения
     * @return True если сообщение было отредактировано и оно действительно изменилось, false если произошла ошибка или
     * если текст сообшения не изменился
     */
    public boolean updatePrivateMessage(PrivateMessage message, String newMessageValue)
    {
        boolean result = false;
        try {
            result = databaseMapper.updatePrivateMessage(message, newMessageValue);
        } catch (SQLException e) {
            Logger.getGlobal().warning("Failed to update private message#" + message.getId() + ": " + e.getMessage());
        }
        return result;
    }

    /**
     * Создаёт новое сообщение в общем чатике, сохраняет его в базе данных и возвращает созданное сообщение
     * @param user Пользователь, создающий сообщение
     * @param message Текст сообщения
     * @return Инстанс созданного сообщения или null если что-то пошло не так
     */
    public PublicMessage createPublicMessage(OnlineUser user, String message)
    {
        PublicMessage result = null;
        try {
            result = databaseMapper.insertPublic(user, message);
        } catch (SQLException e) {
            Logger.getGlobal().warning("Failed to createUser new message: " + e.getMessage());
        }
        return result;
    }

    /**
     * Создаёт новое приватное сообщение, сохраняет его в базе данных и возвращает созданное сообщение
     * @param user Инстанс пользователя, создающего сообщение
     * @param recipient Инстанс пользователя, которому сообщение адресовано
     * @param message Текст сообщения
     * @return Инстанс созданного сообщения или null если что-то пошло не так
     */
    public PrivateMessage createPrivateMessage(OnlineUser user, User recipient, String message)
    {
        PrivateMessage result = null;

        try {
            result = databaseMapper.insertPrivate(user, recipient, message);
        } catch (SQLException e) {
            Logger.getGlobal().warning("Failed to createUser new message: " + e.getMessage());
        }
        return result;
    }

    /**
     * Возвращает список сообщений в общем чате начиная с указанной даты. Сообщения возвращаются отсортированными в
     * обратном порядке по дате создания начиная с указанной даты и в сторону более старых сообщений.
     *
     * @param horizon Таймштамп в милисекундах, начиная с которого возвращаются сообщения
     * @param limit Максимальное количество возвращаемых сообщений
     * @return Список сообщений в общем чате
     */
    public Iterable<PublicMessage> getPublicMessages(long horizon, int limit)
    {
        Iterable<PublicMessage> result;
        try {
            result = databaseMapper.findPublicMessagesFrom(horizon, limit);
        } catch (SQLException e) {
            Logger.getGlobal().warning("Failed to createUser new message: " + e.getMessage());
            result = new ArrayList<>(0);
        }
        return result;
    }

    /**
     * Возвращает список сообщений в приватном чате начиная с указанной даты. Сообщения возвращаются отсортированными в
     * обратном порядке по дате создания начиная с указанной даты и в сторону более старых сообщений.
     * @param currentUser Пользователь для которого грузится история
     * @param user Пользователь с которым общался currentUser
     * param horizon Таймштамп в милисекундах, начиная с которого возвращаются сообщения
     * @param limit Максимальное количество возвращаемых сообщений
     * @return Список сообщений в приватном чате
     */
    public Iterable<PrivateMessage> getPrivateMessages(User currentUser, User user, long horizon, int limit)
    {
        Iterable<PrivateMessage> result;
        try {
            result = databaseMapper.findPrivateMessagesFrom(currentUser, user, horizon, limit);
        } catch (SQLException e) {
            Logger.getGlobal().warning("Failed to createUser new message: " + e.getMessage());
            result = new ArrayList<>(0);
        }
        return result;
    }

    /**
     * Удаляет указанное сообщение из общего чатика
     * @param message Удаляемое сообщение
     * @return True если сообщение было удалено, false если произошла ошибка или сообщения и так не было
     */
    public boolean deletePublicMessage(PublicMessage message)
    {
        boolean result = false;
        try {
            result = databaseMapper.deletePublicMessage(message);
        } catch (SQLException e) {
            Logger.getGlobal().warning("Failed to delete public message#" + message.getId() + ": " + e.getMessage());
        }
        return result;
    }

    /**
     * Удаляет указанное сообщение из приватного чатика
     * @param message Удаляемое сообщение
     * @return True если сообщение было удалено, false если произошла ошибка или сообщения и так не было
     */
    public boolean deletePrivateMessage(PublicMessage message)
    {
        boolean result = false;
        try {
            result = databaseMapper.deletePrivateMessage(message);
        } catch (SQLException e) {
            Logger.getGlobal().warning("Failed to delete private message#" + message.getId() + ": " + e.getMessage());
        }
        return result;
    }

    /**
     * Инициализирует текущий сервис: устанавливает маппер данных
     * @throws ComponentInitializationException Генерируется если при инициализации что-то пошло не так
     */
    protected void init() throws ComponentInitializationException
    {
        databaseMapper = new DatabaseMapper(getContainer().getDatabaseConnectionPool());
    }

    protected ServiceLocator getContainer()
    {
        return (ServiceLocator) super.getContainer();
    }
}
