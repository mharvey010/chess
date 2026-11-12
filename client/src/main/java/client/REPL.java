package client;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class REPL {
    private final ChessClient client;

    public REPL(ChessClient client) {
        this.client = client;
    }

    public void run() {
        System.out.println("Chess Game :)");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            System.out.print("\n" + RESET_TEXT_COLOR + RESET_BG_COLOR + ">>> ");
            result = scanner.nextLine();
            if (result.equals("register")) {
                System.out.print("congrats you have registered");
            }
            if (result.equals("help")) {
                System.out.print(help());
            }
//            try {
//                result = client.eval(line);
//                System.out.print(result);
//            } catch (Throwable e) {
//                var msg = e.toString();
//                System.out.print(msg);
//            }
        }
        System.out.println();
    }

    public String help() {
        if (true) {
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
