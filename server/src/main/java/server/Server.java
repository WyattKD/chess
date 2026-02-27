package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import service.ClearService;
import service.GameService;
import service.UserService;
import model.requests.*;
import model.results.*;
import javax.xml.crypto.Data;

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

    private void failureHandler(String failure_msg, Context context) {
        if (failure_msg == null) {
            return;
        } else if (failure_msg.equals("Error: bad request")) {
            context.status(400);
        } else if (failure_msg.equals("Error: unauthorized")) {
            context.status(401);
        } else if (failure_msg.equals("Error: already taken")) {
            context.status(403);
        } else {
            context.status(500);
        }
    }

    private Object clearHandler(@NotNull Context context) throws DataAccessException {
        clearService.clearDatabase();
        context.status(200);
        return "{}";
    }

    private Object registerHandler(@NotNull Context context) throws DataAccessException {
        RegisterRequest request = new Gson().fromJson(context.body(), RegisterRequest.class);
        RegisterResult result = userService.register(request);
        failureHandler(result.message(), context);
        return new Gson().toJson(result);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }



    public void stop() {
        javalin.stop();
    }
}