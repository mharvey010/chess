package client;

import model.Command;
import model.EvalResponse;
import java.util.Arrays;
import java.util.Scanner;
import static ui.EscapeSequences.*;
import static model.EvalResponse.ResultType.*;

public class REPL {
    private final ChessClient client;

    public REPL(ChessClient client) {
        this.client = client;
    }

    public void run() {
        System.out.println("Chess Game :)");
        System.out.print(client.help().data());

        Scanner scanner = new Scanner(System.in);
        EvalResponse result = new EvalResponse(SUCCESS, "");
        while (result.type() != QUIT) {
            String loginStatus = client.getAuthToken() != null ?
                    SET_TEXT_COLOR_BLUE + "[LOGGED IN] " :
                    SET_TEXT_COLOR_YELLOW + "[LOGGED OUT] ";
            System.out.print("\n" + loginStatus + RESET_TEXT_COLOR + RESET_BG_COLOR + ">>> ");
            String line = scanner.nextLine();
            try {
                String[] tokens = line.toLowerCase().split(" ");
                String cmd = tokens[0];
                String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
                if (validCommand(cmd, params)) {
                    result = client.eval(new Command(cmd, params));
                    System.out.print(result.data());
                } else {
                    System.out.print("Command or arguments invalid. Please enter a valid command.");
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private boolean validCommand(String cmd, String... params) {
        return switch (cmd) {
            case "help", "quit", "logout", "list" -> params.length == 0;
            case "login" -> params.length == 2;
            case "register" -> params.length == 3;
            case "create" -> params.length == 1;
            case "join" -> params.length == 2 && isInteger(params[0]);
            case "observe" -> params.length == 1 && isInteger(params[0]);
            default -> false;
        };
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
