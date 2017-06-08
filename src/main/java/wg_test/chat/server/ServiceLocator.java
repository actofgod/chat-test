package wg_test.chat.server;

import com.lambdaworks.redis.*;
import org.apache.commons.dbcp2.BasicDataSource;
import wg_test.chat.proto.Request;
import wg_test.chat.server.config.DatabaseConfig;
import wg_test.chat.server.config.ServerConfig;
import wg_test.chat.server.requestHandler.*;
import wg_test.chat.server.di.Container;
import wg_test.chat.server.service.*;

import javax.sql.DataSource;

public class ServiceLocator extends Container
{
    private BasicDataSource dbConnectionPool;

    private Server server;
    private PasswordService passwordService;

    private TokenContextMap tokenContextMap;
    private MessageQueueService messageQueue;

    private RedisClient redisClient;
    private RedisConnection<String, String> redisConnection;

    public ServiceLocator(Server server)
    {
        this.server = server;

        dbConnectionPool = buildDatabaseConnectionPool(server.getConfig());

        registerComponent("userService", new UserService(this));
        registerComponent("authService", new AuthService(this));
        registerComponent("messageService", new MessageService(this));
        registerComponent("tokenStorage", new TokenStorage(this));

        passwordService = new PasswordService(
            server.getConfig().getPasswordAlgorithm(), server.getConfig().getPasswordSaltLength()
        );
        tokenContextMap = new TokenContextMap();

        Router router = new Router(this);
        router.addRoute(Request.ClientMessage.MessageType.AUTH, new AuthRequestHandler(this));
        router.addRoute(Request.ClientMessage.MessageType.REGISTER, new RegisterRequestHandler(this));
        router.addRoute(Request.ClientMessage.MessageType.CHECK_USER_NAME, new CheckUserNameRequestHandler(this));
        router.addRoute(Request.ClientMessage.MessageType.RECONNECT, new ReconnectRequestHandler(this));
        router.addRoute(Request.ClientMessage.MessageType.SEND_MESSAGE, new MessageSendRequestHandler(this));
        router.addRoute(Request.ClientMessage.MessageType.UPDATE_MESSAGE, new MessageUpdateRequestHandler(this));
        router.addRoute(Request.ClientMessage.MessageType.DELETE_MESSAGE, new MessageDeleteRequestHandler(this));
        router.addRoute(Request.ClientMessage.MessageType.LIST_MESSAGES, new MessageListRequestHandler(this));
        router.addRoute(Request.ClientMessage.MessageType.USER_LIST, new UserListRequestHandler(this));

        registerComponent("router", router);

        messageQueue = new MessageQueueService();

        initRedisConnection();

        initialize();
    }

    public Router getRouter()
    {
        return (Router)this.get("router");
    }

    public TokenContextMap getTokenContextMap()
    {
        return tokenContextMap;
    }

    public DataSource getDatabaseConnectionPool()
    {
        return dbConnectionPool;
    }

    public UserService getUserService()
    {
        return (UserService)this.get("userService");
    }

    public MessageService getMessageService()
    {
        return (MessageService)this.get("messageService");
    }

    public PasswordService getPasswordService()
    {
        return passwordService;
    }

    public AuthService getAuthService()
    {
        return (AuthService)this.get("authService");
    }

    public MessageQueueService getMessageQueueService()
    {
        return messageQueue;
    }

    public TokenStorage getTokenStorage()
    {
        return (TokenStorage)this.get("tokenStorage");
    }

    public RedisConnection<String, String> getRedisConnection()
    {
        return redisConnection;
    }

    private static BasicDataSource buildDatabaseConnectionPool(ServerConfig config)
    {
        DatabaseConfig dbConfig = config.getDatabaseConfig();
        BasicDataSource pool = new BasicDataSource();

        pool.setUsername(dbConfig.getUserName());
        pool.setPassword(dbConfig.getPassword());
        pool.setDriverClassName(dbConfig.getDriverClassName());
        pool.setUrl(dbConfig.getConnectionString());
        System.out.println(dbConfig.getConnectionString());

        pool.setMaxTotal(config.getWorkersCount() * 2);

        return pool;
    }

    private void initRedisConnection()
    {
        try {
            redisClient = new RedisClient(server.getConfig().getRedisConfig().getUri());
            redisConnection = redisClient.connect();
        } catch (RedisConnectionException e) {
            throw e;
        }
    }
}
