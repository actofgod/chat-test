package wg_test.chat.server.config;

public class DatabaseConfig
{
    private String driver;
    private String driverName;
    private String hostName;
    private short port;
    private String database;
    private String userName;
    private String password;

    public DatabaseConfig()
    {
        driver = "postgresql";
        driverName = "org.postgresql.Driver";
        hostName = "localhost";
        port = 5432;
        database = "chat";
        userName = "nyaah";
        password = "pEjtu6SmyRqhGu2";
    }

    public String getHostName()
    {
        return hostName;
    }

    public DatabaseConfig setHostName(String value) throws InvalidPropertyValueException
    {
        String val = value.trim();
        if (val.isEmpty()) {
            throw new InvalidPropertyValueException("db.hostName", value);
        }
        hostName = val;
        return this;
    }

    public Short getPort()
    {
        return port;
    }

    public DatabaseConfig setPort(int value) throws InvalidPropertyValueException
    {
        if (value <= 0 || value >= Short.MAX_VALUE) {
            throw new InvalidPropertyValueException("db.port", Integer.toString(value));
        }
        port = (short)value;
        return this;
    }

    public String getDatabase()
    {
        return database;
    }

    public DatabaseConfig setDatabase(String value) throws InvalidPropertyValueException
    {
        String val = value.trim();
        if (val.isEmpty()) {
            throw new InvalidPropertyValueException("db.database", value);
        }
        database = val;
        return this;
    }

    public String getUserName()
    {
        return userName;
    }

    public DatabaseConfig setUserName(String value) throws InvalidPropertyValueException
    {
        String val = value.trim();
        if (val.isEmpty()) {
            throw new InvalidPropertyValueException("db.username", value);
        }
        userName = val;
        return this;
    }

    public String getPassword()
    {
        return password;
    }

    public DatabaseConfig setPassword(String value) throws InvalidPropertyValueException
    {
        String val = value.trim();
        if (val.isEmpty()) {
            throw new InvalidPropertyValueException("db.password", value);
        }
        password = val;
        return this;
    }

    public DatabaseConfig setDriver(String value) throws InvalidPropertyValueException
    {
        String val = value.trim();
        if (val.isEmpty()) {
            throw new InvalidPropertyValueException("db.driver", value);
        }
        driver = val;
        return this;
    }

    public DatabaseConfig setDriverClassName(String value) throws InvalidPropertyValueException
    {
        String val = value.trim();
        if (val.isEmpty()) {
            throw new InvalidPropertyValueException("db.driverClassName", value);
        }
        driverName = val;
        return this;
    }

    public String getConnectionString()
    {
        return "jdbc:" + this.driver + "://" + hostName + ":" + ((int)port) + "/" + database;
    }

    public String getDriverClassName()
    {
        return driverName;
    }
}
