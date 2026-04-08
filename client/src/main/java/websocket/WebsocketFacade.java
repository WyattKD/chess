package websocket;

import java.io.IOException;
import java.net.URI;

import jakarta.websocket.*;
import com.google.gson.Gson;

import chess.ChessMove;
import ui.LiveMenu;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class WebsocketFacade extends Endpoint {
    private final URI socket;
    private final Session session;

    public WebsocketFacade(int port, LiveMenu menu) throws Exception {
        socket = new URI("ws://localhost:" + port + "/ws");

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, socket);

        session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                ServerMessage m = new Gson().fromJson(message, ServerMessage.class);
                switch (m.getServerMessageType()) {
                    case LOAD_GAME:
                        LoadGameMessage game = new Gson().fromJson(message, LoadGameMessage.class);
                        menu.updateGame(game.game);
                        break;

                    case NOTIFICATION:
                        NotificationMessage nm = new Gson().fromJson(message, NotificationMessage.class);
                        menu.displayNotification(nm.message);
                        break;

                    case ERROR:
                        ErrorMessage em = new Gson().fromJson(message, ErrorMessage.class);
                        menu.displayError(em.errorMessage);
                        break;
                }
            }
        });
    }

    public void connect(String authToken, int gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(CommandType.CONNECT, authToken, gameID);
        session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws IOException {
        MoveCommand command = new MoveCommand(authToken, gameID, move);
        session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void leaveGame(String authToken, int gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(CommandType.LEAVE, authToken, gameID);
        session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void resignGame(String authToken, int gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(CommandType.RESIGN, authToken, gameID);
        session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}