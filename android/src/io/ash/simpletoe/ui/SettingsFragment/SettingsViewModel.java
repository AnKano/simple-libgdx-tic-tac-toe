package io.ash.simpletoe.ui.SettingsFragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// ViewModel necessary for operative updating all settings that's using in our fragment
public class SettingsViewModel extends ViewModel {
    // editable LiveData fields
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