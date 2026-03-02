package dataaccess;

import model.GameData;

import java.util.HashMap;

public interface GameDAO {
    int createGame(String game) throws DataAccessException;
    void updateGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;
    HashMap<Integer, GameData> listGames() throws DataAccessException;

    void clear() throws DataAccessException;
}
