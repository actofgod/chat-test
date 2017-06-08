
package wg_test.chat.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import wg_test.chat.proto.Request;
import wg_test.chat.proto.Response;

import java.util.logging.Logger;

/**
 * Обработчик запросов
 */
public class ServerHandler extends SimpleChannelInboundHandler<Request.ClientMessage>
{
    /** Инстанс текущего сервера */
    final private Server server;

    private Router router;

    public ServerHandler(Server server)
    {
        this.server = server;
    }

    /**
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request.ClientMessage msg) throws Exception
    {
        Route route = getRouter().getRoute(msg);

        if (route == null) {
            return;
        }
        Response.ServerMessage.Builder response = route.execute(ctx, msg);
        if (response != null) {
            Response.ServerMessage r = response.build();
            Logger.getLogger(ServerHandler.class.getName()).info("Response is: " + r);
            ctx.write(r);
        } else {
            Logger.getLogger(ServerHandler.class.getName()).info("Empty response");
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
    {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        cause.printStackTrace();
        System.err.println(cause.getMessage());
        if (cause.getCause() != null) {
            System.err.println(cause.getCause().getMessage());
        }
        server.getServiceLocator().getTokenContextMap().remove(ctx);
        ctx.close();
    }

    private Router getRouter()
    {
        if (router == null) {
            router = server.getServiceLocator().getRouter();
        }
        return router;
    }
}
