package io.ash.simpletoe.ui.LobbyFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import io.ash.simpletoe.R;
import io.ash.simpletoe.ui.GameActivity;
import io.ash.simpletoe.ui.LobbyFragment.LobbyList.Lobby;
import io.ash.simpletoe.ui.LobbyFragment.LobbyList.LobbyRecyclerAdapter;
import io.ash.simpletoe.ui.MenuActivity;
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
        this.mSocket = ((MenuActivity) Objects.requireNonNull(getActivity())).getSocket();
        // Subscribe on lobby list changes
        this.mSocket.on("lobbiesUpdated", onUpdate);
        this.pref = getActivity().getSharedPreferences("Configuration", Context.MODE_PRIVATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        // On fragment resume trying to get new lobbies list
        this.mSocket.emit("getLobbies", onLobbiesRequested);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lobbies, container, false);

        // Make View Model provider
        lobbyViewModel = new ViewModelProvider(this).get(LobbyViewModel.class);

        RecyclerView recycler = root.findViewById(R.id.lobby_list);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set on lobby list click listener
        LobbyRecyclerAdapter.RecyclerViewClickListener listener = (view, position) -> {
            Lobby lobby = Objects.requireNonNull(lobbyViewModel.getList().getValue()).get(position);
            // If there's password create dialog
            if (lobby.isLocked()) execPasswordDialog(lobby);
            // If it is not open game activity
            else execGameActivity(lobby.getId(), "");
        };

        // on any changes in view model update list adapter
        lobbyViewModel.getList().observe(getViewLifecycleOwner(), s -> {
            LobbyRecyclerAdapter adapter = new LobbyRecyclerAdapter(getContext(), s, listener);
            recycler.setAdapter(adapter);
        });

        FloatingActionButton addButton = root.findViewById(R.id.addbutton); // Get button on the fragment corner
        addButton.setOnClickListener(v -> {
            // onClick create dialog with lobby settings
            final AlertDialog dialogBuilder = new AlertDialog.Builder(getActivity()).create();
            View dialogView = View.inflate(getContext(), R.layout.lobby_dialog, null);

            Button submit = dialogView.findViewById(R.id.submitButton);
            Button discard = dialogView.findViewById(R.id.discardButton);

            EditText nameField = dialogView.findViewById(R.id.nameField);
            EditText passwordField = dialogView.findViewById(R.id.passwordField);

            // dispose our dialog on Close button
            discard.setOnClickListener(view -> dialogBuilder.dismiss());
            submit.setOnClickListener(view -> {
                JSONObject object = new JSONObject();
                try {
                    // parse all inputed data and data stored in the preferences
                    object.put("name", nameField.getText().toString());
                    object.put("password", passwordField.getText().toString());
                    object.put("makerID", pref.getString("uid", ""));
                    object.put("makerName", pref.getString("name", ""));

                    // Subscribe on lobby created
                    mSocket.on("lobbyCreated", args -> {
                        JSONObject messageJson = (JSONObject) args[0];
                        try {
                            execGameActivity(messageJson.getInt("id"), passwordField.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                    // Subscribe on lobby creating failure
                    mSocket.on("lobbyIsNotCreated", onLobbyCreatingFailure);
                    // create package with creation request + data
                    mSocket.emit("createLobby", object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // dispose our dialog after all actions
                dialogBuilder.dismiss();
            });

            dialogBuilder.setView(dialogView);
            dialogBuilder.show();
        });

        return root;
    }

    private void execPasswordDialog(Lobby lobby) {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getActivity()).create();
        View dialogView = View.inflate(getContext(), R.layout.password_dialog, null);

        Button submit = dialogView.findViewById(R.id.submitButton_req);
        Button discard = dialogView.findViewById(R.id.discardButton_req);

        EditText passwordField = dialogView.findViewById(R.id.request_password);

        // dispose our dialog on Close button
        discard.setOnClickListener(view -> dialogBuilder.dismiss());
        submit.setOnClickListener(view -> {
            execGameActivity(lobby.getId(), passwordField.getText().toString());
            // dispose our dialog after all actions
            dialogBuilder.dismiss();
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void execGameActivity(int id, String password) {
        // Create new intent to out game activity class
        Intent mIntent = new Intent(getActivity(), GameActivity.class);
        // Clear up activities stack except first activity and "set no animation"
        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        // pass lobby id and password data to intent
        mIntent.putExtra("LOBBY_ID_EXTRA", String.valueOf(id));
        mIntent.putExtra("PASSWORD_EXTRA", (password != null) ? password : "");
        startActivity(mIntent);
    }

    private void proceedLobbies(Object[] args) {
        JSONArray messageJson = (JSONArray) args[0];
        if (messageJson != null) {
            lobbyViewModel.clearList();
            for (int i = 0; i < messageJson.length(); i++) {
                try {
                    // parse lobbies list
                    JSONObject object = messageJson.getJSONObject(i);
                    lobbyViewModel.addItem(new Lobby(
                            object.getInt("id"),
                            object.getString("name"),
                            object.getBoolean("hasPassword")
                    ));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Ack onLobbiesRequested = this::proceedLobbies;
    private Emitter.Listener onUpdate = this::proceedLobbies;
    private Emitter.Listener onLobbyCreatingFailure = args -> {
        // Create toast with lobby failure
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            Toast.makeText(getContext(), "Lobby creation failure", Toast.LENGTH_LONG).show();
        });
    };
}