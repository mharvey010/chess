package client;

import model.*;

public class ChessClient {

    private final ServerFacade server;
    private String authToken = null;
    private String username = null;

    public ChessClient(ServerFacade facade) {
        server = facade;
    }

    public String eval(Command command) {
        try {
            return switch (command.cmd()) {
                case "register" -> register(command.params());
//                case "login" -> login(params);
//                case "logout" -> logout();
//                case "quit" -> "quit";
                case "help" -> help();
                default -> "Unknown command. Please enter a valid command.";
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String register(String... params) throws Exception {
        try {
            UserData user = new UserData(params[0], params[1], params[2]);
            AuthData auth = server.register(user);
            authToken = auth.authToken();
            username = auth.username();
            return String.format("You registered as %s.", username);
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }
//
//    private String login(String... params) throws Exception {
//
//    }

    public String help() {
        if (authToken == null) {
            return """
                    - register <USERNAME> <PASSWORD> <EMAIL>
                    - login <USERNAME> <PASSWORD>
                    - quit
                    - help
                    """;
        }
        return """
                - create <NAME>
                - list
                - join <ID> [WHITE|BLACK]
                - observe <ID>
                - logout
                - quit
                - help
                """;
    }

    public String getAuthToken() {
        return authToken;
    }
}
