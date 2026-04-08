package server.websocket;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsConfig;
import java.util.function.Consumer;
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


public class WebsocketHandler implements Consumer<WsConfig>{
    private final GameService gameService;
    private final AuthDAO authDAO;
    private HashMap<Integer, HashSet<WsContext>> allClients;

    public WebsocketHandler(GameService gameService, AuthDAO authDAO) {
        this.gameService = gameService;
        this.authDAO = authDAO;
        allClients = new HashMap<>();
    }

    @Override
    public void accept(WsConfig ws) {

        ws.onMessage(ctx -> {
            String message = ctx.message();
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

            try {
                switch (command.getCommandType()) {
                    case CONNECT -> handleConnect(ctx, command);
                    case MAKE_MOVE -> {
                        MoveCommand move = new Gson().fromJson(message, MoveCommand.class);
                        handleMove(move, ctx);
                    }
                    case LEAVE -> handleLeave(command, ctx);
                    case RESIGN -> handleResign(command, ctx);
                }
            } catch (Exception e) {
                ctx.send(new Gson().toJson(new ErrorMessage(ERROR, e.getMessage())));
            }
        });

        ws.onClose(ctx -> {
            allClients.values().forEach(clients -> clients.remove(ctx));
        });
    }

    public void onClose(WsContext ctx, int statusCode, String reason) {
        allClients.values().forEach(clients -> clients.remove(ctx));
    }

    private void broadcast(HashSet<WsContext> clients, ServerMessage message, WsContext ignore) {
        if (clients == null) return;

        for (WsContext ctx : clients) {
            if (ignore != null && ctx.equals(ignore)) continue;
            ctx.send(new Gson().toJson(message));
        }
    }

    private void handleConnect(WsContext ctx, UserGameCommand command) throws Exception {
        String username = authDAO.getAuth(command.getAuthToken()).username();

        HashSet<WsContext> clients = allClients.computeIfAbsent(command.getGameID(), k -> new HashSet<>());
        clients.add(ctx);
        GameData game = gameService.getGame(command.getGameID());

        LoadGameMessage message = new LoadGameMessage(LOAD_GAME, game.game());
        ctx.send(new Gson().toJson(message));

        NotificationMessage m = getNotificationMessage(game, username);
        broadcast(clients, m, ctx);
    }

    @NotNull
    private static NotificationMessage getNotificationMessage(GameData game, String username) {
        String black = game.blackUsername();
        String white = game.whiteUsername();
        NotificationMessage m;
        if (black != null && username.equals(black)) {
            m = new NotificationMessage(NOTIFICATION, username + " joined as black.");
        } else if (white != null && username.equals(white)) {
            m = new NotificationMessage(NOTIFICATION, username + " joined as white.");
        } else {
            m = new NotificationMessage(NOTIFICATION, username + " joined as a spectator.");
        }
        return m;
    }

    private void handleLeave(UserGameCommand command, WsContext ctx) throws Exception {
        GameData game = gameService.getGame(command.getGameID());
        String username = authDAO.getAuth(command.getAuthToken()).username();

        String black = game.blackUsername();
        String white = game.whiteUsername();
        NotificationMessage m;
        if (black != null && username.equals(black)) {
            game = new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game());
            m = new NotificationMessage(NOTIFICATION, username + " left the game.");
        } else if (white != null && username.equals(white)) {
            game = new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game());
            m = new NotificationMessage(NOTIFICATION, username + " left the game.");
        } else {
            m = new NotificationMessage(NOTIFICATION, username + " has stopped observing.");
        }
        broadcast(allClients.get(game.gameID()), m, ctx);
        gameService.setGame(game);
        ctx.session.close();
    }

    private void handleResign(UserGameCommand command, WsContext ctx) throws Exception {
        GameData game = gameService.getGame(command.getGameID());
        String username = authDAO.getAuth(command.getAuthToken()).username();

        String black = game.blackUsername();
        String white = game.whiteUsername();
        NotificationMessage m;

        GameData gameReset = new GameData(game.gameID(), null, null, game.gameName(), game.game());
        if (black != null && username.equals(black)) {
            game = gameReset;
            m = new NotificationMessage(NOTIFICATION, username + " has resigned. White wins!");
        } else if (white != null && username.equals(white)) {
            game = gameReset;
            m = new NotificationMessage(NOTIFICATION, username + " has resigned. Black wins!");
        } else {
            ctx.send(new Gson().toJson(new ErrorMessage(ERROR, "Spectators cannot resign.")));
            return;
        }

        broadcast(allClients.get(game.gameID()), m, null);
        gameService.setGame(game);
        ctx.session.close();
    }

    private void handleMove(MoveCommand move, WsContext ctx) throws Exception {
        String username = authDAO.getAuth(move.getAuthToken()).username();
        GameData gameData = gameService.getGame(move.getGameID());
        if (!username.equals(gameData.game().getTeamTurn() == TeamColor.WHITE ? gameData.whiteUsername() : gameData.blackUsername())) {
            ServerMessage message = new ErrorMessage(ERROR, "you cannot move that piece");
            ctx.send(new Gson().toJson(message));
            return;
        }

        ServerMessage message = gameService.makeMove(move.getGameID(), move.getMove());
        if (message.getClass().equals(ErrorMessage.class)) {
            ctx.send(new Gson().toJson(message));
            return;
        }

        broadcast(allClients.get(move.getGameID()), message, null);
        NotificationMessage nm = new NotificationMessage(NOTIFICATION, username + " moved " + move.getMove().toString());
        broadcast(allClients.get(move.getGameID()), nm, ctx);

        gameData.game().makeMove(move.getMove());

        boolean stalemate = gameData.game().isInStalemate(gameData.game().getTeamTurn());
        boolean check = gameData.game().isInCheck(gameData.game().getTeamTurn());
        boolean checkmate = gameData.game().isInCheckmate(gameData.game().getTeamTurn());

        if (check || checkmate || stalemate) {
            username = gameData.game().getTeamTurn() == TeamColor.WHITE ? gameData.whiteUsername() : gameData.blackUsername();
            String mate;
            if (stalemate) {
                mate = " is in stalemate.";
            } else if (checkmate) {
                mate = " has been checkmated.";
            } else {
                mate = " is in check.";
            }
            nm = new NotificationMessage(NOTIFICATION, username + mate);
            broadcast(allClients.get(move.getGameID()), nm, null);
        }
    }

}
