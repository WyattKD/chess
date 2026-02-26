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
            throw new DataAccessException("User already exists");
        }
        userDataMap.put(userData.username(), new UserData(userData.username(), userData.password(), userData.email()));
    }

    @Override
    public UserData getUser(String username) {
        return userDataMap.get(username);
    }

    @Override
    public void clear() {
        userDataMap.clear();
    }
}
