package service;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import model.results.*;
import model.requests.*;

public class UserService {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) {
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            return new RegisterResult(null, null, "Error: bad request");
        }
        try {
            UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
            AuthData authData = authDAO.createAuth(user);
            userDAO.createUser(user);
            return new RegisterResult(user.username(), authData.authToken(), null);
        } catch (DataAccessException exception) {
            return new RegisterResult(null, null, "Error: " + exception.getMessage());
        }
    }
    //public LoginResult login(LoginRequest loginRequest) {}
    //public void logout(LogoutRequest logoutRequest) {}
}
