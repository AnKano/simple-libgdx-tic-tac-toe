package io.ash.simpletoe.ui.LobbyFragment.LobbyList;

public class Lobby {
    private int id;
    private String name;
    private boolean locked;

    public Lobby(int id, String name, boolean locked) {
        this.id = id;
        this.name = name;
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
