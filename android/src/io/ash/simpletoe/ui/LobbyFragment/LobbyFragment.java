package io.ash.simpletoe.ui.LobbyFragment;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import io.ash.simpletoe.AppContext;
import io.ash.simpletoe.R;
import io.ash.simpletoe.ui.GameActivity;
import io.ash.simpletoe.ui.LobbyFragment.LobbyList.Lobby;
import io.ash.simpletoe.ui.LobbyFragment.LobbyList.LobbyRecyclerAdapter;
import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class LobbyFragment extends Fragment {
    private SharedPreferences pref;
    private LobbyViewModel lobbyViewModel;
    private Socket mSocket;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppContext app = (AppContext) Objects.requireNonNull(getActivity()).getApplication();
        this.mSocket = app.getSocket();
        this.pref = getActivity().getSharedPreferences("Configuration", Context.MODE_PRIVATE);

        this.mSocket.on("lobbyCreated", onLobbyCreated);
        this.mSocket.on("lobbyIsNotCreated", onLobbyCreatingFailure);
        this.mSocket.on("lobbiesUpdated", onUpdate);

        this.mSocket.emit("getLobbies", onLobbiesRequested);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lobbies, container, false);

        lobbyViewModel = ViewModelProviders.of(this).get(LobbyViewModel.class);

        RecyclerView recycler = root.findViewById(R.id.lobby_list);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        LobbyRecyclerAdapter.RecyclerViewClickListener listener = (view, position) -> {
            Lobby lobby = Objects.requireNonNull(lobbyViewModel.getList().getValue()).get(position);
            if (lobby.isLocked())
                execPasswordDialog(lobby, inflater);
            else
                execGameActivity(lobby.getId(), null);
        };

        lobbyViewModel.getList().observe(this, s -> {
            LobbyRecyclerAdapter adapter = new LobbyRecyclerAdapter(s, listener);
            recycler.setAdapter(adapter);
        });

        FloatingActionButton addButton = root.findViewById(R.id.addbutton);
        addButton.setOnClickListener(v -> {
            final AlertDialog dialogBuilder = new AlertDialog.Builder(getActivity()).create();
            View dialogView = inflater.inflate(R.layout.lobby_dialog, null);

            Button submit = dialogView.findViewById(R.id.submitButton);
            Button discard = dialogView.findViewById(R.id.discardButton);

            EditText nameField = dialogView.findViewById(R.id.nameField);
            EditText passwordField = dialogView.findViewById(R.id.passwordField);

            discard.setOnClickListener(view -> dialogBuilder.dismiss());
            submit.setOnClickListener(view -> {
                JSONObject object = new JSONObject();
                try {
                    object.put("name", nameField.getText());
                    object.put("password", passwordField.getText());
                    object.put("makerID", pref.getString("uid", ""));
                    object.put("makerName", pref.getString("name", ""));
                    mSocket.emit("createLobby", object, onLobbyCreated);
                } catch (JSONException e) { e.printStackTrace(); }
                dialogBuilder.dismiss();
            });

            dialogBuilder.setView(dialogView);
            dialogBuilder.show();
        });

        return root;
    }

    private void execPasswordDialog(Lobby lobby, LayoutInflater inflater) {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getActivity()).create();
        View dialogView = inflater.inflate(R.layout.password_dialog, null);

        Button submit = dialogView.findViewById(R.id.submitButton_req);
        Button discard = dialogView.findViewById(R.id.discardButton_req);

        EditText passwordField = dialogView.findViewById(R.id.request_password);

        discard.setOnClickListener(view_int -> dialogBuilder.dismiss());
        submit.setOnClickListener(view_int -> {
            String password = passwordField.getText().toString();

            JSONObject obj = new JSONObject();
            try {
                obj.put("lobbyID", String.valueOf(lobby.getId()));
                obj.put("password", password);
                mSocket.emit("checkPassword", obj, (Ack) args -> { execGameActivity(lobby.getId(), password); });
            } catch (JSONException e) { e.printStackTrace(); }
            dialogBuilder.dismiss();
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void execGameActivity(int id, String password) {
        Intent mIntent = new Intent(getActivity(), GameActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        mIntent.putExtra("LOBBY_ID_EXTRA", String.valueOf(id));
        mIntent.putExtra("PASSWORD_EXTRA", (password != null) ? password : "");
        startActivity(mIntent);
    }

    Ack onLobbiesRequested = args -> {
        JSONArray messageJson = (JSONArray) args[0];
        if (messageJson != null) {
            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                Objects.requireNonNull(lobbyViewModel.getList().getValue()).clear();
                for (int i = 0; i < messageJson.length(); i++) {
                    try {
                        JSONObject object = messageJson.getJSONObject(i);
                        lobbyViewModel.addItem(new Lobby(
                                object.getInt("id"),
                                object.getString("name"),
                                object.getBoolean("hasPassword")
                        ));
                    } catch (JSONException e) { e.printStackTrace(); }
                }
            });
        }
    };

    Emitter.Listener onUpdate = args -> {
        this.mSocket.emit("getLobbies", onLobbiesRequested);
    };

    Emitter.Listener onLobbyCreatingFailure = args -> {
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            Toast.makeText(getContext(), "Lobby creation failure", Toast.LENGTH_LONG).show();
        });
    };

    Emitter.Listener onLobbyCreated = args -> {
        JSONObject messageJson = (JSONObject) args[0];
        try {
            execGameActivity(messageJson.getInt("id"), null);
        } catch (JSONException e) { e.printStackTrace(); }
    };
}