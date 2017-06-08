package wg_test.chat.server.config;

import wg_test.chat.server.ServiceLocator;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ServerConfig
{
    private String hostName;
    private short port;
    private short workersCount;
    private String passwordAlgorithm;
    private byte passwordSaltLength;

    private DatabaseConfig dbConfig;
    private RedisConfig redisConfig;

    public ServerConfig()
    {
        hostName = "127.0.0.1";
        port = 27984;
        workersCount = 2;
        passwordAlgorithm = "SHA-256";
        passwordSaltLength = (byte)16;

        dbConfig = new DatabaseConfig();
        redisConfig = new RedisConfig();
    }

    public String getHostName()
    {
        return hostName;
    }

    public ServerConfig setHostName(String value) throws InvalidPropertyValueException
    {
        String val = value.trim();
        if (val.isEmpty()) {
            throw new InvalidPropertyValueException("hostName", value);
        }
        hostName = val;
        return this;
    }

    public short getPort()
    {
        return port;
    }

    public ServerConfig setPort(int value) throws InvalidPropertyValueException
    {
        if (value <= 0 || value >= Short.MAX_VALUE) {
            throw new InvalidPropertyValueException("port", Integer.toString(value));
        }
        port = (short)value;
        return this;
    }

    public short getWorkersCount()
    {
        return workersCount;
    }

    public ServerConfig setWorkersCount(int value) throws InvalidPropertyValueException
    {
        if (value <= 0 || value >= Short.MAX_VALUE) {
            throw new InvalidPropertyValueException("workersCount", Integer.toString(value));
        }
        workersCount = (short)value;
        return this;
    }

    public DatabaseConfig getDatabaseConfig()
    {
        return dbConfig;
    }

    public RedisConfig getRedisConfig()
    {
        return redisConfig;
    }

    public String getPasswordAlgorithm()
    {
        return passwordAlgorithm;
    }

    public ServerConfig setPasswordAlgorithm(String value) throws InvalidPropertyValueException
    {
        String val = value.trim();
        if (val.isEmpty()) {
            throw new InvalidPropertyValueException("passwordAlgorithm", value);
        }
        try {
            MessageDigest.getInstance(val);
        } catch (NoSuchAlgorithmException e) {
            throw new InvalidPropertyValueException("passwordAlgorithm", value, e);
        }
        passwordAlgorithm = val;
        return this;
    }

    public byte getPasswordSaltLength()
    {
        return passwordSaltLength;
    }

    public ServerConfig setPasswordSaltLength(int value) throws InvalidPropertyValueException
    {
        if (value <= 4 || value >= Byte.MAX_VALUE) {
            throw new InvalidPropertyValueException("passwordSaltLength", Integer.toString(value));
        }
        passwordSaltLength = (byte)value;
        return this;
    }
}
