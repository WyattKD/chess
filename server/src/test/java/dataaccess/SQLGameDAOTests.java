package dataaccess;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Collection;
import org.junit.jupiter.api.*;
import chess.ChessGame;
import model.GameData;

public class SQLGameDAOTests {
    private static GameDAO gameDAO;

    @BeforeAll
    public static void setupGameDAO() {
        assertDoesNotThrow(() -> {
            gameDAO = new SQLGameDAO();
            gameDAO.clear();
        });
    }

    @Test
    @AfterEach
    @DisplayName("Positive Clear")
    public void testClear() {
        assertDoesNotThrow(() -> {
            gameDAO.clear();
        });
    }

    @Test
    @DisplayName("Positive Create")
    void testCreateGamePositive() {
        Integer[] gameID = new Integer[1];
        assertDoesNotThrow(() -> {
            gameID[0] = gameDAO.createGame("test");
        });
        Assertions.assertNotNull(gameID[0]);
    }

    @Test
    @DisplayName("Negative Create")
    void testCreateGameNegative() {
        Integer[] gameID = new Integer[1];
        assertThrows(DataAccessException.class, () -> {
            gameID[0] = gameDAO.createGame(null);
        });
        Assertions.assertNull(gameID[0]);
    }

    @Test
    @DisplayName("Positive Update")
    void testUpdateGamePositive() throws DataAccessException {
        int gameID = gameDAO.createGame("test");
        GameData gameData = gameDAO.getGame(gameID);
        GameData newGameData = new GameData(gameData.gameID(), "test", gameData.blackUsername(), gameData.gameName(), gameData.game());
        assertDoesNotThrow(() -> {
            gameDAO.updateGame(newGameData);
        });
    }

    @Test
    @DisplayName("Negative Update")
    void testUpdateGameNegative() {
        GameData newGameData = new GameData(1, "test", null, "test", new ChessGame());
        assertDoesNotThrow(() -> {
            gameDAO.updateGame(newGameData);
        });
        GameData[] gameData = new GameData[1];
        assertThrows(DataAccessException.class, () -> {
            gameData[0] = gameDAO.getGame(1);
        });
        Assertions.assertNull(gameData[0]);
    }

    @Test
    @DisplayName("Positive Get")
    void testGetGamePositive() {
        Integer[] gameID = new Integer[1];
        assertDoesNotThrow(() -> {
            gameID[0] = gameDAO.createGame("test");
        });

        GameData[] gameData = new GameData[1];
        assertDoesNotThrow(() -> {
            gameData[0] = gameDAO.getGame(gameID[0]);
        });
        Assertions.assertNotNull(gameData[0]);
    }

    @Test
    @DisplayName("Negative Get")
    void testGetGameNegative() {
        GameData[] gameData = new GameData[1];
        assertThrows(DataAccessException.class, () -> {
            gameData[0] = gameDAO.getGame(2);
        });
        Assertions.assertNull(gameData[0]);
    }

    @Test
    @DisplayName("Positive List")
    void testListGamesPositive() {
        Integer[] gameID = new Integer[1];
        assertDoesNotThrow(() -> {
            gameID[0] = gameDAO.createGame("test");
        });

        assertDoesNotThrow(() -> {
            Collection<GameData> gameData = gameDAO.listGames();
            Assertions.assertNotNull(gameData);
            Assertions.assertEquals(1, gameData.size());
            Assertions.assertEquals(gameID[0], gameData.toArray(new GameData[0])[0].gameID());
        });
    }

    @Test
    @DisplayName("Negative List")
    void testListGamesNegative() {
        assertDoesNotThrow(() -> {
            Collection<GameData> gameData = gameDAO.listGames();
            Assertions.assertNotNull(gameData);
            Assertions.assertEquals(0, gameData.size());
        });
    }
}