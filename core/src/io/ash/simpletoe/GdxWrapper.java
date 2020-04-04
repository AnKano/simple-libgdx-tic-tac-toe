package io.ash.simpletoe;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Timer;

import org.json.JSONException;
import org.json.JSONObject;

import io.ash.simpletoe.enums.GameState;
import io.ash.simpletoe.screens.EmptyScreen;
import io.ash.simpletoe.screens.PreScreen;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class GdxWrapper extends Game {
    public GdxWrapper(String lobbyId, String password, String uId, String name, Socket socket) {
        // Pre-create configuration bundle with useful info
        new ConfigurationStash(lobbyId, uId, name, socket, password);
    }

    @Override
    public void create() {
        // Set empty screen before "ready" screen
        this.setScreen(new EmptyScreen());
        // collect status bundle
        JSONObject object = ConfigurationStash.createDataBundle();

        // subscribe on lobby states
        ConfigurationStash.network.on("successJoin", onSuccess);
        ConfigurationStash.network.on("lobbyDeleted", onFailureOrDeleted);
        ConfigurationStash.network.on("failureJoin", onFailureOrDeleted);
        ConfigurationStash.network.on("lobbyResumed", onLobbyResumed);
        ConfigurationStash.network.on("disconnect", onDisconnect);

        // emitting joint to lobby event with prepared bundle
        ConfigurationStash.network.emit("joinLobby", object);
    }

    private Emitter.Listener onSuccess = args -> {
        final GdxWrapper instance = this;
        Gdx.app.postRunnable(() -> {
            try {
                JSONObject obj = (JSONObject) args[0];
                // When client connected to the lobby in the static class loading player symbol and current turn symbol
                ConfigurationStash.currentTurn = ConfigurationStash.setCurentTurnFromInt(obj.getInt("currentTurn"));
                ConfigurationStash.setFigure(obj.getString("xPlayer"), obj.getString("oPlayer"));
                instance.setScreen(new PreScreen(instance));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    };

    private Emitter.Listener onFailureOrDeleted = args -> {
        // on failure programmatically set CLOSED state and exit the activity
        ConfigurationStash.gameState = GameState.CLOSED;
        new Timer().scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                Gdx.app.exit();
            }
        }, 0f);
    };

    private Emitter.Listener onLobbyResumed = args -> {
        // on resume programmatically set IN_PROGRESS state
        ConfigurationStash.gameState = GameState.IN_PROGRESS;
    };

    private Emitter.Listener onDisconnect = args -> {
        JSONObject object = new JSONObject();
        try {
            // on soft disconnect just send the "leave lobby" event
            object.put("lobbyID", ConfigurationStash.lobbyId);
            object.put("clientID", ConfigurationStash.uId);
            ConfigurationStash.network.emit("leaveLobby", object);

            ConfigurationStash.resetStash();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        // on dispose clear up stash in Gdx thread
        Gdx.app.postRunnable(ConfigurationStash::resetStash);
    }
}
