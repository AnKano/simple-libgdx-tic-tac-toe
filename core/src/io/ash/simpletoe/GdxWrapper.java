package io.ash.simpletoe;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Timer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.ash.simpletoe.enums.GameState;
import io.ash.simpletoe.screens.EmptyScreen;
import io.ash.simpletoe.screens.PreScreen;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class GdxWrapper extends Game {
    public GdxWrapper(String lobbyId, String password, String uId, String name, Socket socket) {
        new ConfigurationStash(lobbyId, uId, name, socket, password);
    }

    @Override
    public void create() {
        this.setScreen(new EmptyScreen());

        JSONObject object = ConfigurationStash.createDataBundle();
        ConfigurationStash.socket.on("successJoin", onSuccess);
        ConfigurationStash.socket.on("lobbyDeleted", onFailureOrDeleted);
        ConfigurationStash.socket.on("failureJoin", onFailureOrDeleted);
        ConfigurationStash.socket.emit("joinLobby", object);
    }

    private Emitter.Listener onSuccess = args -> {
        final GdxWrapper instance = this;
        JSONObject obj = (JSONObject) args[0];
        try {
            ConfigurationStash.currentTurn = ConfigurationStash.setCurentTurnFromInt(obj.getInt("currentTurn"));
            ConfigurationStash.setSymbol(obj.getString("xPlayer"), obj.getString("oPlayer"));

            JSONArray fields = obj.getJSONArray("field");
            Gdx.app.postRunnable(() -> instance.setScreen(new PreScreen(instance, fields)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

    private Emitter.Listener onFailureOrDeleted = args -> {
        ConfigurationStash.gameState = GameState.CLOSED;
        new Timer().scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                Gdx.app.exit();
                ConfigurationStash.resetStash();
            }
        }, 5f);
    };

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {

    }
}
