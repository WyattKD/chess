package passoff.server;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import model.requests.*;
import model.results.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.*;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.util.HashSet;


public class ServiceTests {

    static GameDAO gameDAO;
    static AuthDAO authDAO;
    static UserDAO userDAO;

    static AuthData testAuthData;
    static UserData testUserData;
    static GameData testGameData;
    static GameService gameService;
    static UserService userService;
    static ChessGame testGame;

    @BeforeAll
    static void init() throws DataAccessException {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        //gameService = new GameService(gameDAO, authDAO);
        userService = new UserService(authDAO, userDAO);
        testAuthData = new AuthData("Username", "authToken");
        testGameData = new GameData(1, "w", "b", "game", testGame);
        testUserData = new UserData("test", "test", "test");
    }

    @BeforeEach
    void setup() throws DataAccessException {
        gameDAO.clear();
        userDAO.clear();
        authDAO.clear();
    }

    @Test
    @DisplayName("Positive Clear")
    void testClearDatabase() throws DataAccessException {
        userDAO.createUser(testUserData);
        final String[] authToken = new String[1];
        authToken[0] = authDAO.createAuth(testUserData).authToken();
        gameDAO.createGame(testGameData);
        GameData gameID = gameDAO.getGame(1);
        ClearService clearService = new ClearService(authDAO, gameDAO, userDAO);
        Assertions.assertDoesNotThrow(clearService::clearDatabase);
        clearService.clearDatabase();
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser("Test"));
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth(authToken[0]));
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.getGame(1));
    }

    @Test
    @DisplayName("Positive Register")
    void testRegisterPositive() {
        RegisterResult result = userService.register(new RegisterRequest("test", "test", "test"));
        Assertions.assertNotNull(result.authToken());
        Assertions.assertNotNull(result.username());
        Assertions.assertNull(result.message());
    }

    @Test
    @DisplayName("Negative Register")
    void testRegisterNegative() throws DataAccessException {
        RegisterResult result = userService.register(new RegisterRequest("test", null, null));
        Assertions.assertEquals(new RegisterResult(null, null, "Error: bad request"), result);
        userDAO.createUser(testUserData);
        result = userService.register(new RegisterRequest("test", "test", "test"));
        Assertions.assertEquals(new RegisterResult(null, null, "Error: already taken"), result);

    }
}
