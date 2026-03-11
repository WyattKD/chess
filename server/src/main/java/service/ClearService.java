package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.results.ClearResult;

public class ClearService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;


    public ClearService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public ClearResult clearDatabase() throws DataAccessException {
        try {
            authDAO.clear();
            userDAO.clear();
            gameDAO.clear();
            return new ClearResult(null);
        } catch (DataAccessException e) {
            return new ClearResult("Error:" + e);
        }
    }
}