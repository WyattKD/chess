package ui;

import client.ServerFacade;
import chess.*;
import model.GameData;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PostLoginMenu {
    private final String username;
    private final Scanner scanner;
    private HashMap<Integer, Integer> gameMap = new HashMap<>();
    ServerFacade serverFacade;
    List<GameData> games;

    public PostLoginMenu(ServerFacade serverFacade, String username, Scanner scanner) {
        this.serverFacade = serverFacade;
        this.username = username;
        this.scanner = scanner;
        System.out.println("Logged in as " + username);
    }

    public boolean run() {
        String input = "";
        while (true) {
            System.out.print("\n[LOGGED IN] >>> ");
            input = scanner.nextLine();
            if (input.equals("logout")) {
                System.out.println("Logged out " + username);
                serverFacade.logout();
                return false;
            } else if (input.equals("quit")) {
                return true;
            }
            eval(input.split(" "));
        }
    }

    private void eval(String[] input) {
        switch (input[0].toLowerCase()) {
            case "help":
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "create <NAME> - a game");
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "list - games");
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "join <ID> [WHITE|BLACK] - a game");
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "observe <ID> - a game");
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "logout - when you are done");
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "quit - playing chess");
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "help - to display this help message");
                break;

            case "create":
                if (input.length != 2) {
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "incorrect number of arguments");
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "create <NAME> - a game");
                    break;
                }
                int id = serverFacade.createGame(input[1]);
                if (id != -1) {
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "Game successfully created!");
                }
                break;

            case "list":
                games = new ArrayList<>();
                HashSet<GameData> gameList = serverFacade.listGames();
                games.addAll(gameList);
                for (int i = 0; i < games.size(); i++) {
                    GameData game = games.get(i);
                    String whiteUser = game.whiteUsername() != null ? game.whiteUsername() : "open";
                    String blackUser = game.blackUsername() != null ? game.blackUsername() : "open";
                    System.out.printf(EscapeSequences.SET_TEXT_COLOR_GREEN + "%d -- Game: %s  |  White: %s  |  Black: %s %n", i, game.gameName(), whiteUser, blackUser);
                }
                break;

            case "join":
                if (input.length != 3 || (!input[2].equalsIgnoreCase("WHITE") && !input[2].equalsIgnoreCase("BLACK"))) {
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "incorrect number of arguments");
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "join <ID> [WHITE|BLACK] - a game");
                } else {
                    int gameNum = Integer.parseInt(input[1]);
                    if (games.isEmpty() || games.size() <= gameNum) {
                        games = new ArrayList<>();
                        HashSet<GameData> gamesList = serverFacade.listGames();
                        games.addAll(gamesList);
                        if (games.isEmpty()) {
                            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "No games available");
                            break;
                        }
                        if (games.size() <= gameNum) {
                            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Game ID does not exist");
                            for (int i = 0; i < games.size(); i++) {
                                GameData game = games.get(i);
                                String whiteUser = game.whiteUsername() != null ? game.whiteUsername() : "open";
                                String blackUser = game.blackUsername() != null ? game.blackUsername() : "open";
                                System.out.printf(EscapeSequences.SET_TEXT_COLOR_GREEN + "%d -- Game: %s  |  White: %s  |  Black: %s %n", i, game.gameName(), whiteUser, blackUser);
                            }
                            break;
                        }
                    }
                    GameData game = games.get(gameNum);
                    if (serverFacade.joinGame(game.gameID(), input[2].toUpperCase())) {
                        System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "Successfully joined game!");
                        printChessBoard(game.game(), input[2].equalsIgnoreCase("WHITE"));

                    } else {
                        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "That color is already taken");
                        System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "join <ID> [WHITE|BLACK] - a game");
                    }
                }
                break;

            case "observe":
                if (input.length != 2) {
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "incorrect number of arguments");
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "observe <ID> - a game");
                    break;
                }
                // serverFacade.observe()
                break;

            default:
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "unknown command: " + input[0]);
                break;
        }
    }

    static void printChessBoard(ChessGame game, boolean whiteView) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(EscapeSequences.ERASE_SCREEN);
        out.print(EscapeSequences.SET_TEXT_BOLD);
        ArrayList<String> columns = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"));
        ArrayList<String> rows = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8"));
        if (!whiteView) {
            columns = new ArrayList<>(columns.reversed());
        }

        out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
        out.print(EscapeSequences.SET_TEXT_COLOR_BLUE);
        out.print(EscapeSequences.RESET_TEXT_COLOR);
        out.print("  ");
        for (var col : columns) {
            out.print(" " + EscapeSequences.SET_TEXT_COLOR_BLUE + col + " ");
        }
        out.println(EscapeSequences.EMPTY);
        if (whiteView) {
            rows = new ArrayList<>(rows.reversed());
        }
        for (int r = 0; r < 8; ++r) {
            out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + rows.get(r) + " ");
            for (int c = 0; c < 8; ++c) {
                int properRow = whiteView ? 8 - r : r + 1;
                int properCol = whiteView ? c + 1 : 8 - c;
                if ((r + c) % 2 != 0) {
                    out.print(EscapeSequences.SET_BG_COLOR_GREEN);
                } else {
                    out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
                }
                ChessPiece piece = game.getBoard().getPiece(new ChessPosition(properRow, properCol));
                if (piece != null) {
                    out.print(getRenderedPiece(piece));
                } else {
                    out.print("   ");
                }
            }
            out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_BG_COLOR_DARK_GREY + " " + rows.get(r) + " ");
        }
        if (!whiteView) {
            columns = new ArrayList<>(columns.reversed());
        }
        out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
        out.print(EscapeSequences.SET_TEXT_COLOR_BLUE);
        out.print(EscapeSequences.RESET_TEXT_COLOR);
        out.print("  ");
        for (var col : columns) {
            out.print(" " + EscapeSequences.SET_TEXT_COLOR_BLUE + col +" ");
        }
        out.print(EscapeSequences.RESET_BG_COLOR);
        out.print(EscapeSequences.RESET_TEXT_COLOR);
        out.print(EscapeSequences.RESET_TEXT_BOLD_FAINT);
    }

    private static String getRenderedPiece(ChessPiece piece) {
        switch (piece.getPieceType()) {
            case KING:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.SET_TEXT_COLOR_WHITE + " K " : EscapeSequences.SET_TEXT_COLOR_BLACK + " k ";
            case QUEEN:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.SET_TEXT_COLOR_WHITE + " Q " : EscapeSequences.SET_TEXT_COLOR_BLACK + " q ";
            case ROOK:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.SET_TEXT_COLOR_WHITE + " R " : EscapeSequences.SET_TEXT_COLOR_BLACK + " r ";
            case BISHOP:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.SET_TEXT_COLOR_WHITE + " B " : EscapeSequences.SET_TEXT_COLOR_BLACK + " b ";
            case KNIGHT:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.SET_TEXT_COLOR_WHITE + " N " : EscapeSequences.SET_TEXT_COLOR_BLACK + " n ";
            case PAWN:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.SET_TEXT_COLOR_WHITE + " P " : EscapeSequences.SET_TEXT_COLOR_BLACK + " p ";
            default:
                return EscapeSequences.EMPTY;
        }
    }


}
