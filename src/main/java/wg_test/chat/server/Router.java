package wg_test.chat.server;

import com.google.protobuf.GeneratedMessageV3;
import wg_test.chat.proto.Request;
import wg_test.chat.server.di.Component;
import wg_test.chat.server.di.Container;

import java.util.HashMap;
import java.util.logging.Logger;

public class Router extends Component
{
    private HashMap<Request.ClientMessage.MessageType, Route> routes;

    public Router(Container container)
    {
        super(container);
        routes = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T extends GeneratedMessageV3> void addRoute(Request.ClientMessage.MessageType type, RequestHandler<T> handler)
    {
        if (routes.containsKey(type)) {
            throw new RuntimeException("Route for type " + type.getNumber() + " already exists");
        }
        Route<T> route = new Route<>(handler);
        routes.put(type, route);
    }

    public Route getRoute(Request.ClientMessage message)
    {
        Route route = routes.get(message.getType());
        if (route == null) {
            Logger.getLogger(Router.class.getName()).warning("Invalid request type#" + message.getType().getNumber());
        }
        return route;
    }
}
