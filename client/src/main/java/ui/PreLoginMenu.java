package ui;

import client.ServerFacade;

public class PreLoginMenu {
    ServerFacade server;
    PostLoginMenu PostLoginMenu;

    public PreLoginMenu(ServerFacade server) {
        this.server = server;
        PostLoginMenu = new PostLoginMenu(server);
    }

    public void run() {

    }
}
