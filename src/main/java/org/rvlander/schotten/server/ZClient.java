package org.rvlander.schotten.server;

import com.boardgames.bastien.schotten_totten.model.BoardFromPlayerView;
import com.boardgames.bastien.schotten_totten.model.Game;
import org.rvlander.schotten.encoding.ByteArrayEncoder;
import org.rvlander.schotten.encoding.DecodingException;
import org.rvlander.schotten.persistence.GameKey;
import org.rvlander.schotten.persistence.Key;
import org.rvlander.schotten.persistence.PlayerKey;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

public class ZClient extends Client {

    ZFrame address;
    ByteArrayEncoder encoder = new ByteArrayEncoder();
    ZMQ.Socket socket;
    boolean registered = false;

    public ZClient(ClientListener listener, ZFrame address, ZMQ.Socket socket) {
        super(listener);
        this.address = address;
        this.socket = socket;
    }

    public boolean register(ZMsg msg) {
        assert(msg.size() == 2);
        byte[] playerKeyData = msg.pop().getData();
        PlayerKey playerKey = null;
        if(playerKeyData.length != Key.KEY_LENGTH && playerKeyData.length != 0) {
            return false;
        }
        if(playerKeyData.length == Key.KEY_LENGTH) {
            playerKey = new PlayerKey(playerKeyData);
        }

        byte[] gameKeyData = msg.pop().getData();
        GameKey gameKey = null;
        if(gameKeyData.length != Key.KEY_LENGTH && gameKeyData.length != 0) {
            return false;
        }
        if(gameKeyData.length == Key.KEY_LENGTH) {
            gameKey = new GameKey(gameKeyData);
        }

        this.playerKey = playerKey;
        this.gameKey = gameKey;

        this.registered = this.listener.register(this);

        return true;
    }

    public boolean receivedMove(ZMsg msg){
        assert(msg.size() == 3);
        if(this.registered) {
            byte[] playerKeyData = msg.pop().getData();
            PlayerKey playerKey = null;
            if(playerKeyData.length != Key.KEY_LENGTH && playerKeyData.length != 0) {
                return false;
            }
            if(playerKeyData.length == Key.KEY_LENGTH) {
                playerKey = new PlayerKey(playerKeyData);
            }

            byte[] gameKeyData = msg.pop().getData();
            GameKey gameKey = null;
            if(gameKeyData.length != Key.KEY_LENGTH && gameKeyData.length != 0) {
                return false;
            }
            if(gameKeyData.length == Key.KEY_LENGTH) {
                gameKey = new GameKey(gameKeyData);
            }

            if(!gameKey.equals(this.getGameKey()) || !playerKey.equals(this.playerKey)) {
                return false;
            }

            byte[] moveData = msg.pop().getData();
            ClientMove clientMove = null;
            try {
                clientMove = this.encoder.decodeClientMove(moveData);
            } catch(DecodingException e) {
                return false;
            }

            this.listener.nextMove(this, clientMove);

            return true;
        }
        return false;
    }

    public void processTurn(ClientErrorCode errorCode, PlayerKey playerKey, GameKey gameKey,
                            BoardFromPlayerView board) {
        if(playerKey.equals(this.playerKey)) {
            super.processTurn(errorCode, playerKey, gameKey, board);
            ZMsg msg = new ZMsg();
            msg.add(playerKey.getData());
            msg.add(gameKey.getData());
            msg.add(encoder.encode(errorCode));
            msg.add(encoder.encode(board));

            msg.wrap(this.address.duplicate());

            System.out.println(msg);

            msg.send(this.socket);
        }
    }
}
