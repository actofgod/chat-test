package wg_test.chat.server.message;

import wg_test.chat.entity.User;

import javax.sql.DataSource;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.logging.Logger;

/**
 * Класс маппера сообщений в чатиках в БД
 */
public class DatabaseMapper
{
    /**
     * SQL запрос для получения последнего сгенерированного айди сообщения
     */
    final static private String SELECT_LAST_INSERT_ID = "SELECT CURRVAL('chat_messages_ids')";

    /**
     * SQL запрос для получения публичного сообщения по его айди
     */
    final static private String SELECT_PUBLIC_MESSAGE_BY_ID = "SELECT id, from_id, message, date, last_edit " +
            "FROM chat_public_messages WHERE id=?";

    /**
     * SQL запрос для получения списка публичных сообщений
     */
    final static private String SELECT_PUBLIC_MESSAGES = "SELECT id, from_id, message, date, last_edit " +
            "FROM chat_public_messages WHERE date<=? ORDER BY date DESC LIMIT ?";

    /**
     * SQL запрос для вставки сообщения в общем чате
     */
    final static private String INSERT_PUBLIC_MESSAGE = "INSERT INTO chat_public_messages(from_id, message) " +
            "VALUES (?, ?)";

    /**
     * SQL запрос для обновления сообщения в общем чате
     */
    final static private String UPDATE_PUBLIC_MESSAGE = "UPDATE chat_public_messages " +
            "SET message = ?, last_edit = ? WHERE id = ?";

    /**
     * SQL запрос для удаления сообщения в общем чате
     */
    final static private String DELETE_PUBLIC_MESSAGE = "DELETE FROM chat_public_messages WHERE id = ?";

    /**
     * SQL запрос для получения приватного сообщения по его айди
     */
    final static private String SELECT_PRIVATE_MESSAGE_BY_ID = "SELECT id, from_id, to_id, message, date, " +
            "last_edit, date_view FROM chat_private_messages WHERE id=?";

    /**
     * SQL запрос для получения списка приватных сообщений
     */
    final static private String SELECT_PRIVATE_MESSAGES = "SELECT id, from_id, to_id, message, date, " +
            "last_edit, date_view FROM chat_private_messages " +
            "WHERE date<=? AND ((from_id=? AND to_id=?) OR (from_id=? AND to_id=?)) ORDER BY date DESC LIMIT ?";

    /**
     * SQL запрос для вставки приватного сообщения
     */
    final static private String INSERT_PRIVATE_MESSAGE = "INSERT INTO chat_private_messages(from_id, to_id, message)" +
            " VALUES (?, ?, ?)";

    /**
     * SQL запрос для обновления приватного сообщения
     */
    final static private String UPDATE_PRIVATE_MESSAGE = "UPDATE chat_private_messages " +
            "SET message = ?, last_edit = ? WHERE id = ?";

    /**
     * SQL запрос для удаления приватного сообщения
     */
    final static private String DELETE_PRIVATE_MESSAGE = "DELETE FROM chat_private_messages WHERE id = ?";

    /**
     * Пул используемых соединений с базой данных
     */
    private DataSource connectionPool;

    /**
     * @param connectionPool Пул используемых соединений с базой данных
     */
    public DatabaseMapper(DataSource connectionPool)
    {
        this.connectionPool = connectionPool;
    }

    /**
     * Ищет в базе данных и возвращает публичное сообщение
     * @param messageId Айди сообщения
     * @return Инстанс публичного сообщения или null если сообщение не найдено
     * @throws SQLException Генерируется если при работе с базой даннх произошла ошибка
     */
    public PublicMessage findPublicMessageById(int messageId) throws SQLException
    {
        PublicMessage message = null;
        Connection connection = connectionPool.getConnection();
        PreparedStatement stmt;

        stmt = connection.prepareStatement(SELECT_PUBLIC_MESSAGE_BY_ID);
        stmt.setInt(1, messageId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            message = factoryPublicMessage(rs);
        }
        rs.close();
        stmt.close();
        connection.close();
        return message;
    }

