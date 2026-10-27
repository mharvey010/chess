package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import dataaccess.*;
import model.*;
import service.*;

public class Server {

    private final Javalin javalin;
    private final UserDAO userDAO = new MemoryUserDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);
    private final UserService userService = new UserService(userDAO, authDAO);

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", this::clearApplication);
        javalin.post("/user", this::register);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(Context ctx) {
        try {
            AuthData result = userService.register(new Gson().fromJson(ctx.body(), UserData.class));
            ctx.result(new Gson().toJson(result));
        } catch (DataAccessException e) {

        }

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
