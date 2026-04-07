package ui;

import chess.ChessGame;
import chess.ChessMove;
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
        socket = new WebsocketFacade();
    }

    public void run(int gameID) throws Exception {
        this.gameID = gameID;
        // this.socket.connect(client.server.authToken, gameID);
        String input = "";
        while (true) {
            System.out.printf("\n[GAME] >>> ");
            input = s.nextLine();
            if (input.equals("leave")) {
                // this.socket.leaveGame(client.server.authToken, gameID);
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
                // renderGame();
                break;

            case "move":
                if (input.length == 4) {
                    // interpretMove();
                    break;
                } else if (input.length != 3) {
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "incorrect number of arguments");
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "move <BEGIN> <END> <PROMOTION> - a piece, only promote if applicable");
                    break;
                }
                // interpretMove();
                break;

            case "highlight":
                if (input.length != 2) {
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "incorrect number of arguments");
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "highlight <PIECE> - possible moves");
                    break;
                }
                // handleHighlight();
                break;

            case "resign":
                // handleResign();
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
        printChessBoard(currentGame, black, validMoves);
        if (print) {
            System.out.printf("\n[GAME] >>> ");
        }
    }

    public void displayNotification(String n) {
        System.out.print(EscapeSequences.ERASE_LINE + EscapeSequences.SET_BG_COLOR_BLUE);
        System.out.println(n + EscapeSequences.RESET_BG_COLOR);
        System.out.printf("\n[GAME] >>> ");
    }

    public void displayError(String e) {
        System.out.print(EscapeSequences.ERASE_LINE + EscapeSequences.SET_BG_COLOR_BLUE);
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + e + EscapeSequences.RESET_BG_COLOR);
        System.out.printf("\n[GAME] >>> ");
    }

    private void handleHighlight(String pos) {
        // renderGame(currentGame.validMoves(parsePosition(pos)), false);
    }

    private void handleResign() {
        try {
            System.out.print("Please confirm: (Y)es (N)o >>> ");
            String choice = s.nextLine();
            if (choice.charAt(0) == 'Y' || choice.charAt(0) == 'y') {
                // socket.resignGame(client.server.authToken, gameID);
            } else {
                System.out.println("OK, cancelling");
            }
        } catch (Exception e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + e.getMessage());
        }
    }

}
