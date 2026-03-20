package client;

import ui.PostLoginMenu;
import ui.PreLoginMenu;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        System.out.println("♕ 240 Chess Client: enter \"help\" to get started");
        int port = 8080;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }
        ServerFacade server = new ServerFacade("http://localhost:" + port);

        PreLoginMenu prelogin = new PreLoginMenu(server);
        prelogin.run();
        System.out.println("Exited");
    }
}
