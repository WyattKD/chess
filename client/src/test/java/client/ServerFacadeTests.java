package client;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


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
        assertFalse(serverFacade.login("username", "pass"));
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

}
