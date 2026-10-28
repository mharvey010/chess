package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import dataaccess.*;
import model.*;
import org.jetbrains.annotations.NotNull;
import service.*;
import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final UserDAO userDAO = new MemoryUserDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);
    private final UserService userService = new UserService(userDAO, authDAO);
    private final Gson gson = new Gson();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", this::clearApplication);
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);


        javalin.exception(BadRequestException.class, (e, ctx) ->
                ctx.status(400).json(gson.toJson(Map.of("message", "Error: bad request"))));
        javalin.exception(AlreadyTakenException.class, (e, ctx) ->
                ctx.status(403).json(gson.toJson(Map.of("message", "Error: already taken"))));
        javalin.exception(UnauthorizedException.class, (e, ctx) ->
                ctx.status(401).json(gson.toJson(Map.of("message", "Error: unauthorized"))));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(Context ctx) throws Exception {
        AuthData result = userService.register(gson.fromJson(ctx.body(), UserData.class));
        ctx.result(gson.toJson(result));
    }

    private void login(Context ctx) throws Exception {
        AuthData result = userService.login(gson.fromJson(ctx.body(), UserData.class));
        ctx.result(gson.toJson(result));
    }

    private void logout(Context ctx) throws Exception{
        userService.logout(ctx.header("authorization"));
        ctx.json("{}");
    }

    private void clearApplication(Context ctx) {
        try {
            clearService.clear();
            ctx.json("{}");
        } catch (DataAccessException e) {
            ctx.status(500);
        }
    }

}
