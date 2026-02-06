package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] boardGrid;

    public ChessBoard() {
        boardGrid = new ChessPiece[8][8];
    }


    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        boardGrid[position.getColumn() - 1][position.getRow() - 1] = piece;
    }

    /**
     * Removes a chess piece from the chessboard
     *
     * @param position where to remove the piece from
     */
    public void removePiece(ChessPosition position) {
        boardGrid[position.getColumn() - 1][position.getRow() - 1] = null;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return boardGrid[position.getColumn() - 1][position.getRow() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        boardGrid = new ChessPiece[8][8];

        addAllPieces(ChessGame.TeamColor.WHITE);

        addAllPieces(ChessGame.TeamColor.BLACK);

    }

    private void addAllPieces(ChessGame.TeamColor team) {
        var row = 1;
        var pawnRow = 2;
        if (team == ChessGame.TeamColor.BLACK) {
            row = 8;
            pawnRow = 7;
        }

        // Non-pawn Pieces
        addPiece(new ChessPosition(row, 1), new ChessPiece(team, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(row, 2), new ChessPiece(team, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 3), new ChessPiece(team, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 4), new ChessPiece(team, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(row, 5), new ChessPiece(team, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(row, 6), new ChessPiece(team, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 7), new ChessPiece(team, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 8), new ChessPiece(team, ChessPiece.PieceType.ROOK));

        // Pawns
        addPiece(new ChessPosition(pawnRow, 1), new ChessPiece(team, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(pawnRow, 2), new ChessPiece(team, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(pawnRow, 3), new ChessPiece(team, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(pawnRow, 4), new ChessPiece(team, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(pawnRow, 5), new ChessPiece(team, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(pawnRow, 6), new ChessPiece(team, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(pawnRow, 7), new ChessPiece(team, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(pawnRow, 8), new ChessPiece(team, ChessPiece.PieceType.PAWN));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(boardGrid, that.boardGrid);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(boardGrid);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "boardGrid=" + Arrays.toString(boardGrid) +
                '}';
    }
}
