package wg_test.chat.server.user;

import javax.sql.DataSource;
import java.sql.*;
import java.util.LinkedList;

public class DatabaseMapper
{
    final static private String SELECT_ALL_USERS_QUERY = "SELECT id, username, password_hash," +
            " password_salt FROM users WHERE id > 0";

    final static private String SELECT_USER_ID_BY_NAME_QUERY = "SELECT id FROM users WHERE LOWER(username) = ?";

    final static private String INSERT_USER_SQL = "INSERT INTO users(username, password_hash, password_salt)" +
            " VALUES (?,?,?)";

    private DataSource connectionPool;

    public DatabaseMapper(DataSource connectionPool)
    {
        this.connectionPool = connectionPool;
    }

    public Iterable<UserImpl> findAllUsers() throws SQLException
    {
        LinkedList<UserImpl> result = new LinkedList<>();

        Connection connection = connectionPool.getConnection();
        Statement stmt = connection.createStatement();

        ResultSet rs = stmt.executeQuery(SELECT_ALL_USERS_QUERY);
        while (rs.next()) {
            result.add(factoryUser(rs));
        }
        rs.close();
        stmt.close();
        connection.close();

        return result;
    }

    public int findUserIdByName(String userName) throws SQLException
    {
        int result = 0;
        Connection connection = connectionPool.getConnection();
        PreparedStatement stmt;

        stmt = connection.prepareStatement(SELECT_USER_ID_BY_NAME_QUERY);
        stmt.setString(1, userName.toLowerCase());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            result = rs.getInt(1);
        }
        rs.close();
        stmt.close();
        connection.close();

        return result;
    }

    public UserImpl insert(String userName, String passwordHash, String passwordSalt) throws SQLException
    {
        UserImpl result = null;
        Connection connection = connectionPool.getConnection();
        PreparedStatement stmt;

        stmt = connection.prepareStatement(INSERT_USER_SQL);
        stmt.setString(1, userName);
        stmt.setString(2, passwordHash);
        stmt.setString(3, passwordSalt);
        if (stmt.executeUpdate() > 0) {
            stmt.close();
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT CURRVAL('users_ids')");
            rs.next();
            result = new UserImpl(rs.getInt(1), userName, passwordHash, passwordSalt);
            rs.close();
            s.close();
        } else {
            stmt.close();
        }
        return result;
    }

    private UserImpl factoryUser(ResultSet resultSet) throws SQLException
    {
        return
            new UserImpl(
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4)
            );
    }
}
