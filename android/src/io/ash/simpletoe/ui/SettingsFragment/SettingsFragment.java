package io.ash.simpletoe.ui.SettingsFragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import io.ash.simpletoe.AppContext;
import io.ash.simpletoe.R;
import io.socket.client.Ack;
import io.socket.client.Socket;

public class SettingsFragment extends Fragment {
    private SharedPreferences pref;
    private SettingsViewModel slideshowViewModel;
    private Socket mSocket;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppContext app = (AppContext) getActivity().getApplication();

        this.pref = getActivity().getSharedPreferences("Configuration", Context.MODE_PRIVATE);
        this.mSocket = app.getSocket();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        TextView idView = root.findViewById(R.id.idText);
        TextView nameText = root.findViewById(R.id.nameText);
        Button saveButton = root.findViewById(R.id.saveButton);

        slideshowViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);

        slideshowViewModel.getId().observe(this, idView::setText);
        slideshowViewModel.setId(pref.getString("uid", ""));

        slideshowViewModel.getName().observe(this, nameText::setText);
        slideshowViewModel.setName(pref.getString("name", ""));

        saveButton.setOnClickListener(args -> {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("name", nameText.getText().toString());
            editor.apply();
            editor.commit();
            slideshowViewModel.setName(pref.getString("name", ""));

            nameText.clearFocus();
        });

        Button addButton = root.findViewById(R.id.idButton);
        addButton.setOnClickListener(v -> {
            mSocket.emit("getUniqueID", onUpdateID);
        });

        return root;
    }

    Ack onUpdateID = args -> {
        JSONObject messageJson = (JSONObject) args[0];
        try {
            String line = messageJson.getString("id");
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("uid", line);
            editor.apply();
            editor.commit();

            getActivity().runOnUiThread(() -> {
                slideshowViewModel.setId(pref.getString("uid", ""));
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };
}