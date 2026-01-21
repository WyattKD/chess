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
        return switch (pieceType) {
            case BISHOP -> getBishopMoves(board, myPosition);
            case KING -> getKingMoves(board, myPosition);
            case KNIGHT -> getKnightMoves(board, myPosition);
            case PAWN -> getPawnMoves(board, myPosition);
            case QUEEN -> getQueenMoves(board, myPosition);
            case ROOK -> getRookMoves(board, myPosition);
        };
    }
    private HashSet<ChessMove> getBishopMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("not implemented");
    }
    private HashSet<ChessMove> getKingMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("not implemented");
    }
    private HashSet<ChessMove> getKnightMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("not implemented");
    }
    private HashSet<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("not implemented");
    }
    private HashSet<ChessMove> getQueenMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("not implemented");
    }
    private HashSet<ChessMove> getRookMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("not implemented");
    }
}
