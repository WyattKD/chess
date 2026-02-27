package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements dataaccess.UserDAO {
    private HashMap<String, UserData> userDataMap = new HashMap<>();

    public MemoryUserDAO() {
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        if (userDataMap.containsKey(userData.username())) {
            throw new DataAccessException("already taken");
        }
        userDataMap.put(userData.username(), new UserData(userData.username(), userData.password(), userData.email()));
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (userDataMap.containsKey(username)) {
            return userDataMap.get(username);
        } else {
            throw new DataAccessException("User does not exist");
        }

    }

    @Override
    public void clear() {
        userDataMap.clear();
    }
}
