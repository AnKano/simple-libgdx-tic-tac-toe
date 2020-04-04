package io.ash.simpletoe.ui.LobbyFragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import io.ash.simpletoe.ui.LobbyFragment.LobbyList.Lobby;

public class LobbyViewModel extends ViewModel {
    private MutableLiveData<List<Lobby>> lobbyList;

    public LobbyViewModel() {
        lobbyList = new MutableLiveData<>();
        // set empty list for avoid nullpointer exception
        lobbyList.setValue(new ArrayList<>());
    }

    void addItem(Lobby data) {
        if (data != null && lobbyList.getValue() != null) {
            lobbyList.getValue().add(0, data);
            // postvalue using instead of setvalue cos' fragment can be suspended
            // in this case changes won't be committed
            lobbyList.postValue(lobbyList.getValue());
        }
    }

    LiveData<List<Lobby>> getList() {
        return lobbyList;
    }

    void clearList() {
        if (lobbyList.getValue() != null) {
            lobbyList.getValue().clear();
            // postvalue using instead of setvalue cos' fragment can be suspended
            // in this case changes won't be committed
            lobbyList.postValue(lobbyList.getValue());
        }
    }
}