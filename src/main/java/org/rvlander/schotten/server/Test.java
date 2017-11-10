package org.rvlander.schotten.server;

import org.rvlander.schotten.persistence.Key;

import java.util.HashMap;

public class Test {
    public static void main(String... args) {
        HashMap<Key, Integer> map = new HashMap<>();

        Key key = new Key();
        map.put(key, 5);
        System.out.println("Get" + map.get(new Key(key.getData())));
    }
}
