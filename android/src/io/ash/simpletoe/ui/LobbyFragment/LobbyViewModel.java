package io.ash.simpletoe.ui.LobbyFragment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import io.ash.simpletoe.ui.LobbyFragment.LobbyList.Lobby;

public class LobbyViewModel extends ViewModel {
    private MutableLiveData<List<Lobby>> lobbyList;

    public LobbyViewModel() {
        lobbyList = new MutableLiveData<>();
        lobbyList.setValue(new ArrayList<>());
    }

    void addItem(Lobby data) {
        List<Lobby> lobbies = lobbyList.getValue();
        lobbies.add(0, data);
        lobbyList.setValue(lobbies);
    }

    LiveData<List<Lobby>> getList() {
        return lobbyList;
    }
}