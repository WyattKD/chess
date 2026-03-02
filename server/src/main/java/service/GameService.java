package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import model.results.*;
import model.requests.*;
import java.util.Collection;

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
        } catch (DataAccessException e) {
            return new ListGamesResult(null, "Error: unauthorized");
        }

        try {
            Collection<GameData> gamesList = gameDAO.listGames();
            return new ListGamesResult(gamesList, null);
        } catch (DataAccessException exception) {
            return new ListGamesResult(null, "Error: " + exception.getMessage());
        }
    }

}
