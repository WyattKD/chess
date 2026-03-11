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

            authDAO = new SQLAuthDAO();
            gameDAO = new SQLGameDAO();
            userDAO = new SQLUserDAO();

            clearService = new ClearService(authDAO, gameDAO, userDAO);
            gameService = new GameService(authDAO, gameDAO);
            userService = new UserService(authDAO, userDAO);
        } catch (Exception exception) {
            System.out.println(exception);
        }
        
        javalin.delete("/db", this::clearHandler);
        javalin.post("/user", this::registerHandler);
        javalin.post("/session", this::loginHandler);
        javalin.delete("/session", this::logoutHandler);
        javalin.post("/game", this::createGameHandler);
        javalin.get("/game", this::listGameHandler);
        javalin.put("/game", this::joinGameHandler);
    }

    private void failureHandler(String failureMsg, Context context) {
        if (failureMsg == null) {
            context.status(200);
        } else if (failureMsg.equals("Error: bad request")) {
            context.status(400);
        } else if (failureMsg.equals("Error: unauthorized")) {
            context.status(401);
        } else if (failureMsg.equals("Error: already taken")) {
            context.status(403);
        } else {
            context.status(500);
        }
    }

    private void clearHandler(@NotNull Context context) throws DataAccessException {
        ClearResult result = clearService.clearDatabase();
        failureHandler(result.message(), context);
        context.result(new Gson().toJson(result));
    }

    private void registerHandler(@NotNull Context context) {
        RegisterRequest request = new Gson().fromJson(context.body(), RegisterRequest.class);
        RegisterResult result = userService.register(request);
        failureHandler(result.message(), context);
        context.result(new Gson().toJson(result));
    }

    private void loginHandler(@NotNull Context context) {
        LoginRequest request = new Gson().fromJson(context.body(), LoginRequest.class);
        LoginResult result = userService.login(request);
        failureHandler(result.message(), context);
        context.result(new Gson().toJson(result));
    }

    private void logoutHandler(@NotNull Context context) {
        LogoutRequest request = new LogoutRequest(context.header("authorization"));
        LogoutResult result = userService.logout(request);
        failureHandler(result.message(), context);
        context.result(new Gson().toJson(result));
    }

    private void createGameHandler(@NotNull Context context) {
        String gameName = new Gson().fromJson(context.body(), CreateGameRequest.class).gameName();
        CreateGameRequest request = new CreateGameRequest(context.header("Authorization"), gameName);
        CreateGameResult result = gameService.createGame(request);
        failureHandler(result.message(), context);
        context.result(new Gson().toJson(result));
    }

    private void listGameHandler(@NotNull Context context) {
        ListGamesRequest request = new ListGamesRequest(context.header("Authorization"));
        ListGamesResult result = gameService.listGames(request);
        failureHandler(result.message(), context);
        context.result(new Gson().toJson(result));
    }

    private void joinGameHandler(@NotNull Context context) {
        JoinGameRequest base = new Gson().fromJson(context.body(), JoinGameRequest.class);
        JoinGameRequest request = new JoinGameRequest(context.header("Authorization"), base.playerColor(), base.gameID());
        JoinGameResult result = gameService.joinGame(request);
        failureHandler(result.message(), context);
        context.result(new Gson().toJson(result));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }



    public void stop() {
        javalin.stop();
    }
}