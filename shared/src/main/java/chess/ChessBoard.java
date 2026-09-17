package chess;

import java.util.Arrays;
import java.util.Objects;

import static chess.ChessPiece.PieceType;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() { }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
        addMinorAndMajorPieces(0, ChessGame.TeamColor.WHITE);
        addPawnPieces(1, ChessGame.TeamColor.WHITE);
        addMinorAndMajorPieces(7, ChessGame.TeamColor.BLACK);
        addPawnPieces(6, ChessGame.TeamColor.BLACK);
    }

    private void addPawnPieces(int row, ChessGame.TeamColor teamColor) {
        for (int col = 0; col < 8; col++) {
            ChessPosition pos = new ChessPosition(row + 1, col + 1);
            addPiece(pos, new ChessPiece(teamColor, PieceType.PAWN));
        }
    }

    private void addMinorAndMajorPieces(int row, ChessGame.TeamColor teamColor) {
        ChessPiece.PieceType[] pieces = {PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
                PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK};

        for (int col = 0; col < 8; col++) {
            ChessPosition pos = new ChessPosition(row + 1, col + 1);
            addPiece(pos, new ChessPiece(teamColor, pieces[col]));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
