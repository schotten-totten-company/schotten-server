package org.rvlander.schotten.server;

public interface ClientListener {
    public boolean register(Client client);
    public void nextMove(Client client, ClientMove move);
}
