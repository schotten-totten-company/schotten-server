package org.rvlander.schotten.encoding;


import com.boardgames.bastien.schotten_totten.model.Game;

public interface Encoder<T> {
    public T encode (Game game);
    public Game decodeGame(T encodedGame);
}
