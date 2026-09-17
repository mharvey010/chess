package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    public class DirectionVectors {
        public static final int[][] DIAGONALS = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        public static final int[][] STRAIGHTS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        public static final int[][] OMNIDIRECTIONAL = {{1,1}, {1,-1}, {-1,1}, {-1,-1}, {1,0}, {-1,0}, {0,1}, {0,-1}};

        public static final int[][] KNIGHT = {{2,1}, {2,-1}, {-2,1}, {-2,-1}, {1,2}, {1,-2}, {-1,2}, {-1,-2}};
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        if (piece.getPieceType() == PieceType.BISHOP) {
            return calculateMoves(board, myPosition, DirectionVectors.DIAGONALS);
        }
        if (piece.getPieceType() == PieceType.ROOK) {
            return calculateMoves(board, myPosition, DirectionVectors.STRAIGHTS);
        }
        if (piece.getPieceType() == PieceType.QUEEN) {
            return calculateMoves(board, myPosition, DirectionVectors.OMNIDIRECTIONAL);
        }


        return List.of();
    }

    private boolean isOnBoard(int row, int col) {
        return row <= 8 && row >0 && col <= 8 && col >0;
    }

    private void slidingLogic(Collection<ChessMove> moves, int[] direction, ChessBoard board, ChessPosition myPosition, int row, int col) {
        while (isOnBoard(row, col)) {
            ChessPiece pieceAt = board.getPiece(new ChessPosition(row, col));
            if (pieceAt == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            } else {
                if (pieceAt.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                }
                break;
            }
            row += direction[0];
            col += direction[1];
        }
    }

    private Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, int[][] dirVectors) {
        Collection<ChessMove> moves = new ArrayList<>();

        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();
        for (int[] direction : dirVectors) {
            int row = startRow + direction[0];
            int col = startCol + direction[1];

            slidingLogic(moves, direction, board, myPosition, row, col);
        }
        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
