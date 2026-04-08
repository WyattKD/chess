package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ServerFacade;
import websocket.WebsocketFacade;

import java.util.Collection;
import java.util.Scanner;

import static ui.PostLoginMenu.printChessBoard;

public class LiveMenu {
    private final ServerFacade server;
    private final Scanner s;
    private final boolean black;
    private final WebsocketFacade socket;
    private int gameID;
    private ChessGame currentGame = new ChessGame();

    public LiveMenu(ServerFacade server, Scanner s, boolean black) throws Exception {
        this.server = server;
        this.s = s;
        this.black = black;
        socket = new WebsocketFacade(server.port, this);
    }

    public void run(int gameID) throws Exception {
        this.gameID = gameID;
        this.socket.connect(server.authToken, gameID);
        String input = "";
        while (true) {
            System.out.printf("\n[GAME] >>> ");
            input = s.nextLine();
            if (input.equals("leave")) {
                this.socket.leaveGame(server.authToken, gameID);
                break;
            }
            eval(input.split(" "));
        }
    }

    private void eval(String[] input) {
        switch (input[0].toLowerCase()) {
            case "help":
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "redraw - the chess board");
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "move <BEGIN> <END> <PROMOTION> - a piece, only promote if applicable");
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "resign - from a game");
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "highlight <PIECE> - possible moves");
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "leave - to stop playing");
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "help - print this message");
                break;
            case "redraw":
                renderGame(null, false);
                break;

            case "move":
                if (input.length == 4) {
                    interpretMove(input[1], input[2], input[3]);
                    break;
                } else if (input.length != 3) {
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "incorrect number of arguments");
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "move <BEGIN> <END> <PROMOTION> - a piece, only promote if applicable");
                    break;
                }
                interpretMove(input[1], input[2], null);
                break;

            case "highlight":
                if (input.length != 2) {
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "incorrect number of arguments");
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "highlight <PIECE> - possible moves");
                    break;
                }
                handleHighlight(input[1]);
                break;

            case "resign":
                handleResign();
                break;

            default:
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "unknown command: " + input[0]);
                break;
        }
    }

    public void updateGame(ChessGame game) {
        currentGame = game;
        renderGame(null, true);
    }

    public void renderGame(Collection<ChessMove> validMoves, boolean print) {
        System.out.println("\n");
        printChessBoard(currentGame, !black, validMoves);
        if (print) {
            System.out.printf("\n[GAME] >>> ");
        }
    }

    public void displayNotification(String n) {
        System.out.print(EscapeSequences.ERASE_LINE + EscapeSequences.SET_BG_COLOR_DARK_GREY);
        System.out.println(n + EscapeSequences.RESET_BG_COLOR);
        System.out.printf("\n[GAME] >>> ");
    }

    public void displayError(String e) {
        System.out.print(EscapeSequences.ERASE_LINE + EscapeSequences.SET_BG_COLOR_BLUE);
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + e + EscapeSequences.RESET_BG_COLOR);
        System.out.printf("\n[GAME] >>> ");
    }

    private void handleHighlight(String pos) {
        renderGame(currentGame.validMoves(parsePosition(pos)), false);
    }

    private void handleResign() {
        try {
            System.out.print("Please confirm: (Y)es (N)o >>> ");
            String choice = s.nextLine();
            if (choice.charAt(0) == 'Y' || choice.charAt(0) == 'y') {
                socket.resignGame(server.authToken, gameID);
            } else {
                System.out.println("Cancelling.");
            }
        } catch (Exception e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + e.getMessage());
        }
    }
    private void interpretMove(String from, String to, String piece) {
        try {
            ChessPosition end = parsePosition(to);
            ChessPosition start = parsePosition(from);

            ChessMove move;
            if (piece != null) {
                switch (piece.toLowerCase()) {
                    case "queen":
                        move = new ChessMove(start, end, ChessPiece.PieceType.QUEEN);
                        break;
                    case "rook":
                        move = new ChessMove(start, end, ChessPiece.PieceType.ROOK);
                        break;
                    case "bishop":
                        move = new ChessMove(start, end, ChessPiece.PieceType.BISHOP);
                        break;
                    case "knight":
                        move = new ChessMove(start, end, ChessPiece.PieceType.KNIGHT);
                        break;
                    default:
                        throw new Exception("Invalid promotion piece!");
                }
            } else {
                move = new ChessMove(start, end, null);
            }

            this.socket.makeMove(server.authToken, gameID, move);
        } catch (Exception e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + e.getMessage());
        }
    }

    private ChessPosition parsePosition(String coordinate) {
        if (coordinate.length() != 2) {
            throw new IllegalArgumentException("invalid position format");
        }

        char col = coordinate.charAt(0);
        int row = Character.getNumericValue(coordinate.charAt(1));

        if (col < 'a' || col > 'h' || row < 1 || row > 8) {
            throw new IllegalArgumentException("position out of bounds");
        }

        return new ChessPosition(row, col - 'a' + 1);
    }
}
