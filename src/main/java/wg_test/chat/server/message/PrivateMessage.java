package wg_test.chat.server.message;

import java.util.Date;

/**
 * Класс приватного сообщения
 */
public class PrivateMessage extends PublicMessage
{
    /**
     * Айди сообщения
     */
    private int toId;

    /**
     * Дата просмотра сообщения получателем, или null если пользователь его не видел
     */
    private Date dateView;

    /**
     * @param id Айди сообщения
     * @param fromId Айди отправителя
     * @param toId Айди получателя
     * @param message Текст сообщения
     * @param dateCreate Дата создания сообщения
     */
    public PrivateMessage(int id, int fromId, int toId, String message, Date dateCreate)
    {
        super(id, fromId, message, dateCreate);
        this.toId = toId;
    }

    /**
     * Возвращает айди получателя сообщения
     * @return Айди получателя сообщения
     */
    public int getToId()
    {
        return toId;
    }

    /**
     * Возвращает дату просмотра сообщения получателем
     * @return Дата просмотра сообщения получателем или null если он его ещё не видел
     */
    public Date getDateView()
    {
        return dateView;
    }

    /**
     * Устанавливает дату просмотра сообщения получателем
     * @param value Дата просмотра сообщения получателем
     * @return Инстанс текущего объекта приватного сообщения
     */
    public PrivateMessage setDateView(Date value)
    {
        dateView = value;
        return this;
    }
}
