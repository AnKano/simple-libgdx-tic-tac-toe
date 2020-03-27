package io.ash.simpletoe.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import org.json.JSONArray;
import org.json.JSONObject;

import io.ash.simpletoe.ConfigurationStash;
import io.ash.simpletoe.GdxWrapper;
import io.ash.simpletoe.screens.generic.DynamicScreen;
import io.socket.emitter.Emitter;


public class PreScreen extends DynamicScreen {
    private GdxWrapper parent;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private JSONArray fieldsStatus;

    public PreScreen(GdxWrapper gdxWrapper, JSONArray fields) {
        this.parent = gdxWrapper;
        this.batch = new SpriteBatch();
        this.fieldsStatus = fields;

        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.update();

        changeClearColor(0x3C2243FF, 1);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                JSONObject object = ConfigurationStash.createDataBundle();

                // Separately emit the "ready" state. It will stacked in the socket buffer
                if (!ConfigurationStash.readyPreScreenState) ConfigurationStash.socket.emit("ready", object);
                else ConfigurationStash.socket.emit("notReady", object);

                ConfigurationStash.readyPreScreenState = !ConfigurationStash.readyPreScreenState;

                // Separately work with state in GUI
                if (!ConfigurationStash.readyPreScreenState) changeClearColor(0x3C2243FF, 2);
                else changeClearColor(0x9657A8FF, 2);

                Gdx.input.vibrate(20);

                return true;
            }
        });

        ConfigurationStash.socket.on("gameStarted", onGameStarted);
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

    private Emitter.Listener onGameStarted = args -> {
        final GdxWrapper instance = this.parent;
        Gdx.app.postRunnable(() -> instance.setScreen(new GameScreen(instance, fieldsStatus)));
    };
}
