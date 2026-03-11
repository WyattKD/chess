package dataaccess;

import java.sql.SQLException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

public class SQLUserDAO implements UserDAO {
    public SQLUserDAO() {
        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var conn = DatabaseManager.getConnection()) {
            var createTestTable = """            
                    CREATE TABLE if NOT EXISTS user (
                                    username VARCHAR(255) NOT NULL,
                                    password VARCHAR(255) NOT NULL,
                                    email VARCHAR(255),
                                    PRIMARY KEY (username)
                                    )""";
            try (var createTableStatement = conn.prepareStatement(createTestTable)) {
                createTableStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {

        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT username, password, email FROM user WHERE username=?")) {
                statement.setString(1, username);
                try (var results = statement.executeQuery()) {
                    if (results.next()) {
                        var password = results.getString("password");
                        var email = results.getString("email");
                        return new UserData(username, password, email);
                    } else {
                        throw new DataAccessException("unauthorized");
                    }
                }
            }
        } catch (SQLException exception) {
            throw new DataAccessException(exception.getMessage());
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("INSERT INTO user (username, password, email) VALUES(?, ?, ?)")) {
                String password = BCrypt.hashpw(user.password(), BCrypt.gensalt());
                statement.setString(1, user.username());
                statement.setString(2, password);
                statement.setString(3, user.email());
                statement.executeUpdate();
            }
        } catch (SQLException exception) {
            if (exception.getMessage().contains("Duplicate")) {
                throw new DataAccessException("already taken");
            } else {
                throw new DataAccessException(exception.getMessage());
            }

        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("TRUNCATE user")) {
                statement.executeUpdate();
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        } catch (SQLException | DataAccessException exception) {
            throw new DataAccessException(exception.getMessage());
        }
    }

}
