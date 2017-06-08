package wg_test.chat.server.message;

import wg_test.chat.proto.MessageResponse;
import wg_test.chat.proto.Response;

/**
 * Класс билдера ответов на запросы при работе с сообщениями в чатике
 */
public class ResponseBuilder
{
    /**
     * Создаёт ответ на запрос создания публичного сообщения
     * @param message Инстанс созданного сообщения
     * @return Ответ, отправляемый клиенту
     */
    public MessageResponse.SendMessage buildPublicMessageResponse(PublicMessage message)
    {
        return MessageResponse.SendMessage.newBuilder()
                .setMessageId(message.getId())
                .setTimeSend(message.getDateCreate().getTime())
                .build();
    }

    /**
     * Создаёт ответ на запрос создания приватного сообщения
     * @param message Инстанс созданного сообщения
     * @return Ответ, отправляемый клиенту
     */
    public MessageResponse.SendMessage buildPrivateMessageResponse(PrivateMessage message)
    {
        return MessageResponse.SendMessage.newBuilder()
                .setMessageId(message.getId())
                .setTimeSend(message.getDateCreate().getTime())
                .build();
    }

    /**
     * Создайт ответ на запрос обновления публичного сообщения
     * @param message Инстанс отредактированного сообщения
     * @return Ответ, отправляемый клиенту
     */
    public MessageResponse.UpdateMessage buildPublicMessageUpdateResponse(PublicMessage message)
    {
        return MessageResponse.UpdateMessage.newBuilder()
                .setMessageId(message.getId())
                .setLastUpdate(message.getLastUpdate().getTime())
                .build();
    }

    /**
     * Создайт ответ на запрос обновления приватного сообщения
     * @param message Инстанс отредактированного сообщения
     * @return Ответ, отправляемый клиенту
     */
    public MessageResponse.UpdateMessage buildPrivateMessageUpdateResponse(PrivateMessage message)
    {
        return MessageResponse.UpdateMessage.newBuilder()
                .setMessageId(message.getId())
                .setLastUpdate(message.getLastUpdate().getTime())
                .build();
    }

    /**
     * Создаёт сообщение отпрвляемое всем активным клиентам после создания нового сообщения
     * @param message Инстанс созданного сообщения
     * @return Сообщение рассылаемое активным клиентам
     */
    public Response.ServerMessage buildPublicMessageBroadcastMessage(PublicMessage message)
    {
        MessageResponse.Message.Builder mess = MessageResponse.Message.newBuilder()
                .setMessageId(message.getId())
                .setMessage(message.getMessage())
                .setFromId(message.getFromId())
                .setToId(0)
                .setTimeSend(message.getDateCreate().getTime());
        if (message.getLastUpdate() != null) {
            mess.setLastUpdate(message.getLastUpdate().getTime());
        }
        return Response.ServerMessage.newBuilder()
                .setSuccess(true)
                .setType(Response.ServerMessage.MessageType.NEW_MESSAGE)
                .setData(mess.build().toByteString())
                .build();
    }

    /**
     * Создаёт сообщение отпрвляемое клиентам после создания нового приватного сообщения
     * @param message Инстанс созданного сообщения
     * @return Сообщение рассылаемое клиентам, учавствующим в сообщении
     */
    public Response.ServerMessage buildPrivateMessageBroadcastMessage(PrivateMessage message)
    {
        MessageResponse.Message.Builder mess = MessageResponse.Message.newBuilder()
                .setMessageId(message.getId())
                .setMessage(message.getMessage())
                .setFromId(message.getFromId())
                .setToId(message.getToId())
                .setTimeSend(message.getDateCreate().getTime());
        if (message.getDateView() != null) {
            mess.setDateView(message.getDateView().getTime());
        }
        if (message.getLastUpdate() != null) {
            mess.setLastUpdate(message.getLastUpdate().getTime());
        }
        return Response.ServerMessage.newBuilder()
                .setSuccess(true)
                .setType(Response.ServerMessage.MessageType.NEW_MESSAGE)
                .setData(mess.build().toByteString())
                .build();
    }

    /**
     * Создаёт сообщение, рассылаемое всем клиентам, при редактировании сообщения в общем чате
     * @param message Инстанс созданного сообщения
     * @return Сообщение рассылаемое активным клиентам
     */
    public Response.ServerMessage buildPublicMessageUpdateBroadcastMessage(PublicMessage message)
    {
        MessageResponse.Message.Builder mess = MessageResponse.Message.newBuilder()
                .setMessageId(message.getId())
                .setMessage(message.getMessage())
                .setFromId(message.getFromId())
                .setToId(0)
                .setTimeSend(message.getDateCreate().getTime())
                .setLastUpdate(message.getLastUpdate().getTime());
        return Response.ServerMessage.newBuilder()
                .setSuccess(true)
                .setType(Response.ServerMessage.MessageType.UPDATED_MESSAGE)
                .setData(mess.build().toByteString())
                .build();
    }

    /**
     * Создаёт сообщение отпрвляемое клиентам после редактирования приватного сообщения
     * @param message Инстанс отредактированного сообщения
     * @return Сообщение рассылаемое клиентам, учавствующим в сообщении
     */
    public Response.ServerMessage buildPrivateMessageUpdateBroadcastMessage(PrivateMessage message)
    {
        MessageResponse.Message.Builder mess = MessageResponse.Message.newBuilder()
                .setMessageId(message.getId())
                .setMessage(message.getMessage())
                .setFromId(message.getFromId())
                .setToId(message.getToId())
                .setTimeSend(message.getDateCreate().getTime())
                .setLastUpdate(message.getLastUpdate().getTime());
        if (message.getDateView() != null) {
            mess.setDateView(message.getDateView().getTime());
        }
        return Response.ServerMessage.newBuilder()
                .setSuccess(true)
                .setType(Response.ServerMessage.MessageType.UPDATED_MESSAGE)
                .setData(mess.build().toByteString())
                .build();
    }

    /**
     * Создаёт сообщение, рассылаемое всем клиентам, при удалении сообщения в общем чате
     * @param message Инстанс удалённого сообщения
     * @return Сообщение рассылаемое активным клиентам
     */
    public Response.ServerMessage buildPublicMessageDeleteBroadcastMessage(PublicMessage message)
    {
        MessageResponse.DeleteMessage.Builder mess = MessageResponse.DeleteMessage.newBuilder()
                .setMessageId(message.getId());
        return Response.ServerMessage.newBuilder()
                .setSuccess(true)
                .setType(Response.ServerMessage.MessageType.DELETE_MESSAGE)
                .setData(mess.build().toByteString())
                .build();
    }

    /**
     * Создаёт сообщение отпрвляемое клиентам после удаления приватного сообщения
     * @param message Инстанс удалённого сообщения
     * @return Сообщение рассылаемое клиентам, учавствующим в сообщении
     */
    public Response.ServerMessage buildPrivateMessageDeleteBroadcastMessage(PrivateMessage message)
    {
        MessageResponse.DeleteMessage.Builder mess = MessageResponse.DeleteMessage.newBuilder()
                .setMessageId(message.getId());
        return Response.ServerMessage.newBuilder()
                .setSuccess(true)
                .setType(Response.ServerMessage.MessageType.DELETE_MESSAGE)
                .setData(mess.build().toByteString())
                .build();
    }
}
