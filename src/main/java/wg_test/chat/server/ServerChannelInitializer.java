package wg_test.chat.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import wg_test.chat.proto.Request;

/**
 * Класс для инициализации цепочки обработчиков сообщений с клиента
 */
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel>
{
    /** Инстанс сервера */
    final private Server server;

    public ServerChannelInitializer(Server server)
    {
        this.server = server;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception
    {
        ChannelPipeline p = ch.pipeline();

        // добавляем цепочку для распаковки protobuf пакетов
        p.addLast(new ProtobufVarint32FrameDecoder());
        p.addLast(new ProtobufDecoder(Request.ClientMessage.getDefaultInstance()));
        p.addLast(new ProtobufVarint32LengthFieldPrepender());
        p.addLast(new ProtobufEncoder());

        // добавляем обработчик сообщений чата
        p.addLast(new ServerHandler(server));
    }
}
