package client;

import model.*;
import chess.*;
import static chess.ChessGame.TeamColor.*;
import static chess.ChessPiece.PieceType.*;
import java.util.Collection;
import java.util.Arrays;
import java.util.Objects;
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
//                    System.out.print(result.data());
                    handleResponse(result);
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

    private void handleResponse(EvalResponse response) {
        switch (response.type()) {
            case ERROR -> System.out.println(SET_TEXT_COLOR_RED + response.data());
            case SUCCESS -> System.out.println(SET_TEXT_COLOR_GREEN + response.data());
            case GAMES_LIST -> printGamesList((Collection<GameData>) response.data());
            case BOARD -> printBoard((GameData) response.data());
        }
    }

    private void printGamesList(Collection<GameData> games) {
        if (games.isEmpty()) {
            System.out.println("No games");
        }
        System.out.println();
        for (GameData game : games) {
            int id = game.gameID();
            String name = game.gameName();
            String white = game.whiteUsername() == null ? "EMPTY" : game.whiteUsername();
            String black = game.blackUsername() == null ? "EMPTY" : game.blackUsername();
            System.out.printf("   %d - %s | WHITE PLAYER: %s | BLACK PLAYER: %s\n", id, name, white, black);
        }
    }

    private void printBoard(GameData gameInfo) {
        ChessGame game = gameInfo.game();
        ChessBoard board = game.getBoard();

        boolean isWhite = Objects.equals(client.getUsername(), gameInfo.whiteUsername()) ||
                          Objects.equals(client.getUserRole(), "OBSERVER");

        printHeader(isWhite);
        for (int i = 0; i < 8; i++) {
            int row = isWhite ? (7 - i) : i;
            System.out.print(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLACK);
            System.out.print(" " + (row + 1) + " "); // Left border: rank #

            for (int j = 0; j < 8; j++) {
                int col = isWhite ? j : (7 - j);
                ChessPosition pos = new ChessPosition(row + 1, col + 1);
                ChessPiece piece = board.getPiece(pos);
                String pieceColor;
                String pieceType = " ";
                if (piece != null) {
                    pieceColor = piece.getTeamColor() == WHITE ? SET_TEXT_COLOR_RED : SET_TEXT_COLOR_BLUE;
                    pieceType = pieceString(piece.getPieceType());
                    System.out.print(pieceColor);
                }
                String squareColor = (row + col) % 2 == 1 ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_BLACK;
                System.out.print(squareColor);
                System.out.print(" " + pieceType + " ");
            }
            System.out.print(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLACK);
            System.out.print(" " + (row + 1) + " ");
            System.out.println(RESET_BG_COLOR);
        }
        printHeader(isWhite);
    }

    private void printHeader(boolean isWhite) {
        System.out.print(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLACK);
        System.out.print("   ");
        for (int i = 0; i < 8; i++) {
            if (isWhite) {
                System.out.print(" " + (char)('a' + i) + " ");
            } else {
                System.out.print(" " + (char)('h' - i) + " ");
            }
        }
        System.out.print("   ");
        System.out.println(RESET_BG_COLOR);
    }

    private String pieceString(ChessPiece.PieceType type) {
        return switch (type) {
            case KING -> "K";
            case QUEEN -> "Q";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case ROOK -> "R";
            case PAWN -> "P";
        };
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
