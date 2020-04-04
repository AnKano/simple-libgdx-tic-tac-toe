package io.ash.simpletoe.ui.SettingsFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import io.ash.simpletoe.AppContext;
import io.ash.simpletoe.R;
import io.ash.simpletoe.ui.MenuActivity;
import io.socket.client.Ack;
import io.socket.client.Socket;

public class SettingsFragment extends Fragment {
    private SharedPreferences pref;
    private SettingsViewModel slideshowViewModel;
    private Socket mSocket;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppContext app = (AppContext) Objects.requireNonNull(getActivity()).getApplication();

        this.pref = getActivity().getSharedPreferences("Configuration", Context.MODE_PRIVATE);
        this.mSocket = app.getSocket();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        TextView idView = root.findViewById(R.id.idText); // Get TextView that contain uid token
        EditText nameText = root.findViewById(R.id.nameText); // Get EditText that contain written user name
        Button saveButton = root.findViewById(R.id.saveButton); // Get Button that will use for accepting changes

        // Make View Model provider
        slideshowViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        // Subscribe idView::setText on any LiveData changing
        slideshowViewModel.getId().observe(getViewLifecycleOwner(), idView::setText);
        // Set uid that containing in SharedPreferences
        slideshowViewModel.setId(pref.getString("uid", ""));

        // Subscribe nameText on any LiveData changing
        slideshowViewModel.getName().observe(getViewLifecycleOwner(), nameText::setText);
        // Set name that containing in SharedPreferences
        slideshowViewModel.setName(pref.getString("name", ""));

        // Set Click listener on save button
        saveButton.setOnClickListener(args -> {
            // Open shared preferences on edit
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("name", nameText.getText().toString());
            editor.apply();
            // Committing all changes
            editor.commit();

            // Kick off live data
            slideshowViewModel.setName(pref.getString("name", ""));

            // Clear focus from the field
            nameText.clearFocus();
        });

        Button addButton = root.findViewById(R.id.idButton);
        // Set Click listener on change uid button
        addButton.setOnClickListener(v -> {
            // emit on server package with update uid request
            mSocket.emit("getUniqueID", onUpdateID);
        });

        return root;
    }

    //Restart activity after some critical changes (e.g. uid update)
    private void restartApplication() {
        // Create intent for our main menu activity
        Intent intent = new Intent(getContext(), MenuActivity.class);
        // Set flag "Create in new task"
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Start new "task"
        Objects.requireNonNull(getContext()).startActivity(intent);
        // Close before activity...
        if (getContext() instanceof MenuActivity)
            Objects.requireNonNull(getActivity()).finish();
        // ... and dispose our old task
        Runtime.getRuntime().exit(0);
    }

    private Ack onUpdateID = args -> {
        JSONObject messageJson = (JSONObject) args[0];
        try {
            String line = messageJson.getString("id");
            // Open sp on edit
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("uid", line);
            editor.apply();
            // Committing changes
            editor.commit();

            // Set data in ViewModel in UI Thread (not necessary. you should use postValue instead)
            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                slideshowViewModel.setId(pref.getString("uid", ""));
            });

            // kick off restarting
            restartApplication();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };
}