package io.ash.simpletoe.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.ash.simpletoe.ConfigurationStash;
import io.ash.simpletoe.GdxWrapper;
import io.ash.simpletoe.screens.generic.DynamicScreen;
import io.socket.emitter.Emitter;


public class PreScreen extends DynamicScreen {
    private GdxWrapper parent;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    public PreScreen(GdxWrapper gdxWrapper) {
        this.parent = gdxWrapper;
        this.batch = new SpriteBatch();

        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.update();

        changeClearColor(0x3C2243FF, 1);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                JSONObject object = ConfigurationStash.createDataBundle();

                // Separately emit the "ready" state. It will stacked in the socket buffer
                if (!ConfigurationStash.readyPreScreenState)
                    ConfigurationStash.network.emit("ready", object);
                else
                    ConfigurationStash.network.emit("notReady", object);

                // We can change it programmatically because it's not make a damage for game logic
                // it will be stacked in adapter and sended to the server. game started when srever
                // send another event
                ConfigurationStash.readyPreScreenState = !ConfigurationStash.readyPreScreenState;

                // Separately work with state in GUI
                if (!ConfigurationStash.readyPreScreenState) changeClearColor(0x3C2243FF, 2);
                else changeClearColor(0x9657A8FF, 2);

                // Yay!
                Gdx.input.vibrate(20);

                return true;
            }
        });

        ConfigurationStash.network.on("gameStarted", onGameStarted);
    }

    @Override
    public void render(float delta) {
        updateClearColor(delta);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        batch.begin();
        GlyphLayout glyphLayout = new GlyphLayout();
        String item = (ConfigurationStash.readyPreScreenState) ? "Waiting for opponent!" : "Click to being ready!";
        glyphLayout.setText(ConfigurationStash.bakedFont, item);
        float w = glyphLayout.width / 2;
        ConfigurationStash.bakedFont.draw(batch, item, Gdx.graphics.getWidth() * 0.5f - w, Gdx.graphics.getHeight() * 0.1f);
        batch.end();

        this.camera.update();
    }

    @Override
    public void dispose() {
        this.batch.dispose();
        super.dispose();
    }

    private Emitter.Listener onGameStarted = args -> {
        JSONObject obj = (JSONObject) args[0];

        try {
            JSONArray fields = obj.getJSONArray("board");
            Gdx.app.postRunnable(() -> {
                this.parent.setScreen(new GameScreen(this.parent, fields));

                // deleting local socket listener
                ConfigurationStash.network.off("gameStarted");

                this.dispose();
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };
}
