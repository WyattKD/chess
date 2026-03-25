package ui;

import client.ServerFacade;
import java.util.Random;

import java.util.Scanner;

public class PreLoginMenu {
    ServerFacade server;
    private Scanner scanner;
    private int guestNumber;

    public PreLoginMenu(ServerFacade server) {
        this.server = server;
        scanner = new Scanner(System.in);
        Random rand = new Random();
        guestNumber = rand.nextInt(999) + 1;
    }

    public void run() {
        String input = "";

        while (!input.equals("quit")) {
            System.out.printf(EscapeSequences.SET_TEXT_COLOR_RED + "\n[Guest#" + guestNumber + "] >>> ");
            try {
                input = scanner.nextLine();
                if (input.equals("quit")) {
                    System.out.println("Quitting...");
                    break;
                }
                eval(input.split(" "));
            } catch (Exception exception) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + exception.getMessage());
            }
        }
        scanner.close();
    }

    private void eval(String[] input) {
        switch (input[0].toLowerCase()) {
            case "help":
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "login <USERNAME> <PASSWORD> - to play a game");
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "quit - to stop playing chess");
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "help - to display this help message");
                break;

            case "quit":
                System.out.println("Quitting...");
                break;

            case "register":
                if (input.length != 4) {
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "incorrect number of arguments");
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
                    break;
                }
                register(input[1], input[2], input[3]);
                break;

            case "login":
                if (input.length != 3) {
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "incorrect number of arguments");
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "login <USERNAME> <PASSWORD> - to play a game");
                    break;
                }
                login(input[1], input[2]);
                break;

            default:
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "unknown command: " + input[0]);
                break;
        }
    }

    private void register(String username, String password, String email) {
        if (server.register(username, password, email)) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "Successfully registered!");
            boolean isQuitting = new PostLoginMenu(server, username, scanner).run();
            if (isQuitting) {
                System.out.println("Quitting...");
                scanner.close();
                System.exit(0);
            } else {
                System.out.println("♕ 240 Chess Client: enter \"help\" to get started");
            }
        } else {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Username already taken");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
        }
    }

    private void login(String username, String password) {
        if (server.login(username, password)) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "Successfully logged in!");
            boolean isQuitting = new PostLoginMenu(server, username, scanner).run();
            if (isQuitting) {
                System.out.println("Quitting...");
                scanner.close();
                System.exit(0);
            } else {
                System.out.println("♕ 240 Chess Client: enter \"help\" to get started");
            }
        } else {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Username or password incorrect, please try again");
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "login <USERNAME> <PASSWORD> - to play a game");
        }
    }
}
