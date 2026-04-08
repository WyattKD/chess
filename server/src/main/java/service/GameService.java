package service;

import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import model.results.*;
import model.requests.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.util.Collection;

import static websocket.messages.ServerMessage.ServerMessageType.ERROR;
import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) {
        try {
            AuthData authData = authDAO.getAuth(createGameRequest.authToken());
            if (authData == null) {
                return new CreateGameResult(null, "Error: unauthorized");
            } else if (createGameRequest.gameName() == null) {
                return new CreateGameResult(null, "Error: bad request");
            }

            int gameID;
            gameID = gameDAO.createGame(createGameRequest.gameName());

            return new CreateGameResult(gameID, null);
        } catch (DataAccessException exception) {
            return new CreateGameResult(null, "Error: " + exception.getMessage());
        }
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) {
        try {
            AuthData authData = authDAO.getAuth(listGamesRequest.authToken());
            if (authData == null) {
                throw new DataAccessException(null);
            }
        } catch (DataAccessException exception) {
            if (exception.getMessage().contains("connection")) {
                return new ListGamesResult(null, "Error: " + exception.getMessage());
            } else {
                return new ListGamesResult(null, "Error: unauthorized");
            }
        }

        try {
            Collection<GameData> gamesList = gameDAO.listGames();
            return new ListGamesResult(gamesList, null);
        } catch (DataAccessException exception) {
            return new ListGamesResult(null, "Error: " + exception.getMessage());
        }
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) {
        try {
            GameData gameData = gameDAO.getGame(joinGameRequest.gameID());

            if (gameData == null || joinGameRequest.playerColor() == null) {
                return new JoinGameResult("Error: bad request");
            }

            AuthData authData = authDAO.getAuth(joinGameRequest.authToken());

            if (authData == null) {
                return new JoinGameResult("Error: unauthorized");
            }
            switch (joinGameRequest.playerColor()) {
                case "WHITE" -> {
                    if (gameData.whiteUsername() == null) {
                        gameData = new GameData(gameData.gameID(), authData.username(), gameData.blackUsername(),
                                gameData.gameName(), gameData.game());
                    } else {
                        throw new DataAccessException("already taken");
                    }
                }
                case "BLACK" -> {
                    if (gameData.blackUsername() == null) {
                        gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), authData.username(),
                                gameData.gameName(), gameData.game());
                    } else {
                        throw new DataAccessException("already taken");
                    }
                }
                case "WHITE/BLACK" -> {
                    if (gameData.whiteUsername() == null) {
                        gameData = new GameData(gameData.gameID(), authData.username(), gameData.blackUsername(),
                                gameData.gameName(), gameData.game());
                    } else if (gameData.blackUsername() == null) {
                        gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), authData.username(),
                                gameData.gameName(), gameData.game());
                    } else {
                        throw new DataAccessException("already taken");
                    }
                }
                default -> {
                    throw new DataAccessException("bad request");
                }
            }

            gameDAO.updateGame(gameData);
        } catch (DataAccessException exception) {
            return new JoinGameResult("Error: " + exception.getMessage());
        }
        return new JoinGameResult(null);
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return gameDAO.getGame(gameID);
    }

    public void setGame(GameData game) throws DataAccessException {
        gameDAO.updateGame(game);
    }

    public ServerMessage makeMove(int gameID, ChessMove move) throws DataAccessException {
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            return new ErrorMessage(ERROR, "no game");
        }

        try {
            gameData.game().makeMove(move);
        } catch (InvalidMoveException e) {
            return new ErrorMessage(ERROR, "invalid move " + move.toString());
        }

        gameDAO.updateGame(gameData);
        return new LoadGameMessage(LOAD_GAME, gameData.game());
    }
}