    /**
     * Выгребает из базы данных список публичных сообщений начиная с указанной даты в порядке удаления в прошлое
     * @param horizon Таймштамп с которого вытаскиваются данные
     * @param limit Максимальное количество возвращаемых строк
     * @return Список публичных сообщений
     * @throws SQLException Генерируется если при работе с базой даннх произошла ошибка
     */
    public Iterable<PublicMessage> findPublicMessagesFrom(long horizon, int limit) throws SQLException
    {
        ArrayList<PublicMessage> result = new ArrayList<>(limit);
        Connection connection = connectionPool.getConnection();
        PreparedStatement stmt;

        Logger log = Logger.getGlobal();
        stmt = connection.prepareStatement(SELECT_PUBLIC_MESSAGES);
        stmt.setTimestamp(1, new Timestamp(horizon));
        stmt.setInt(2, limit);
        log.info("Select public messages from " + (new Date(horizon)).toString());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            result.add(factoryPublicMessage(rs));
        }
        rs.close();
        stmt.close();
        connection.close();
        return result;
    }

    /**
     * Добавляет в базу данных новое публичное сообщение
     * @param user Инстанс пользователя, создавшего сообщение
     * @param message Текст сообщения
     * @return Инстанс созданного сообщения
     * @throws SQLException Генерируется если при работе с базой даннх произошла ошибка
     */
    public PublicMessage insertPublic(User user, String message) throws SQLException
    {
        int result = -1;
        Connection connection = connectionPool.getConnection();
        PreparedStatement stmt;

        stmt = connection.prepareStatement(INSERT_PUBLIC_MESSAGE);
        stmt.setInt(1, user.getId());
        stmt.setString(2, message);
        if (stmt.executeUpdate() > 0) {
            stmt.close();
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery(SELECT_LAST_INSERT_ID);
            rs.next();
            result = rs.getInt(1);
            rs.close();
            s.close();
        } else {
            stmt.close();
        }
        connection.close();
        return result > 0 ? findPublicMessageById(result) : null;
    }

    /**
     * Обновляет текст сообщения
     * @param message Инстанс редактируемого сообщения
     * @param newMessageValue Новый текст сообщения
     * @return True если сообщение было обновлено, false если текст сообщения не изменился
     * @throws SQLException Генерируется если при работе с базой даннх произошла ошибка
     */
    public boolean updatePublicMessage(PublicMessage message, String newMessageValue) throws SQLException
    {
        Connection connection = connectionPool.getConnection();
        PreparedStatement stmt;
        boolean result;
        Timestamp lastUpdate;

        stmt = connection.prepareStatement(UPDATE_PUBLIC_MESSAGE);
        stmt.setString(1, newMessageValue);
        lastUpdate = new Timestamp(System.currentTimeMillis());
        stmt.setTimestamp(2, lastUpdate);
        stmt.setInt(3, message.getId());
        result = (stmt.executeUpdate() > 0);
        stmt.close();
        connection.close();
        if (result) {
            message.setLastUpdate(lastUpdate);
            message.setMessage(newMessageValue);
        }
        return result;
    }

    /**
     * Удаляет публичное сообщение
     * @param message Удаляемое сообщение
     * @return True если сообщение было удалено, false если сообщения и так не было в базе данных
     * @throws SQLException Генерируется если при работе с базой даннх произошла ошибка
     */
    public boolean deletePublicMessage(PublicMessage message) throws SQLException
    {
        Connection connection = connectionPool.getConnection();
        PreparedStatement stmt;
        boolean result;

        stmt = connection.prepareStatement(DELETE_PUBLIC_MESSAGE);
        stmt.setInt(1, message.getId());
        result = (stmt.executeUpdate() > 0);
        stmt.close();
        connection.close();
        return result;
    }

    /**
     * Ищет в базе данных и возращает приватное сообщение по его айди
     * @param messageId Айди сообщения
     * @return Инстанс найденного сообщения или null если сообщение не найдено
     * @throws SQLException Генерируется если при работе с базой даннх произошла ошибка
     */
    public PrivateMessage findPrivateMessageById(int messageId) throws SQLException
    {
        PrivateMessage message = null;
        Connection connection = connectionPool.getConnection();
        PreparedStatement stmt;

        stmt = connection.prepareStatement(SELECT_PRIVATE_MESSAGE_BY_ID);
        stmt.setInt(1, messageId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            message = factoryPrivateMessage(rs);
        }
        rs.close();
        stmt.close();
        connection.close();
        return message;
    }

    /**
     * Возвращает список приватныз сообщений между двумя пользователями
     * @param currentUser Инстанс пользователя
     * @param user Инстанс второго пользователя
     * @param horizon Таймштамп с которого вытаскиваются данные
     * @param limit Максимальное количество возвращаемых строк
     * @return Список приватных сообщений между пользователями
     * @throws SQLException Генерируется если при работе с базой даннх произошла ошибка
     */
    public Iterable<PrivateMessage> findPrivateMessagesFrom(User currentUser, User user, long horizon, int limit)
            throws SQLException {

        ArrayList<PrivateMessage> result = new ArrayList<>(limit);
        Connection connection = connectionPool.getConnection();
        PreparedStatement stmt;

        stmt = connection.prepareStatement(SELECT_PRIVATE_MESSAGES);
        stmt.setTimestamp(1, new Timestamp(horizon));
        stmt.setInt(2, currentUser.getId());
        stmt.setInt(3, user.getId());
        stmt.setInt(4, user.getId());
        stmt.setInt(5, currentUser.getId());
        stmt.setInt(6, limit);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            result.add(factoryPrivateMessage(rs));
        }
        rs.close();
        stmt.close();
        connection.close();
        return result;
    }

    /**
     * Вставляет новое приватное сообщение в базу данных и возвращает новый инстанс этого сообщения
     * @param user Инстанс пользователя, отправившего сообщение
     * @param recipient Инстанс получателя сообщения
     * @param message Текст сообщения
     * @return Инстанс созданного сообщения
     * @throws SQLException Генерируется если при работе с базой даннх произошла ошибка
     */
    public PrivateMessage insertPrivate(User user, User recipient, String message) throws SQLException
    {
        int result = -1;
        Connection connection = connectionPool.getConnection();
        PreparedStatement stmt;

        stmt = connection.prepareStatement(INSERT_PRIVATE_MESSAGE);
        stmt.setInt(1, user.getId());
        stmt.setInt(2, recipient.getId());
        stmt.setString(3, message);
        if (stmt.executeUpdate() > 0) {
            stmt.close();
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery(SELECT_LAST_INSERT_ID);
            rs.next();
            result = rs.getInt(1);
            rs.close();
            s.close();
        } else {
            stmt.close();
        }
        connection.close();
        return result > 0 ? findPrivateMessageById(result) : null;
    }

    /**
     * Обновялет приватное сообщение
     * @param message Инстанс редактируемого сообщения
     * @param newMessageValue Новый текст сообщения
     * @return True если сообщение было обновлено, false если текст сообщения не изменился
     * @throws SQLException Генерируется если при работе с базой даннх произошла ошибка
     */
    public boolean updatePrivateMessage(PrivateMessage message, String newMessageValue) throws SQLException
    {
        Connection connection = connectionPool.getConnection();
        PreparedStatement stmt;
        boolean result;
        Timestamp lastUpdate;

        stmt = connection.prepareStatement(UPDATE_PRIVATE_MESSAGE);
        stmt.setString(1, newMessageValue);
        lastUpdate = new Timestamp(System.currentTimeMillis());
        stmt.setTimestamp(2, lastUpdate);
        stmt.setInt(3, message.getId());
        result = (stmt.executeUpdate() > 0);
        stmt.close();
        connection.close();
        if (result) {
            message.setLastUpdate(lastUpdate);
            message.setMessage(newMessageValue);
        }
        return result;
    }

    /**
     * Удаляет приватное сообщение
     * @param message Инстанс удаляемого сообщения
     * @return True если сообщение было удалено, false если его и так не было в базе данных
     * @throws SQLException Генерируется если при работе с базой даннх произошла ошибка
     */
    public boolean deletePrivateMessage(PublicMessage message) throws SQLException
    {
        Connection connection = connectionPool.getConnection();
        PreparedStatement stmt;
        boolean result;

        stmt = connection.prepareStatement(DELETE_PRIVATE_MESSAGE);
        stmt.setInt(1, message.getId());
        result = (stmt.executeUpdate() > 0);
        stmt.close();
        connection.close();
        return result;
    }

    /**
     * Фабличный метод создания публичного сообщения из записи, полученной из базы данных
     * @param resultSet Результат, полученный из базы данных
     * @return Инстанс созданного сообщения
     * @throws SQLException Генерируется если при работе с результатом выборки произошла ошибка
     */
    private PublicMessage factoryPublicMessage(ResultSet resultSet) throws SQLException
    {
        PublicMessage message = new PublicMessage(
            resultSet.getInt(1),
            resultSet.getInt(2),
            resultSet.getString(3),
            resultSet.getTimestamp(4)
        );
        Timestamp lastUpdate = resultSet.getTimestamp(5);
        if (!resultSet.wasNull()) {
            message.setLastUpdate(lastUpdate);
        }
        return message;
    }

    /**
     * Фабличный метод создания приватного сообщения из записи, полученной из базы данных
     * @param resultSet Результат, полученный из базы данных
     * @return Инстанс созданного сообщения
     * @throws SQLException Генерируется если при работе с результатом выборки произошла ошибка
     */
    private PrivateMessage factoryPrivateMessage(ResultSet resultSet) throws SQLException
    {
        PrivateMessage message = new PrivateMessage(
                resultSet.getInt(1),
                resultSet.getInt(2),
                resultSet.getInt(3),
                resultSet.getString(4),
                resultSet.getTimestamp(5)
        );
        Timestamp date = resultSet.getTimestamp(6);
        if (!resultSet.wasNull()) {
            message.setLastUpdate(date);
        }
        date = resultSet.getTimestamp(7);
        if (!resultSet.wasNull()) {
            message.setDateView(date);
        }
        return message;
    }
}
