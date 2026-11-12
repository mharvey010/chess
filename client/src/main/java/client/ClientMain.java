package client;

import chess.*;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        String serverUrl = "http://localhost:8080";
        ServerFacade server = new ServerFacade(serverUrl);
        ChessClient client = new ChessClient(server);
        REPL repl = new REPL(client);
        repl.run();
    }
}
