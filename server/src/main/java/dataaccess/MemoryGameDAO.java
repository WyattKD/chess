package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;
import java.util.Collection;
import java.util.Random;

public class MemoryGameDAO implements dataaccess.GameDAO {

    HashMap<Integer, GameData> db;

    public MemoryGameDAO() {
        db = HashMap.newHashMap(16);
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {

        int gameID = new Random().nextInt(1, 1000);
        int i = 0;
        while (db.containsKey(gameID) && i < 5) {
            gameID = new Random().nextInt(1, 1000);
            i++;
        }
        if (i == 5 && db.containsKey(gameID)) {
            throw new DataAccessException("Error: too many games");
        }
        GameData gameData = new GameData(gameID, null, null, gameName, new ChessGame());

        db.put(gameData.gameID(), gameData);
        return gameData.gameID();
    }

    @Override
    public void updateGame(GameData game) {
        db.remove(game.gameID());
        db.put(game.gameID(), game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return db.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() {
        return db.values();
    }

    @Override
    public void clear() {
        db = HashMap.newHashMap(16);
    }
}