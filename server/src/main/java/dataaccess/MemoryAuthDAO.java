package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements dataaccess.AuthDAO {
    private HashMap<String, AuthData> authDataMap = new HashMap<>();

    public MemoryAuthDAO() {
    }

    @Override
    public AuthData createAuth(UserData userData) {
        String authToken = generateToken();
        AuthData authData = new AuthData(userData.username(), authToken);
        authDataMap.put(authToken, authData);

        return authData;
    }

    @Override
    public Boolean deleteAuth(String authToken) throws DataAccessException {
        if (authDataMap.containsKey(authToken)) {
            authDataMap.remove(authToken);
            return true;
        } else {
            throw new DataAccessException("unauthorized");
        }

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (authDataMap.containsKey(authToken)) {
            return authDataMap.get(authToken);
        } else {
            throw new DataAccessException("unauthorized");
        }

    }

    @Override
    public void clear() {
        authDataMap.clear();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}