package ui;

import client.ServerFacade;

import java.util.Scanner;

public class PreLoginMenu {
    ServerFacade server;
    PostLoginMenu PostLoginMenu;
    private Scanner scanner;

    public PreLoginMenu(ServerFacade server) {
        this.server = server;
        PostLoginMenu = new PostLoginMenu(server);
        scanner = new Scanner(System.in);
    }

    public void run() {
        String input = "";
        while (!input.equals("quit")) {
            System.out.printf("\n[LOGGED OUT] >>> ");
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
                System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN + "register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
                System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN + "login <USERNAME> <PASSWORD> - to play a game");
                System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN + "quit - to stop playing chess");
                System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN + "help - to display this help message");
                break;

            case "quit":
                System.out.println("Quitting...");
                break;

            case "register":
                if (input.length != 4) {
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "incorrect number of arguments");
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN + "register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
                    break;
                }
                register(input);
                break;

            case "login":
                if (input.length != 3) {
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "incorrect number of arguments");
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN + "login <USERNAME> <PASSWORD> - to play a game");
                    break;
                }
                login(input);
                break;

            default:
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "unknown command: " + input[0]);
                break;
        }
    }

    private void login(String[] args){}

    private void register(String[] args){}
}
