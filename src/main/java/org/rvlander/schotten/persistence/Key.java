package org.rvlander.schotten.persistence;

import java.util.Random;

public class Key {
    private static final int KEY_LENGTH = 16;
    private static final Random RANDOMIZER = new Random();

    private byte data[];

    public Key() {
        this.data = new byte[Key.KEY_LENGTH];
        RANDOMIZER.nextBytes(data);
    }
}
