package dataaccess;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.*;

import model.AuthData;
import model.UserData;

public class SQLAuthDAOTests {
    private static AuthDAO authDAO;

    @BeforeAll
    public static void setupAuthDAO() {
        assertDoesNotThrow(() -> {
            authDAO = new SQLAuthDAO();
            authDAO.clear();
        });
    }

    @Test
    @AfterEach
    @DisplayName("Positive Clear")
    public void testClear() {
        assertDoesNotThrow(() -> {
            authDAO.clear();
        });
    }

    @Test
    @DisplayName("Positive Create")
    void testCreateAuthPositive() {
        final AuthData[] authData = new AuthData[1];
        assertDoesNotThrow(() -> {
            authData[0] = authDAO.createAuth(new UserData("test", "test", "test"));
        });
        Assertions.assertNotNull(authData[0]);
    }

    @Test
    @DisplayName("Negative Create")
    void testCreateAuthNegative() {
        final AuthData[] authData = new AuthData[1];
        assertThrows(DataAccessException.class, () -> {
            authData[0] = authDAO.createAuth(new UserData(null, "test", "test"));
        });
        Assertions.assertNull(authData[0]);
    }

    @Test
    @DisplayName("Positive Delete")
    void testDeleteAuthPositive() throws DataAccessException {
        final AuthData[] authData = new AuthData[1];
        assertDoesNotThrow(() -> {
            authData[0] = authDAO.createAuth(new UserData("test", "test", "test"));
        });
        Assertions.assertNotNull(authData[0]);
        Assertions.assertTrue(authDAO.deleteAuth(authData[0].authToken()));
    }

    @Test
    @DisplayName("Negative Delete")
    void testDeleteAuthNegative() throws DataAccessException {
        Assertions.assertFalse(authDAO.deleteAuth("fake"));
    }

    @Test
    @DisplayName("Positive Get")
    void testGetAuthPositive() {
        final AuthData[] authData = new AuthData[2];
        assertDoesNotThrow(() -> {
            authData[0] = authDAO.createAuth(new UserData("test", "test", "test"));
        });
        Assertions.assertNotNull(authData[0]);
        assertDoesNotThrow(() -> {
            authData[1] = authDAO.getAuth(authData[0].authToken());
        });
        Assertions.assertEquals(authData[0], authData[1]);
    }

    @Test
    @DisplayName("Negative Get")
    void testGetAuthNegative() {
        final AuthData[] authData = new AuthData[1];
        assertThrows(DataAccessException.class, () -> {
            authData[0] = authDAO.getAuth("fake");
        });
        Assertions.assertNull(authData[0]);
    }

}