package org.rvlander.schotten.server;

import com.boardgames.bastien.schotten_totten.model.BoardFromPlayerView;
import org.rvlander.schotten.persistence.GameKey;
import org.rvlander.schotten.persistence.PlayerKey;

public class Client {

    private ClientListener listener;
    private PlayerKey playerKey;
    private GameKey gameKey;

    public Client(ClientListener listener) {
        this.listener = listener;
    }

    public void processTurn(ClientErrorCode errorCode, PlayerKey playerKey, GameKey gameKey,
                            BoardFromPlayerView board) {
        assert(playerKey != null);
        assert(gameKey != null);

        // send game usgin zero mq
    }

    public void receivedFrame(byte[] frame) {
        // process frame and call register or nextTurn
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
