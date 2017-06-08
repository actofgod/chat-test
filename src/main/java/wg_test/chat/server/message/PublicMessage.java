package wg_test.chat.server.message;

import java.util.Date;

/**
 * Класс публичного сообщения
 */
public class PublicMessage
{
    /**
     * Айди сообщения
     */
    final private int id;

    /**
     * Текст сообщения
     */
    private String message;

    /**
     * Айди отпарвителя сообщения
     */
    final private int fromId;

    /**
     * Дата создания сообщения
     */
    final private Date dateCreate;

    /**
     * Дата последнего изменения сообщения или null если сообщение не менялось
     */
    private Date lastUpdate;

    /**
     * @param id Айди сообщения
     * @param fromId Айди отправителя сообщения
     * @param message Текст сообщения
     * @param dateCreate Дата создания сообщения
     */
    public PublicMessage(int id, int fromId, String message, Date dateCreate)
    {
        this.id = id;
        this.fromId = fromId;
        this.message = message;
        this.dateCreate = dateCreate;
        this.lastUpdate = null;
    }

    /**
     * Возвращает айди сообщения
     * @return Айди сообщения
     */
    public int getId()
    {
        return id;
    }

    /**
     * Возвращает айди отправителя сообщения
     * @return АЙди отправителя сообщения
     */
    public int getFromId()
    {
        return fromId;
    }

    /**
     * Возвращает текст сообщения
     * @return Текст сообщения
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Устанавливает текст сообщения
     * @param value Новый текст сообщения
     * @return Инстанс текущего объекта
     */
    public PublicMessage setMessage(String value)
    {
        message = value;
        return this;
    }

    /**
     * Возвращает дату создания сообщения
     * @return Дата создания сообщения
     */
    public Date getDateCreate()
    {
        return dateCreate;
    }

    /**
     * Возвращает дату последнего изменения сообщения
     * @return Дата последнего редактирования или null если сообщение не редактировалось
     */
    public Date getLastUpdate()
    {
        return lastUpdate;
    }

    /**
     * Устанавливает дату последнего редактирования сообщения
     * @param value Инастанс даты последнего редактирования
     * @return Инстанс текущего объекта
     */
    public PublicMessage setLastUpdate(Date value)
    {
        lastUpdate = value;
        return this;
    }
}
