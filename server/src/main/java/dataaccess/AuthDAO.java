package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    AuthData createAuth(UserData userData) throws DataAccessException;
    void deleteAuth(String authToken);
    AuthData getAuth(String authToken) throws DataAccessException;

    void clear() throws DataAccessException;
}
