package client;

import model.*;
import java.util.Collection;
import static model.EvalResponse.ResultType.*;

public class ChessClient {

    private final ServerFacade server;
    private String authToken = null;
    private String username = null;
    private GameData currGame;
    private String userRole;

    public ChessClient(ServerFacade facade) {
        server = facade;
    }

    public EvalResponse eval(Command command) {
        try {
            return switch (command.cmd()) {
                case "register" -> register(command.params());
                case "login" -> login(command.params());
                case "create" -> createGame(command.params());
                case "list" -> listGames();
                case "join" -> joinGame(command.params());
                case "observe" -> observe(command.params());
                case "logout" -> logout();
                case "quit" -> new EvalResponse(QUIT, "quit");
                case "help" -> help();
                default -> new EvalResponse(ERROR, "Unknown command. Please enter a valid command.");
            };
        } catch (Exception ex) {
            return new EvalResponse(ERROR, ex.getMessage());
        }
    }

    private EvalResponse register(String... params) {
        try {
            UserData user = new UserData(params[0], params[1], params[2]);
            AuthData auth = server.register(user);
            authToken = auth.authToken();
            username = auth.username();
            return new EvalResponse(SUCCESS, String.format("You registered as %s.", username));
        } catch (Exception ex) {
            return new EvalResponse(ERROR, ex.getMessage());
        }
    }

    private EvalResponse login(String... params) {
        try {
            UserData user = new UserData(params[0], params[1], null);
            AuthData auth = server.login(user);
            authToken = auth.authToken();
            username = auth.username();
            return new EvalResponse(SUCCESS, String.format("You are logged in as %s.", username));
        } catch (Exception ex) {
            return new EvalResponse(ERROR, ex.getMessage());
        }
    }

    private EvalResponse logout() throws Exception {
        assertLoggedIn();
        try {
            server.logout(authToken);
            authToken = null;
            username = null;
            return new EvalResponse(SUCCESS, "Logging out...");
        } catch (Exception ex) {
            return new EvalResponse(ERROR, ex.getMessage());
        }
    }

    private EvalResponse createGame(String... params) throws Exception {
        assertLoggedIn();
        try {
            int gameID = server.createGame(authToken, new CreateGameRequest(params[0]));
            return new EvalResponse(SUCCESS, String.format("The game %s has been created with ID - %d.", params[0], gameID));
        } catch (Exception ex) {
            return new EvalResponse(ERROR, ex.getMessage());
        }
    }

    private EvalResponse joinGame(String... params) throws Exception {
        assertLoggedIn();
        try {
            server.joinGame(authToken, new JoinGameRequest(params[1].toUpperCase(), Integer.parseInt(params[0])));
            currGame = getGame(Integer.parseInt(params[0]));
            userRole = "PLAYER";
            return new EvalResponse(BOARD, currGame);
        } catch (Exception ex) {
            return new EvalResponse(ERROR, ex.getMessage());
        }
    }

    private EvalResponse observe(String... params) throws Exception {
        assertLoggedIn();
        try {
            GameData game = getGame(Integer.parseInt(params[0]));
            userRole = "OBSERVER";
            return new EvalResponse(BOARD, game);
        } catch (Exception ex) {
            return new EvalResponse(ERROR, ex.getMessage());
        }
    }

    private EvalResponse listGames() throws Exception {
        assertLoggedIn();
        try {
            Collection<GameData> games = server.listGames(authToken);
            return new EvalResponse(GAMES_LIST, games);
        } catch (Exception ex) {
            return new EvalResponse(ERROR, ex.getMessage());
        }
    }

    private GameData getGame(int id) throws Exception {
        for (GameData game : server.listGames(authToken)) {
            if (game.gameID() == id) {
                return game;
            }
        }
        throw new Exception("Game not found");
    }

    public EvalResponse help() {
        if (authToken == null) {
            return new EvalResponse(SUCCESS, """
                    
                    - register <USERNAME> <PASSWORD> <EMAIL>
                    - login <USERNAME> <PASSWORD>
                    - quit
                    - help
                    """);
        }
        return new EvalResponse(SUCCESS, """
                
                - create <NAME>
                - list
                - join <ID> [WHITE|BLACK]
                - observe <ID>
                - logout
                - quit
                - help
                """);
    }

    private void assertLoggedIn() throws Exception {
        if (authToken == null) {
            throw new Exception("You need to sign in");
        }
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }

    public String getUserRole() {
        return userRole;
    }
}
