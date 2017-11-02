package org.rvlander.schotten.encoding;

import org.rvlander.schotten.persistence.Game;

public class DummyEncoder implements Encoder<Game>{
    public Game encode (Game game) {
        return game;
    }
    public Game decode (Game encodedGame) {
        return encodedGame;
    }
}
