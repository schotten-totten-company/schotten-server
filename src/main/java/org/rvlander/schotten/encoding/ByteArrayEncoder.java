package org.rvlander.schotten.encoding;

import com.boardgames.bastien.schotten_totten.model.Game;
import org.rvlander.schotten.server.ClientErrorCode;

public class ByteArrayEncoder implements Encoder<byte []>{
    @Override
    public byte[] encode(Game game) {
        return new byte[0];
    }

    @Override
    public Game decode(byte[] encodedGame) {
        return null;
    }

    public byte[] encode(ClientErrorCode code) {
        byte[] res = new byte[1];
        switch(code) {
            case OK:
                res[0] = 0x00;
                break;
            case WON:
                res[0] = 0x01;
                break;
            case LOST:
                res[0] = 0x02;
                break;
            case INVALID_PLAY:
                res[0] = 0x03;
                break;
        }
        return res;
    }
}
