package wg_test.chat.server.config;

import com.lambdaworks.redis.RedisURI;

public class RedisConfig
{
    private String hostName;
    private short port;
    private byte database;

    public RedisConfig()
    {
        hostName = "localhost";
        port = 6379;
        database = 0;
    }

    public String getHostName()
    {
        return hostName;
    }

    public RedisConfig setHostName(String value) throws InvalidPropertyValueException
    {
        String val = value.trim();
        if (val.isEmpty()) {
            throw new InvalidPropertyValueException("redis.hostName", value);
        }
        hostName = val;
        return this;
    }

    public short getPort()
    {
        return port;
    }

    public RedisConfig setPort(int value) throws InvalidPropertyValueException
    {
        if (value <= 0 || value >= Short.MAX_VALUE) {
            throw new InvalidPropertyValueException("redis.port", Integer.toString(value));
        }
        port = (short)value;
        return this;
    }

    public byte getDatabase()
    {
        return database;
    }

    public RedisConfig setDatabase(int value) throws InvalidPropertyValueException
    {
        if (value < 0 || value >= Byte.MAX_VALUE) {
            throw new InvalidPropertyValueException("redis.database", Integer.toString(value));
        }
        database = (byte)value;
        return this;
    }

    public RedisURI getUri()
    {
        return RedisURI.Builder
                .redis(hostName)
                .withPort(port)
                .withDatabase(database)
                .build();
    }
}
