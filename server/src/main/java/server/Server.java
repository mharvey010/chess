package server;

import io.javalin.*;
import io.javalin.http.Context;
import dataaccess.*;
import service.*;

public class Server {

    private final Javalin javalin;
    private final UserDAO userDAO = new MemoryUserDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", this::clearApplication);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void clearApplication(Context ctx) {
        try {
            clearService.clear();
            ctx.status(200);
        } catch (DataAccessException dataAccessException) {
            ctx.status(500);
        }
    }

}
