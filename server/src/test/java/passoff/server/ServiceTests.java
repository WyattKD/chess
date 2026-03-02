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

import java.util.Collection;


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
        gameService = new GameService(authDAO, gameDAO);
        userService = new UserService(authDAO, userDAO);

        testGame = new ChessGame();
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
        gameDAO.createGame(testGameData.gameName());
        GameData gameID = gameDAO.getGame(1);
        ClearService clearService = new ClearService(authDAO, gameDAO, userDAO);
        Assertions.assertDoesNotThrow(clearService::clearDatabase);
        clearService.clearDatabase();
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser("Test"));
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth(authToken[0]));
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
    @Test
    @DisplayName("Positive Login")
    void testLoginPositive() throws DataAccessException {
        userDAO.createUser(testUserData);
        LoginResult result = userService.login(new LoginRequest("test", "test"));
        Assertions.assertNotNull(result.username());
        Assertions.assertNotNull(result.authToken());
        Assertions.assertNull(result.message());
    }

    @Test
    @DisplayName("Negative Login")
    void testLoginNegative() throws DataAccessException {
        userDAO.createUser(testUserData);
        LoginResult result = userService.login(new LoginRequest("test", "i_forgot"));
        Assertions.assertEquals(new LoginResult(null, null, "Error: unauthorized"), result);
    }

    @Test
    @DisplayName("Positive Logout")
    void testLogoutPositive() throws DataAccessException {
        final String[] authToken = new String[1];
        authToken[0] = authDAO.createAuth(testUserData).authToken();
        LogoutResult result = userService.logout(new LogoutRequest(authToken[0]));
        Assertions.assertEquals(new LogoutResult(null), result);
    }

    @Test
    @DisplayName("Negative Logout")
    void testLogoutNegative() throws DataAccessException {
        String authToken = "fake";
        LogoutResult result = userService.logout(new LogoutRequest(authToken));
        Assertions.assertEquals(new LogoutResult("Error: unauthorized"), result);
    }

    @Test
    @DisplayName("Positive Create Game")
    void testCreateGamePositive() throws DataAccessException {
        String authToken = authDAO.createAuth(testUserData).authToken();
        int gameID = gameService.createGame(new CreateGameRequest(authToken, testGameData.gameName())).gameID();

        GameData expectedGameData = new GameData(gameID, null, null, "game", new ChessGame());
        Assertions.assertDoesNotThrow(() -> {
            Assertions.assertEquals(expectedGameData, gameDAO.getGame(gameID));
        });
    }

    @Test
    @DisplayName("Negative Create Game")
    void testCreateGameNegative() throws DataAccessException {
        String authToken = authDAO.createAuth(testUserData).authToken();
        CreateGameResult result = gameService.createGame(new CreateGameRequest(authToken, null));
        CreateGameResult expectedResult = new CreateGameResult(null, "Error: bad request");
        Assertions.assertEquals(expectedResult, result);

        authDAO.deleteAuth(authToken);
        result = gameService.createGame(new CreateGameRequest(authToken, testGameData.gameName()));
        expectedResult = new CreateGameResult(null, "Error: unauthorized");
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("Positive List Games")
    void testListGamesPositive() throws DataAccessException {
        gameDAO.createGame("game1");
        gameDAO.createGame("game2");
        String authToken = authDAO.createAuth(testUserData).authToken();
        ListGamesResult result = gameService.listGames(new ListGamesRequest(authToken));

        Collection<GameData> gamesList = gameDAO.listGames();
        Assertions.assertEquals(new ListGamesResult(gamesList, null), result);
    }

    @Test
    @DisplayName("Negative List Games")
    void testListGamesNegative() throws DataAccessException {
        String authToken = authDAO.createAuth(testUserData).authToken();
        authDAO.deleteAuth(authToken);
        ListGamesResult result = gameService.listGames(new ListGamesRequest(authToken));
        Assertions.assertEquals(new ListGamesResult(null, "Error: unauthorized"), result);
    }

    @Test
    @DisplayName("Positive Join Game")
    void testJoinGamePositive() throws DataAccessException {
        int gameID = gameDAO.createGame("game1");
        String authToken = authDAO.createAuth(testUserData).authToken();
        JoinGameResult result = gameService.joinGame(new JoinGameRequest(authToken, "WHITE", gameID));
        Assertions.assertEquals(new JoinGameResult(null), result);
        result = gameService.joinGame(new JoinGameRequest(authToken, "WHITE/BLACK", gameID));
        Assertions.assertEquals(new JoinGameResult(null), result);
    }

    @Test
    @DisplayName("Negative Join Game")
    void testJoinGameNegative() throws DataAccessException {
        int gameID = gameDAO.createGame("game1");
        String authToken = authDAO.createAuth(testUserData).authToken();
        GameData gameData = gameDAO.getGame(gameID);
        gameData = new GameData(gameID, "TakenWhite", "TakenBlack", gameData.gameName(), gameData.game());
        gameDAO.updateGame(gameData);

        JoinGameResult result = gameService.joinGame(new JoinGameRequest(authToken, "WHITE", gameID));
        Assertions.assertEquals(new JoinGameResult("Error: already taken"), result);

        result = gameService.joinGame(new JoinGameRequest(authToken, "BLACK", gameID));
        Assertions.assertEquals(new JoinGameResult("Error: already taken"), result);

        result = gameService.joinGame(new JoinGameRequest(authToken, "", gameID));
        Assertions.assertEquals(new JoinGameResult("Error: bad request"), result);

        authDAO.deleteAuth(authToken);
        result = gameService.joinGame(new JoinGameRequest(authToken, "BLACK", gameID));
        Assertions.assertEquals(new JoinGameResult("Error: unauthorized"), result);
    }

}
