package io.ash.simpletoe.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import io.ash.simpletoe.AppContext;
import io.ash.simpletoe.GdxWrapper;

public class GameActivity extends AndroidApplication {
    private SharedPreferences pref;
    private AppContext context;
    private String currentLobbyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String currentPassword = getIntent().getStringExtra("PASSWORD_EXTRA");
        this.currentLobbyId = getIntent().getStringExtra("LOBBY_ID_EXTRA");

        context = (AppContext)getApplication();
        pref = getSharedPreferences("Configuration", Context.MODE_PRIVATE);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;

        GdxWrapper gameContext = new GdxWrapper(
                this.currentLobbyId,
                currentPassword,
                pref.getString("uid", ""),
                pref.getString("name", ""),
                ((AppContext) getApplication()).getSocket()
        );

        RelativeLayout layout = new RelativeLayout(this);
        layout.addView(initializeForView(gameContext, config));
        setContentView(layout);
    }


    @Override
    protected void onDestroy() {
        Intent mIntent = new Intent(getContext(), MenuActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(mIntent);

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        JSONObject object = new JSONObject();
        try {
            object.put("lobbyID", this.currentLobbyId);
            object.put("clientID", pref.getString("uid", ""));
            context.getSocket().emit("leaveLobby", object);

            Intent mIntent = new Intent(getContext(), MenuActivity.class);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(mIntent);
        } catch (JSONException e) { e.printStackTrace(); }
    }
}