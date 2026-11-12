package client;

import model.AuthData;
import model.UserData;

import java.util.Arrays;

public class ChessClient {

    private final ServerFacade server;
    private String authToken = null;
    private String username = null;

    public ChessClient(ServerFacade facade) {
        server = facade;
    }

    public String eval(String line) {
        try {
            String[] tokens = line.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
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
        if (params.length == 3) {
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
        throw new Exception();
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
                - list
                - adopt <pet id>
                - rescue <name> <CAT|DOG|FROG|FISH>
                - adoptAll
                - signOut
                - quit
                """;
    }
}
