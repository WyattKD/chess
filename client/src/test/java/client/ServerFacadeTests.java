package client;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;

    private ServerFacade serverFacade;

    static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void setup() throws Exception {
        server.clear();
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @AfterEach
    void cleanup() throws DataAccessException {
        server.clear();
    }

    @Test
    @DisplayName("Register Positive")
    public void registerPositive() {
        assertTrue(serverFacade.register("username", "password", "email"));
    }

    @Test
    @DisplayName("Register Negative")
    public void registerNegative() {
        serverFacade.register("username", "password", "email");
        assertFalse(serverFacade.register("username", "password", "email"));
    }

    @Test
    @DisplayName("Login Positive")
    public void loginPositive() {
        serverFacade.register("username", "password", "email");
        assertTrue(serverFacade.login("username", "password"));
    }

    @Test
    @DisplayName("Login Negative")
    public void loginNegative() {
        serverFacade.register("username", "password", "email");
        assertFalse(serverFacade.login("username", "incorrect"));
    }

    @Test
    @DisplayName("Logout Positive")
    public void logoutPositive() {
        serverFacade.register("username", "password", "email");
        assertTrue(serverFacade.logout());
    }

    @Test
    @DisplayName("Logout Negative")
    public void logoutNegative() {
        assertFalse(serverFacade.logout());
    }

    @Test
    @DisplayName("Create Game Positive")
    public void createGamePositive() {
        serverFacade.register("username", "password", "email");
        assertTrue(serverFacade.createGame("game") >= 0);
    }

    @Test
    @DisplayName("Create Game Negative")
    public void createGameNegative() {
        assertTrue(serverFacade.createGame("name") == -1);
    }

    @Test
    @DisplayName("List Game Positive")
    public void listGamesPositive() {
        serverFacade.register("username", "password", "email");
        serverFacade.createGame("game");
        assertEquals(1, serverFacade.listGames().size());
    }

    @Test
    @DisplayName("List Game Negative")
    public void listGamesNegative() {
        assertEquals(serverFacade.listGames(), HashSet.newHashSet(8));
    }

    @Test
    @DisplayName("Join Game Positive")
    public void joinGamePositive() {
        serverFacade.register("username", "password", "email");
        int id = serverFacade.createGame("gameName");
        assertTrue(serverFacade.joinGame(id, "WHITE"));
    }

    @Test
    @DisplayName("Join Game Negative")
    public void joinGameNegative() {
        serverFacade.register("username", "password", "email");
        int id = serverFacade.createGame("gameName");
        serverFacade.joinGame(id, "WHITE");
        assertFalse(serverFacade.joinGame(id, "WHITE"));
    }

}
