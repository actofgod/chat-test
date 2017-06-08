package wg_test.chat.server;

import io.netty.channel.ChannelHandlerContext;
import wg_test.chat.server.entity.OnlineUser;
import wg_test.chat.server.entity.User;
import wg_test.chat.server.entity.UserToken;
import wg_test.chat.server.service.AuthService;
import wg_test.chat.server.service.MessageQueueService;
import wg_test.chat.server.service.UserService;

import java.util.logging.Logger;

/**
 * Класс объекта для рассылки сообщений из очереди сообщений пользователям
 */
public class BackgroundSendWorker implements Runnable
{
    /**
     * Очередь сообщений
     */
    private MessageQueueService queue;

    /**
     * Сервис работы с пользователями
     */
    private UserService userService;

    /**
     * Сервис авторизации, требуется для получения онлайн пользователей
     */
    private AuthService authService;

    /**
     * Карта соответствия контекстов соединений и токенов пользователй
     */
    private TokenContextMap tokenMap;

    /**
     * Флаг, сообщающий, требуется ли горшочку перестать варить
     */
    private boolean gracefulStop;

    public BackgroundSendWorker(ServiceLocator locator)
    {
        this.queue = locator.getMessageQueueService();
        this.authService = locator.getAuthService();
        this.userService = locator.getUserService();
        this.tokenMap = locator.getTokenContextMap();
        this.gracefulStop = false;
    }

    /**
     * Обрабатывает очередь сообщений, рассылая эти сообщения кому требуется
     */
    public void run()
    {
        Logger logger = Logger.getLogger(BackgroundSendWorker.class.getName());
        do {
            // получаем первое сообщение из очереди
            MessageQueueService.QueueEntry entry = queue.dequeueMessage();
            if (entry == null) {
                // если очередь пуста - вздремнём
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    gracefulStop = true;
                }
            } else {
                // если сообщение есть
                logger.info("Queue entry exists");
                if (entry.hasRecipients()) {
                    // если требуется отправить сообщение конкретным пользователям
                    int[] recipientIds = entry.getRecipients();
                    for (int i = 0; i < recipientIds.length; i++) {
                        logger.info("Send message to " + recipientIds[i]);
                        // получаем получателя
                        User user = userService.getUserById(recipientIds[i]);
                        if (user != null && user.isOnline()) {
                            // получаем онлайн пользователя
                            OnlineUser onlineUser = authService.getUserById(user.getId());
                            if (onlineUser != null) {
                                // если пользователь онлайн, пробегаемся по всем активным токенам пользователя
                                for (UserToken token : onlineUser.getActiveTokens()) {
                                    // и во все соединения пользователя с сервера отправляем сообщение
                                    logger.info("Send message to user#" + recipientIds[i] + " token " + token.getTokenValue());
                                    ChannelHandlerContext ctx = token.getChannelHandlerContext();
                                    ctx.writeAndFlush(entry.getMessage());
                                }
                            } else {
                                logger.info("User#" + recipientIds[i] + " not online");
                            }
                        } else {
                            logger.info("User#" + recipientIds[i] + " not exists or not online");
                        }
                    }
                } else {
                    // если получателей нет, то отправляем сообщение всем
                    logger.info("Broadcast message");
                    for (ChannelHandlerContext ctx: tokenMap.getContextList()) {
                        // пробегаемся по всем активным соединениям и гадим всем
                        UserToken token = tokenMap.getTokenByContext(ctx);
                        logger.info("Send message to user#" + token.getUser().getId() + " token " + token.getTokenValue());
                        ctx.writeAndFlush(entry.getMessage());
                    }
                }
            }
        } while (!gracefulStop);
    }
}
