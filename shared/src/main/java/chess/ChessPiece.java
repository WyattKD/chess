package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor teamColor;
    private PieceType pieceType;

    // Piece Movement Directions
    private static final int[][] ROOK_DIRS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    private static final int[][] BISHOP_DIRS = {
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };

    private static final int[][] QUEEN_DIRS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };

    private static final int[][] KNIGHT_DIRS = {
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
    };

    private static final int[][] KING_DIRS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.pieceType = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "teamColor=" + teamColor +
                ", pieceType=" + pieceType +
                '}';
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

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (pieceType != PieceType.PAWN) {
            return getMoves(board, myPosition);
        } else {
            return getPawnMoves(board, myPosition);
        }
    }

    private boolean isOnBoard(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    private boolean isOnBoard(ChessPosition pos) {
        return isOnBoard(pos.getRow(), pos.getColumn());
    }

    // Uses piece vectors to calculate possible moves
    private HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();

        int[][] directions;
        boolean sliding = true;

        switch (pieceType) {
            case ROOK -> {
                directions = ROOK_DIRS;
            }
            case BISHOP -> {
                directions = BISHOP_DIRS;
            }
            case QUEEN -> {
                directions = QUEEN_DIRS;
            }
            case KNIGHT -> {
                directions = KNIGHT_DIRS;
                sliding = false;
            }
            case KING -> {
                directions = KING_DIRS;
                sliding = false;
            }
            default -> throw new IllegalStateException();
        }

        for (int[] dir : directions) {
            int row = myPosition.getRow() + dir[0];
            int col = myPosition.getColumn() + dir[1];

            while (isOnBoard(row, col)) {
                ChessPosition targetPos = new ChessPosition(row, col);
                ChessPiece targetPiece = board.getPiece(targetPos);

                if (targetPiece == null) {
                    moves.add(new ChessMove(myPosition, targetPos, null)); // blank space move
                } else {
                    if (targetPiece.teamColor != this.teamColor) {
                        moves.add(new ChessMove(myPosition, targetPos, null)); // capture move
                    }
                    break; // blocked
                }

                if (!sliding) break;

                row += dir[0];
                col += dir[1];
            }
        }

        return moves;
    }
    // Calculates possible pawn moves
    private HashSet<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();

        int direction = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (teamColor == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (teamColor == ChessGame.TeamColor.WHITE) ? 8 : 1;

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // forward one
        ChessPosition oneForward = new ChessPosition(row + direction, col);
        if (isOnBoard(oneForward) && board.getPiece(oneForward) == null) {
            addPawnMove(moves, myPosition, oneForward, promotionRow);

            // forward two
            ChessPosition twoForward = new ChessPosition(row + 2 * direction, col);
            if (row == startRow && board.getPiece(twoForward) == null) {
                moves.add(new ChessMove(myPosition, twoForward, null));
            }
        }

        // captures
        for (int dc : new int[]{-1, 1}) {
            ChessPosition diag = new ChessPosition(row + direction, col + dc);
            if (isOnBoard(diag)) {
                ChessPiece target = board.getPiece(diag);
                if (target != null && target.teamColor != this.teamColor) {
                    addPawnMove(moves, myPosition, diag, promotionRow);
                }
            }
        }

        return moves;
    }
    // Handles possible promotion
    private void addPawnMove(HashSet<ChessMove> moves,
                             ChessPosition from,
                             ChessPosition to,
                             int promotionRow) {

        if (to.getRow() == promotionRow) {
            moves.add(new ChessMove(from, to, PieceType.QUEEN));
            moves.add(new ChessMove(from, to, PieceType.ROOK));
            moves.add(new ChessMove(from, to, PieceType.BISHOP));
            moves.add(new ChessMove(from, to, PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(from, to, null));
        }
    }


}
