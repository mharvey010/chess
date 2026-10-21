package chess;

import java.util.ArrayList;
import java.util.Collection;
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

    public static class DirectionVectors {
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
        return switch (this.type) {
            case BISHOP -> calculateMoves(board, myPosition, DirectionVectors.DIAGONALS, 7);
            case ROOK -> calculateMoves(board, myPosition, DirectionVectors.STRAIGHTS, 7);
            case QUEEN -> calculateMoves(board, myPosition, DirectionVectors.OMNIDIRECTIONAL, 7);
            case KING -> calculateMoves(board, myPosition, DirectionVectors.OMNIDIRECTIONAL, 1);
            case KNIGHT -> calculateMoves(board, myPosition, DirectionVectors.KNIGHT, 1);
            case PAWN -> calculatePawnMoves(board, myPosition);
        };
    }

    private boolean isOnBoard(int row, int col) {
        return row <= 8 && row >0 && col <= 8 && col >0;
    }

    private void addPawnMove(Collection<ChessMove> moves, ChessPosition start, ChessPosition end) {
        if (end.getRow() == 1 || end.getRow() == 8) {
            moves.add(new ChessMove(start, end, PieceType.QUEEN));
            moves.add(new ChessMove(start, end, PieceType.ROOK));
            moves.add(new ChessMove(start, end, PieceType.BISHOP));
            moves.add(new ChessMove(start, end, PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(start, end, null));
        }
    }

    Collection<ChessMove> calculatePawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        boolean isWhite = pieceColor == ChessGame.TeamColor.WHITE;
        int direction = isWhite ? 1 : -1;
        int pawnBaseRow = isWhite ? 2 : 7;
        int rowAhead = row + direction;

        if (isOnBoard(rowAhead, col) && board.getPiece(new ChessPosition(rowAhead, col)) == null) {
            addPawnMove(moves, myPosition, new ChessPosition(rowAhead, col));
            int twoAhead = rowAhead + direction;
            if (row == pawnBaseRow && board.getPiece(new ChessPosition(twoAhead, col)) == null) {
                addPawnMove(moves, myPosition, new ChessPosition(twoAhead, col));
            }
        }

        for (int diagonal : new int[]{1, -1}) {
            int colAhead = col + diagonal;
            if (isOnBoard(rowAhead, colAhead)) {
                ChessPosition nextPosition = new ChessPosition(rowAhead, colAhead);
                ChessPiece pieceAt = board.getPiece(nextPosition);
                if (pieceAt != null && pieceAt.getTeamColor() != pieceColor) {
                    addPawnMove(moves, myPosition, nextPosition);
                }
            }
        }

        return moves;
    }

    Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, int[][] dirVectors, int maxSteps) {
        Collection<ChessMove> moves = new ArrayList<>();
        for (int[] direction : dirVectors) {
            for (int step = 1; step <= maxSteps; step++) {
                int row = myPosition.getRow() + direction[0] * step;
                int col = myPosition.getColumn() + direction[1] * step;
                if (!isOnBoard(row, col)) {
                    break;
                }

                ChessPosition nextPosition = new ChessPosition(row, col);
                ChessPiece pieceAt = board.getPiece(nextPosition);
                if (pieceAt == null || pieceAt.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(myPosition, nextPosition, null));
                }
                if (pieceAt != null) {
                    break;
                }
            }
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
