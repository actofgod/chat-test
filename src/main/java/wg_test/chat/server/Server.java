package wg_test.chat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.log4j.PropertyConfigurator;
import wg_test.chat.server.config.ServerConfig;
import wg_test.chat.server.config.InvalidPropertyValueException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Класс сервера чата
 */
public class Server
{
    /**
     * Конфиг сервера
     */
    final private ServerConfig config;

    /**
     * Сервис локатор, DI контейнер
     */
    private ServiceLocator serviceLocator;

    public Server()
    {
        config = new ServerConfig();
    }

    /**
     * Возвращает объект конфига сервера
     * @return Инстанс конфига сервера
     */
    public ServerConfig getConfig()
    {
        return config;
    }

    /**
     * Возвращает сервис локатор
     * @return Инстанс сервис локатора
     */
    public ServiceLocator getServiceLocator()
    {
        if (serviceLocator == null) {
            serviceLocator = new ServiceLocator(this);
        }
        return serviceLocator;
    }

    /**
     * Инициализирует конфиг сервера
     * @return Инстанс текущего сервера
     * @throws InvalidPropertyValueException
     */
    private Server configure() throws InvalidPropertyValueException
    {
        PropertyConfigurator.configure("resources/config/log4j.properties");

        Properties props = new Properties();
        File configFile = new File("resources/config/server.xml");
        if (configFile.exists()) {
            try {
                InputStream configStream = new FileInputStream(configFile);
                props.loadFromXML(configStream);
                configStream.close();
            } catch (IOException e) {
                Logger.getGlobal().warning("Config file read error: " + e.getMessage());
            }
        }
        config.setHostName(props.getProperty("hostname", "127.0.0.1"))
            .setPort(Integer.parseInt(props.getProperty("port", "27984")))
            .setWorkersCount(Integer.parseInt(props.getProperty("workersCount", "2")))
            .setPasswordAlgorithm(props.getProperty("passwordAlgorithm", "SHA-256"))
            .setPasswordSaltLength(Integer.parseInt(props.getProperty("passwordSaltLength", "16")));

        config.getDatabaseConfig().setHostName(props.getProperty("db.hostname", "localhost"))
            .setPort(Integer.parseInt(props.getProperty("db.port", "5432")))
            .setDriverClassName(props.getProperty("db.driverName", "org.postgresql.Driver"))
            .setDriver(props.getProperty("db.driver", "postgresql"))
            .setUserName(props.getProperty("db.username", "root"))
            .setPassword(props.getProperty("db.password", "secret-password"))
            .setDatabase(props.getProperty("db.database", "chat"));

        config.getRedisConfig()
                .setHostName(props.getProperty("redis.hostname", "localhost"))
                .setPort(Integer.parseInt(props.getProperty("redis.port", "6379")))
                .setDatabase(Integer.parseInt(props.getProperty("redis.database", "6379")));

        return this;
    }

    private void run()
    {
        ExecutorService bg = Executors.newCachedThreadPool();
        for (int i = 0; i < 5; ++i) {
            bg.execute(new BackgroundSendWorker(getServiceLocator()));
        }

        // Create event loop groups. One for incoming connections handling and
        // second for handling actual event by workers
        EventLoopGroup serverGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(config.getWorkersCount());

        try {
            ServerBootstrap bootStrap = new ServerBootstrap();
            bootStrap.group(serverGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ServerChannelInitializer(this));

            // Bind to port
            bootStrap.bind(config.getHostName(), config.getPort()).sync().channel().closeFuture().sync();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            serverGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args)
    {
        Server server = new Server();

        try {
            server.configure().run();
        } catch (InvalidPropertyValueException e) {
            System.err.println("Invalid server configuration");
            System.err.println(e.getMessage());
        }
    }
}
