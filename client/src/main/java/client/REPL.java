package client;

import model.Command;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class REPL {
    private final ChessClient client;

    public REPL(ChessClient client) {
        this.client = client;
    }

    public void run() {
        System.out.println("Chess Game :)");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            System.out.print("\n" + RESET_TEXT_COLOR + RESET_BG_COLOR + ">>> ");
            String line = scanner.nextLine();
            try {
                String[] tokens = line.toLowerCase().split(" ");
                String cmd = tokens[0];
                String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
                if (validCommand(cmd, params)) {
                    result = client.eval(new Command(cmd, params));
                    System.out.print(result);
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
            case "login", "join" -> params.length == 2;
            case "register" -> params.length == 3;
            case "create", "observe" -> params.length == 1;
            default -> false;
        };
    }
}
