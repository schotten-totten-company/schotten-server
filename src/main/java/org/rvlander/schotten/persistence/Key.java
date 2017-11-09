package org.rvlander.schotten.persistence;

import java.util.Random;

public class Key {
    public static final int KEY_LENGTH = 16;
    private static final Random RANDOMIZER = new Random();

    private byte data[];

    public Key() {
        this.data = new byte[Key.KEY_LENGTH];
        RANDOMIZER.nextBytes(data);
    }

    public Key(byte[] key) {
        assert(key.length == KEY_LENGTH);
        this.data = key;
    }

    public byte[] getData() {
        return data;
    }
}
