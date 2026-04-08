package server.websocket;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import com.google.gson.Gson;
import chess.ChessGame.TeamColor;
import dataaccess.AuthDAO;
import model.GameData;
import service.GameService;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import static websocket.messages.ServerMessage.ServerMessageType.ERROR;
import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;
import static websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;


@WebSocket
public class WebsocketHandler {
    private final GameService gameService;
    private final AuthDAO authDAO;
    private HashMap<Integer, HashSet<Session>> allClients;

    public WebsocketHandler(GameService gameService, AuthDAO authDAO) {
        this.gameService = gameService;
        this.authDAO = authDAO;
        allClients = new HashMap<>();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

        try {
            switch (command.getCommandType()) {
                case CONNECT:
                    //handleConnect(session, command);
                    break;

                case MAKE_MOVE:
                    MoveCommand move = new Gson().fromJson(message, MoveCommand.class);
                    //handleMove(move, session);
                    break;

                case LEAVE:
                    //handleLeave(command, session);
                    break;

                case RESIGN:
                    //handleResign(command, session);
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            try {
                session.getRemote().sendString(new Gson().toJson(new ErrorMessage(ERROR, e.getMessage())));
            } catch (IOException ee) {
                System.out.println(ee.getMessage());
            }
        }
    }
}
