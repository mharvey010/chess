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
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            System.out.print("\n" + RESET_TEXT_COLOR + RESET_BG_COLOR + ">>> ");
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }
}
