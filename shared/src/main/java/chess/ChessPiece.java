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
        public static final int[][] PAWN = {{1, 1}, {1, -1}, {1, 0}};
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
        return switch (piece.getPieceType()) {
            case BISHOP -> calculateMoves(board, myPosition, DirectionVectors.DIAGONALS, true);
            case ROOK -> calculateMoves(board, myPosition, DirectionVectors.STRAIGHTS, true);
            case QUEEN -> calculateMoves(board, myPosition, DirectionVectors.OMNIDIRECTIONAL, true);
            case KING -> calculateMoves(board, myPosition, DirectionVectors.OMNIDIRECTIONAL, false);
            case KNIGHT -> calculateMoves(board, myPosition, DirectionVectors.KNIGHT, false);
            case PAWN -> {
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    yield calculatePawnMoves(board, myPosition, 1);
                } else {
                    yield calculatePawnMoves(board, myPosition, -1);
                }
            }
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

    private Collection<ChessMove> calculatePawnMoves(ChessBoard board, ChessPosition myPosition, int upOrDown) {
        Collection<ChessMove> moves = new ArrayList<>();
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();
        for (int[] direction : DirectionVectors.PAWN) {
            int row = startRow + (direction[0] * upOrDown);
            int col = startCol + (direction[1] * upOrDown);
            if (isOnBoard(row, col)) {
                ChessPosition nextPosition = new ChessPosition(row, col);
                ChessPiece pieceAt = board.getPiece(new ChessPosition(row, col));
                if (direction == DirectionVectors.PAWN[2] && pieceAt == null) {
                    addPawnMove(moves, myPosition, nextPosition);
                    if (isOnBoard(row + upOrDown, col) && board.getPiece(new ChessPosition(row + upOrDown, col)) == null && (startRow == 2 || startRow == 7)) {
                        addPawnMove(moves, myPosition, new ChessPosition(row + upOrDown, col));
                    }
                } else if (direction != DirectionVectors.PAWN[2]) {
                    if (pieceAt != null && pieceAt.getTeamColor() != pieceColor) {
                        addPawnMove(moves, myPosition, nextPosition);
                    }
                }
            }

        }
        return moves;
    }

    private void stepLogic(Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition, int row, int col) {
        if (isOnBoard(row, col)) {
            ChessPosition nextPosition = new ChessPosition(row, col);
            ChessPiece pieceAt = board.getPiece(nextPosition);
            if (pieceAt == null) {
                moves.add(new ChessMove(myPosition, nextPosition, null));
            } else {
                if (pieceAt.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(myPosition, nextPosition, null));
                }
            }
        }
    }

    private void slidingLogic(Collection<ChessMove> moves, int[] direction, ChessBoard board, ChessPosition myPosition, int row, int col) {
        while (isOnBoard(row, col)) {
            ChessPosition nextPosition = new ChessPosition(row, col);
            ChessPiece pieceAt = board.getPiece(nextPosition);
            if (pieceAt == null) {
                moves.add(new ChessMove(myPosition, nextPosition, null));
            } else {
                if (pieceAt.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(myPosition, nextPosition, null));
                }
                break;
            }
            row += direction[0];
            col += direction[1];
        }
    }

    private Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, int[][] dirVectors, boolean slide) {
        Collection<ChessMove> moves = new ArrayList<>();

        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();
        for (int[] direction : dirVectors) {
            int row = startRow + direction[0];
            int col = startCol + direction[1];
            if (slide) {
                slidingLogic(moves, direction, board, myPosition, row, col);
            } else {
                stepLogic(moves, board, myPosition, row, col);
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
