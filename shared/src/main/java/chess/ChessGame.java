package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor currentTurn = TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTurn = team;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return currentTurn == chessGame.currentTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTurn, board);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            return null;
        }

        Collection<ChessMove> pseudoMoves =
                piece.pieceMoves(board, startPosition);

        Collection<ChessMove> legalMoves = new HashSet<>();

        for (ChessMove move : pseudoMoves) {
            if (!leavesKingInCheck(move, piece.getTeamColor())) {
                legalMoves.add(move);
            }
        }

        return legalMoves;
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());

        if (piece == null) {
            throw new InvalidMoveException("Can't move empty space");
        }

        if (piece.getTeamColor() != currentTurn) {
            throw new InvalidMoveException("Can't move enemy piece");
        }

        Collection<ChessMove> legalMoves = validMoves(move.getStartPosition());
        if (!legalMoves.contains(move)) {
            throw new InvalidMoveException("Not a valid move");
        }

        board.removePiece(move.getStartPosition());

        if (move.getPromotionPiece() != null) {
            board.addPiece(move.getEndPosition(),
                    new ChessPiece(currentTurn, move.getPromotionPiece()));
        } else {
            board.addPiece(move.getEndPosition(), piece);
        }

        currentTurn = currentTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKing(teamColor);
        return isSquareAttacked(kingPos, teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && !hasAnyAvailableMoves(teamColor);
    }


    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && !hasAnyAvailableMoves(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param new_board the new board to use
     */
    public void setBoard(ChessBoard new_board) {
        board = new_board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private ChessPosition findKing(TeamColor team) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                if (piece != null &&
                        piece.getTeamColor() == team &&
                        piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return pos;
                }
            }
        }
        throw new IllegalStateException("Somehow there is not a king on the board");
    }

    private boolean isSquareAttacked(ChessPosition square, TeamColor byTeam) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition from = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(from);

                if (piece == null || piece.getTeamColor() != byTeam) {
                    continue;
                }

                if (pieceAttacksSquare(piece, from, square)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean pieceAttacksSquare(ChessPiece piece, ChessPosition from, ChessPosition to) {
        int dr = Integer.compare(to.getRow(), from.getRow());
        int dc = Integer.compare(to.getColumn(), from.getColumn());

        return switch (piece.getPieceType()) {
            case PAWN -> pawnAttacks(from, to, piece.getTeamColor());
            case KNIGHT -> knightAttacks(from, to);
            case BISHOP -> Math.abs(from.getRow() - to.getRow()) ==
                    Math.abs(from.getColumn() - to.getColumn()) &&
                    slidingAttacks(from, to, dr, dc);
            case ROOK -> (from.getRow() == to.getRow() ||
                    from.getColumn() == to.getColumn()) &&
                    slidingAttacks(from, to, dr, dc);
            case QUEEN -> ((from.getRow() == to.getRow() ||
                    from.getColumn() == to.getColumn()) ||
                    Math.abs(from.getRow() - to.getRow()) ==
                            Math.abs(from.getColumn() - to.getColumn())) &&
                    slidingAttacks(from, to, dr, dc);
            case KING -> Math.max(
                    Math.abs(from.getRow() - to.getRow()),
                    Math.abs(from.getColumn() - to.getColumn())
            ) == 1;
            default -> false;
        };
    }

    private boolean pawnAttacks(ChessPosition from, ChessPosition to, TeamColor color) {
        int dir = (color == TeamColor.WHITE) ? 1 : -1;

        return to.getRow() == from.getRow() + dir &&
                Math.abs(to.getColumn() - from.getColumn()) == 1;
    }

    private boolean knightAttacks(ChessPosition from, ChessPosition to) {
        int dr = Math.abs(from.getRow() - to.getRow());
        int dc = Math.abs(from.getColumn() - to.getColumn());
        return (dr == 2 && dc == 1) || (dr == 1 && dc == 2);
    }

    private boolean slidingAttacks(ChessPosition from, ChessPosition to, int dr, int dc) {
        int row = from.getRow() + dr;
        int col = from.getColumn() + dc;

        while (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPosition cur = new ChessPosition(row, col);

            if (cur.equals(to)) {
                return true;
            }

            if (board.getPiece(cur) != null) {
                return false;
            }

            row += dr;
            col += dc;
        }
        return false;
    }

    private boolean leavesKingInCheck(ChessMove move, TeamColor team) {
        ChessBoard potentialBoard = new ChessBoard(); // copy constructor assumed
        ChessPiece piece = potentialBoard.getPiece(move.getStartPosition());

        potentialBoard.removePiece(move.getStartPosition());
        potentialBoard.addPiece(move.getEndPosition(), piece);

        ChessPosition kingPos = findKing(team);
        return isSquareAttacked(kingPos, team == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
    }

    private boolean hasAnyAvailableMoves(TeamColor team) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                if (piece == null || piece.getTeamColor() != team) {
                    continue;
                }

                Collection<ChessMove> moves = validMoves(pos);

                if (moves != null && !moves.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }


}
