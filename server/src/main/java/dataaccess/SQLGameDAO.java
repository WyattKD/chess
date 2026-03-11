package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

public class SQLGameDAO implements GameDAO {
    public SQLGameDAO() {
        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var conn = DatabaseManager.getConnection()) {
            var createTestTable = """            
                    CREATE TABLE if NOT EXISTS game (
                                    gameID INT NOT NULL,
                                    whiteUsername VARCHAR(255),
                                    blackUsername VARCHAR(255),
                                    gameName VARCHAR(255),
                                    chessGame LONGTEXT NOT NULL,
                                    PRIMARY KEY (gameID)
                                    )""";
            try (var createTableStatement = conn.prepareStatement(createTestTable)) {
                createTableStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int createGame(String game) throws DataAccessException {
        if (game == null) {
            throw new DataAccessException("bad request");
        }
        int gameID = new Random().nextInt(1, 1000);
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, chessGame) VALUES(?, ?, ?, ?, ?)")) {
                statement.setInt(1, gameID);
                statement.setString(2, null);
                statement.setString(3, null);
                statement.setString(4, game);
                statement.setString(5, new Gson().toJson(new ChessGame()));
                statement.executeUpdate();
            }
            return gameID;
        } catch (SQLException exception) {
            throw new DataAccessException(exception.getMessage());
        }
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("UPDATE game SET whiteUsername=?, blackUsername=?, gameName=?, chessGame=? WHERE gameID=?")) {
                statement.setString(1, gameData.whiteUsername());
                statement.setString(2, gameData.blackUsername());
                statement.setString(3, gameData.gameName());
                statement.setString(4, new Gson().toJson(gameData.game()));
                statement.setInt(5, gameData.gameID());
                statement.executeUpdate();
            }
        } catch (SQLException exception) {
            throw new DataAccessException(exception.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT whiteUsername, blackUsername, gameName, chessGame FROM game WHERE gameID=?")) {
                statement.setInt(1, gameID);
                try (var results = statement.executeQuery()) {
                    if (results.next()) {
                        var whiteUsername = results.getString("whiteUsername");
                        var blackUsername = results.getString("blackUsername");
                        var gameName = results.getString("gameName");
                        var chessGame = new Gson().fromJson(results.getString("chessGame"), ChessGame.class);
                        return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                    } else {
                        throw new DataAccessException("bad request");
                    }
                }
            }
        } catch (SQLException exception) {
            throw new DataAccessException(exception.getMessage());
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        HashSet<GameData> games = HashSet.newHashSet(16);
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game")) {
                try (var results = statement.executeQuery()) {
                    while (results.next()) {
                        var gameID = results.getInt("gameID");
                        var whiteUsername = results.getString("whiteUsername");
                        var blackUsername = results.getString("blackUsername");
                        var gameName = results.getString("gameName");
                        var chessGame = new Gson().fromJson(results.getString("chessGame"), ChessGame.class);
                        games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame));
                    }
                }
                return games;
            }
        } catch (SQLException | DataAccessException exception) {
            throw new DataAccessException(exception.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("TRUNCATE game")) {
                statement.executeUpdate();
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        } catch (SQLException | DataAccessException exception) {
            throw new DataAccessException(exception.getMessage());
        }
    }
}
