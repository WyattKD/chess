package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Server {


    AuthDAO authDAO;
    GameDAO gameDAO;
    UserDAO userDAO;

    static ClearService clearService;
    static GameService gameService;
    static UserService userService;

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        try {

            authDAO = new MemoryAuthDAO();
            gameDAO = new MemoryGameDAO();
            userDAO = new MemoryUserDAO();

            clearService = new ClearService(authDAO, gameDAO, userDAO);
            //gameService = new GameService();
            //userService = new UserService();
        } catch (Exception exception) {
            System.out.println(exception);
        }
        
        javalin.delete("/db", this::clearHandler);

    }

    private Object clearHandler(@NotNull Context context) throws DataAccessException {
        clearService.clearDatabase();
        context.status(200);
        return "{}";
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }



    public void stop() {
        javalin.stop();
    }
}