package wg_test.chat.server.service;

import wg_test.chat.proto.Response;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Класс очереди сообщений для отправки их в бэкграунде
 */
public class MessageQueueService
{
    /**
     * Класс объекта сообщения с адресатами
     */
    public class QueueEntry
    {
        /**
         * Список получателей сообщения или null если получатели - все активные пользователи
         */
        private int[] recipients;

        /**
         * Инстанс отправляемого сообщения
         */
        private Response.ServerMessage message;

        /**
         * Конструктор, создаёт сообщение для рассылки всем активным пользователям
         * @param message Инстанс отправляемого сообщения
         */
        public QueueEntry(Response.ServerMessage message)
        {
            this.message = message;
            recipients = null;
        }

        /**
         * Конструктор, создаёт сообщение для отправки одному пользователю
         * @param message Инстанс отправляемого сообщения
         * @param recipientId Айди получателя сообщения
         */
        public QueueEntry(Response.ServerMessage message, int recipientId)
        {
            this.message = message;
            recipients = new int[1];
            recipients[0] = recipientId;
        }

        /**
         * Конструктор, создаёт сообщение для отправки двум пользователям
         * @param message Инстанс отправляемого сообщения
         * @param r1 Айди получателя сообщения
         * @param r2 Айди получателя сообщения
         */
        public QueueEntry(Response.ServerMessage message, int r1, int r2)
        {
            this.message = message;
            recipients = new int[2];
            recipients[0] = r1;
            recipients[1] = r2;
        }

        /**
         * Возвращает инстанс отправляемого сообщения
         * @return Инстанс сообщения
         */
        public Response.ServerMessage getMessage()
        {
            return message;
        }

        /**
         * Проверяет, пуст ли список получателей
         * @return True если список получателей пуст, false если нет
         */
        public boolean hasRecipients()
        {
            return recipients != null;
        }

        /**
         * Возвращает список получателей сообщения
         * @return Список получателей сообщения
         */
        public int[] getRecipients()
        {
            return recipients;
        }
    }

    /**
     * Очередь сообщений
     */
    private ConcurrentLinkedQueue<QueueEntry> messageQueue;

    /**
     * Конструктор, инициализирует очередь сообщений
     */
    public MessageQueueService()
    {
        messageQueue = new ConcurrentLinkedQueue<>();
    }

    /**
     * Добавляет в очередь новое сообщение для рассылки всем активным пользователям
     * @param message Инстанс рассылаемого сообщения
     */
    public void enqueueMessage(Response.ServerMessage message)
    {
        messageQueue.add(new QueueEntry(message));
    }

    /**
     * Добавляет в очередь новое сообщение для отправки одному пользователю
     * @param message Инстанс отправляемого сообщения
     * @param recipientId Айди получателя сообщения
     */
    public void enqueueMessage(Response.ServerMessage message, int recipientId)
    {
        messageQueue.add(new QueueEntry(message, recipientId));
    }

    /**
     * Добавляет в очередь новое сообщение для отправки двум пользователям
     * @param message Инстанс отправляемого сообщения
     * @param r1 Айди получателя сообщения
     * @param r2 Айди получателя сообщения
     */
    public void enqueueMessage(Response.ServerMessage message, int r1, int r2)
    {
        messageQueue.add(new QueueEntry(message, r1, r2));
    }

    /**
     * Возвращает сообщение из очереди
     * @return Сообщение для отправки из очереди сообщений
     */
    public QueueEntry dequeueMessage()
    {
        return messageQueue.poll();
    }
}
