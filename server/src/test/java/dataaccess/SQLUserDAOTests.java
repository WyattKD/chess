package dataaccess;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.*;

import model.UserData;

public class SQLUserDAOTests {
    private static UserDAO userDAO;

    @BeforeAll
    public static void setupUserDAO() {
        assertDoesNotThrow(() -> {
            userDAO = new SQLUserDAO();
            userDAO.clear();
        });
    }

    @Test
    @AfterEach
    @DisplayName("Positive Clear")
    public void clearTest() {
        assertDoesNotThrow(() -> {
            userDAO.clear();
        });
    }


    @Test
    @DisplayName("Positive Get")
    void testGetUserPositive() {
        testCreateUserPositive();
        final UserData[] userData = new UserData[1];
        assertDoesNotThrow(() -> {
            userData[0] = userDAO.getUser("test");
        });
        Assertions.assertNotNull(userData[0]);
    }

    @Test
    @DisplayName("Negative Get")
    void testGetUserNegative() {
        final UserData[] userData = new UserData[1];
        assertThrows(DataAccessException.class, () -> {
            userData[0] = userDAO.getUser("test");
        });
        Assertions.assertNull(userData[0]);
    }

    @Test
    @DisplayName("Positive Create")
    void testCreateUserPositive() {
        assertDoesNotThrow(() -> {
            userDAO.createUser(new UserData("test", "test", "test"));
        });
    }

    @Test
    @DisplayName("Negative Create")
    void testCreateUserNegative() {
        assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(new UserData(null, "test", "test"));
        });
    }

}