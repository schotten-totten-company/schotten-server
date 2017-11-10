package org.rvlander.schotten.persistence;

import java.util.Random;
import java.util.Arrays;

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

    public boolean equals(Key key) {
        return Arrays.equals(this.data, key.data);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Key)) {
            return false;
        } else {
            return this.equals((Key)obj);
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.data);
    }
}
