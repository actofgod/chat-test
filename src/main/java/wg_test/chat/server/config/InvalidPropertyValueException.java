package wg_test.chat.server.config;

public class InvalidPropertyValueException extends Exception
{
    private String property;
    private String value;

    public InvalidPropertyValueException(String property, String value)
    {
        super("Invalid config value '" + value + "' for property '" + property + "'");
        this.property = property;
        this.value = value;
    }

    public InvalidPropertyValueException(String property, String value, Throwable cause)
    {
        super("Invalid config value '" + value + "' for property '" + property + "'", cause);
        this.property = property;
        this.value = value;
    }

    public String getProperty()
    {
        return property;
    }

    public String getValue()
    {
        return value;
    }
}
