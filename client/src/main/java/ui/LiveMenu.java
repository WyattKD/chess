package ui;

import client.ServerFacade;
import websocket.WebsocketFacade;
import java.util.Scanner;

public class LiveMenu {
    private final ServerFacade server;
    private final Scanner s;
    private final WebsocketFacade socket;
    private int gameID;

    public LiveMenu(ServerFacade server, Scanner s) throws Exception {
        this.server = server;
        this.s = s;
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
}
