package ui;

import client.ServerFacade;
import chess.*;
import model.GameData;

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
            System.out.printf("\n[LOGGED IN] >>> ");
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
                    System.out.printf("%d -- Game: %s  |  White User: %s  |  Black User: %s %n", i, game.gameName(), whiteUser, blackUser);
                }
                break;

            case "join":
                if (input.length != 3 || (!input[2].equals("WHITE") && !input[2].equals("BLACK"))) {
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "incorrect number of arguments");
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "join <ID> [WHITE|BLACK] - a game");
                    break;
                } else if (gameMap.isEmpty()) {
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "there are currently no games");
                    break;
                }
                // serverFacade.join()
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

}
