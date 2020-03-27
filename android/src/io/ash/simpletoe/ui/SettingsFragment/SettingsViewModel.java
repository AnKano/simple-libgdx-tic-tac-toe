package io.ash.simpletoe.ui.SettingsFragment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {
    private MutableLiveData<String> mId;
    private MutableLiveData<String> mName;

    public SettingsViewModel() {
        mId = new MutableLiveData<>();
        mName = new MutableLiveData<>();
    }

    void setId(String id) {
        mId.setValue(id);
    }

    LiveData<String> getId() {
        return mId;
    }

    void setName(String name) {
        mName.setValue(name);
    }

    LiveData<String> getName() {
        return mName;
    }
}