package dataaccess;


import model.AuthData;
import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class SQLAuthDAO implements dataaccess.AuthDAO {

    public SQLAuthDAO() throws DataAccessException {
        DatabaseManager.createDatabase();
    }

    @Override
    public AuthData createAuth(UserData userData) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        deleteAuth(authToken);
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("INSERT INTO auth (username, authToken) VALUES(?, ?)")) {
                statement.setString(1, userData.username());
                statement.setString(2, authToken);
                statement.executeUpdate();
            }
        } catch (SQLException | DataAccessException exception) {
            throw new DataAccessException(exception.getMessage());
        }
        return new AuthData(authToken, userData.username());
    }

    @Override
    public Boolean deleteAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection() ) {
            try (var statement = conn.prepareStatement("DELETE FROM auth WHERE authToken=?")) {
                statement.setString(1, authToken);
                statement.executeUpdate();
                return true;
            }
        } catch (SQLException | DataAccessException exception) {
            return false;
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT username, authToken FROM auth WHERE authToken=?")) {
                statement.setString(1, authToken);
                try (var results = statement.executeQuery()) {
                    if (results.next()) {
                        var username = results.getString("username");
                        return new AuthData(username, authToken);
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
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("TRUNCATE auth")) {
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException | DataAccessException exception) {
            throw new DataAccessException(exception.getMessage());
        }
    }
}
