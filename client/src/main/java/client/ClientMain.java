package client;

import ui.PostLoginMenu;
import ui.PreLoginMenu;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client:");

        ServerFacade server = new ServerFacade();

        PreLoginMenu prelogin = new PreLoginMenu(server);
        prelogin.run();
        System.out.println("Exited");
    }
}
