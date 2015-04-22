package eu.leads.processor.web;

import org.vertx.java.core.json.JsonObject;

import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class WebSocketClient extends WebServiceClient{

    Session userSession = null;
    private MessageHandler messageHandler;

    public WebSocketClient(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider
                    .getWebSocketContainer();
            container.connectToServer(this, endpointURI);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession
     *            the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        this.userSession = userSession;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession
     *            the userSession which is getting closed.
     * @param reason
     *            the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        this.userSession = null;
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a
     * client send a message.
     *
     * @param message
     *            The text message
     */
    @OnMessage
    public void onMessage(String message) {
        if (this.messageHandler != null)
            this.messageHandler.handleMessage(message);
    }

    /**
     * register message handler
     */
    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    /**
     * Send a message
     */
    public void sendMessage(String queryId, String websocketId, String username, JsonObject object) {
        final MonitorQueryBody body = new MonitorQueryBody();
        body.setQueryId(queryId);
        body.setWebsocketId(websocketId);
        body.setUserName(username);
        body.setObject(object.toString());
        this.userSession.getAsyncRemote().sendText(body.toString());
    }

    /**
     * Message handler.
     */
    public static interface MessageHandler {
        public void handleMessage(String message);
    }
}