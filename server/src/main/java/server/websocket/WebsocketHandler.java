package server.websocket;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import com.google.gson.Gson;
import chess.ChessGame.TeamColor;
import dataaccess.AuthDAO;
import model.GameData;
import org.jetbrains.annotations.NotNull;
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
                    handleConnect(session, command);
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

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        allClients.values().forEach(clients -> clients.remove(session));
    }

    private void broadcast(HashSet<Session> clients, ServerMessage message, Session ignore) throws IOException{
        for (Session s : clients) {
            if (ignore != null && s.equals(ignore)) {
                continue;
            }
            s.getRemote().sendString(new Gson().toJson(message));
        }
    }

    private void handleConnect(Session session, UserGameCommand command) throws Exception {
        String username = authDAO.getAuth(command.getAuthToken()).username();

        HashSet<Session> clients = allClients.computeIfAbsent(command.getGameID(), k -> new HashSet<>());
        clients.add(session);
        GameData game = gameService.getGame(command.getGameID());

        LoadGameMessage message = new LoadGameMessage(LOAD_GAME, game.game());
        session.getRemote().sendString(new Gson().toJson(message));

        NotificationMessage m = getNotificationMessage(game, username);
        broadcast(clients, m, session);
    }

    @NotNull
    private static NotificationMessage getNotificationMessage(GameData game, String username) {
        String black = game.blackUsername();
        String white = game.whiteUsername();
        NotificationMessage m;
        if (black != null && username.equals(black)) {
            m = new NotificationMessage(NOTIFICATION, username + " joined as black");
        } else if (white != null && username.equals(white)) {
            m = new NotificationMessage(NOTIFICATION, username + " joined as white");
        } else {
            m = new NotificationMessage(NOTIFICATION, username + " joined as an observer");
        }
        return m;
    }


}
