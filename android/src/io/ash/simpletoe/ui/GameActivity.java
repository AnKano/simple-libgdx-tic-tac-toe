package io.ash.simpletoe.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import io.ash.simpletoe.AppContext;
import io.ash.simpletoe.ConfigurationStash;
import io.ash.simpletoe.GdxWrapper;
import io.ash.simpletoe.enums.GameState;

public class GameActivity extends AndroidApplication {
    private SharedPreferences pref;
    private AppContext context;
    private String currentLobbyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Eject extra with PASSWORD from intent bundle
        String currentPassword = getIntent().getStringExtra("PASSWORD_EXTRA");
        // Eject extra with LOBBY ID from intent bundle
        this.currentLobbyId = getIntent().getStringExtra("LOBBY_ID_EXTRA");

        context = (AppContext) getApplication();
        pref = getSharedPreferences("Configuration", Context.MODE_PRIVATE);

        // Create LibGDX context configuration and turn off the screen rotating
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;

        // Create context and pass useful info into
        GdxWrapper gameContext = new GdxWrapper(
                this.currentLobbyId,
                currentPassword,
                pref.getString("uid", ""),
                pref.getString("name", ""),
                ((AppContext) getApplication()).getSocket()
        );

        RelativeLayout layout = new RelativeLayout(this);
        layout.addView(initializeForView(gameContext, config)); // init-ing game view over
        setContentView(layout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // If back button pressed we are set CLOSED state programmatically way for correct game screen changing
        ConfigurationStash.gameState = GameState.CLOSED;
        JSONObject object = new JSONObject();
        try {
            // Collect "leaveLobby" bundle that contain lobbyId and client uid
            object.put("lobbyID", this.currentLobbyId);
            object.put("clientID", pref.getString("uid", ""));
            context.getSocket().emit("leaveLobby", object);

            // Kick off all dispose methods
            Gdx.app.exit();
            // Clear off our game session configuration static class
            ConfigurationStash.resetStash();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}