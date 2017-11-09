package org.rvlander.schotten.server;

import com.boardgames.bastien.schotten_totten.model.BoardFromPlayerView;
import org.rvlander.schotten.persistence.GameKey;
import org.rvlander.schotten.persistence.PlayerKey;

public abstract class Client {

    protected ClientListener listener;
    protected PlayerKey playerKey;
    protected GameKey gameKey;

    public Client(ClientListener listener) {
        this.listener = listener;
    }

    public void processTurn(ClientErrorCode errorCode, PlayerKey playerKey, GameKey gameKey,
                            BoardFromPlayerView board) {
        assert(playerKey != null);
        assert(gameKey != null);

        System.out.println("Time to play");
    }


    public PlayerKey getPlayerKey() {
        return playerKey;
    }

    public GameKey getGameKey() {
        return gameKey;
    }

    public void setPlayerKey(PlayerKey playerKey) {
        this.playerKey = playerKey;
    }

    public void setGameKey(GameKey gameKey) {
        this.gameKey = gameKey;
    }
}
