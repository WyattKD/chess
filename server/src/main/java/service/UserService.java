package service;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import model.results.*;
import model.requests.*;
import org.mindrot.jbcrypt.BCrypt;

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
    public LoginResult login(LoginRequest loginRequest) {
        try {

            if (loginRequest.username() == null || loginRequest.password() == null) {
                return new LoginResult(null, null, "Error: bad request");
            }
            UserData user = userDAO.getUser(loginRequest.username());
            if (!BCrypt.checkpw(loginRequest.password(), user.password())) {
                return new LoginResult(null, null, "Error: unauthorized");
            }
            AuthData authData = authDAO.createAuth(user);

            return new LoginResult(user.username(), authData.authToken(), null);
        } catch (DataAccessException exception) {
            return new LoginResult(null, null, "Error: " + exception.getMessage());
        }
    }
    public LogoutResult logout(LogoutRequest logoutRequest) {
        try {
            authDAO.deleteAuth(logoutRequest.authToken());
            return new LogoutResult(null);
        } catch (DataAccessException exception) {
            if (exception.getMessage().contains("connection")) {
                return new LogoutResult("Error: " + exception.getMessage());
            } else {
                return new LogoutResult("Error: unauthorized");
            }
        }


    }
}
