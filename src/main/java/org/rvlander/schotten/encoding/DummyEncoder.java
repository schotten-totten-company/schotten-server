package org.rvlander.schotten.encoding;


import com.boardgames.bastien.schotten_totten.model.Game;

public class DummyEncoder implements Encoder<Game>{
    public Game encode (Game game) {
        return game;
    }
    public Game decodeGame(Game encodedGame) {
        return encodedGame;
    }
}
