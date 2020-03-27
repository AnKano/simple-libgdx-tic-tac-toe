package io.ash.simpletoe.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.ash.simpletoe.ConfigurationStash;
import io.ash.simpletoe.GdxWrapper;
import io.ash.simpletoe.entities.Grid;
import io.ash.simpletoe.enums.GameState;
import io.ash.simpletoe.screens.generic.DynamicScreen;
import io.socket.emitter.Emitter;


public class GameScreen extends DynamicScreen {
    public final GdxWrapper parent;
    private SpriteBatch batch;
    public Stage stage;

    private OrthographicCamera camera;

    GameScreen(GdxWrapper gdxWrapper, JSONArray fields) {
        this.parent = gdxWrapper;
        this.batch = new SpriteBatch();
        Grid grid = new Grid(this, fields);

        ConfigurationStash.socket.on("gameEnded", onRoundEnded);

        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.update();

        this.stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), this.camera));
        this.updateGrid(grid);

        Gdx.input.setInputProcessor(createInputListener(grid, this.camera));
    }

    @Override
    public void render(float delta) {
        updateClearColor(delta);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        if (ConfigurationStash.gameState == GameState.IN_PROGRESS ||
                ConfigurationStash.gameState == GameState.DRAW ||
                ConfigurationStash.gameState == GameState.WIN) {

            this.camera.update();
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), (int) (Gdx.graphics.getHeight() * 1.0f));

            batch.begin();
            drawTextOnScreenBottom();
            batch.end();

            this.stage.act();
            this.stage.draw();
        } else {
            batch.begin();
            drawCrashText();
            batch.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        this.stage.getViewport().update(width, height, true);
    }

    private InputMultiplexer createInputListener(Grid grid, OrthographicCamera camera) {
        InputMultiplexer input = new InputMultiplexer();
        input.addProcessor(new GestureDetector(new GestureDetector.GestureAdapter() {
            @Override
            public boolean zoom(float initialDistance, float distance) {
                float coeff = (initialDistance / distance);
                if (coeff >= .4f && coeff <= 2.0f)
                    camera.zoom = coeff;
                camera.update();

                return true;
            }

            @Override
            public boolean pan(float x, float y, float deltaX, float deltaY) {
                float dx = deltaX * camera.zoom;
                float dy = deltaY * camera.zoom;

                if ((camera.position.x + -dx >= 0 && camera.position.y + dy >= 0) &&
                        (camera.position.x + -dx <= grid.getRows() * 128 &&
                                camera.position.y + dy <= grid.getColums() * 128)) {
                    camera.translate(-dx, dy);
                    camera.update();
                }
                return true;
            }
        }));
        input.addProcessor(this.stage);
        return input;
    }

    private void updateGrid(Grid grid) {
        for (int i = 0; i < grid.getColums(); i++)
            for (int j = 0; j < grid.getRows(); j++)
                this.stage.addActor(grid.state[i][j]);
    }

    private void drawTextOnScreenBottom() {
        String gameScreenText;
        switch (ConfigurationStash.gameState) {
            case WIN: gameScreenText = (ConfigurationStash.winner.equals(ConfigurationStash.uId)) ? "You win" : "You Lose!"; break;
            case DRAW: gameScreenText = "Game drawed!"; break;
            case IN_PROGRESS: gameScreenText = ConfigurationStash.isYourTurn() ? "Your Turn!" : "Opponent Turn!"; break;
            default: gameScreenText = "Unexpected";
        }

        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(ConfigurationStash.bakedFont, gameScreenText);
        float w = glyphLayout.width / 2;
        ConfigurationStash.bakedFont.getData().setScale(1f, 1f);
        ConfigurationStash.bakedFont.draw(batch, gameScreenText, Gdx.graphics.getWidth() * 0.5f - w, Gdx.graphics.getHeight() * 0.1f);
    }

    private void drawCrashText() {
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(ConfigurationStash.bakedFont, "Room closed");
        float w = glyphLayout.width / 2;
        ConfigurationStash.bakedFont.getData().setScale(1f, 1f);
        ConfigurationStash.bakedFont.draw(batch, "Room closed", Gdx.graphics.getWidth() * 0.5f - w, Gdx.graphics.getHeight() * 0.1f);
    }

    private Emitter.Listener onRoundEnded = args -> {
        JSONObject obj = (JSONObject) args[0];
        try {
            ConfigurationStash.gameState = ConfigurationStash.setGameState(obj.getInt("status"));
            if (ConfigurationStash.gameState == GameState.WIN)
                ConfigurationStash.winner = obj.getString("winnerID");

            Gdx.input.vibrate(200);

            new Timer().scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    Gdx.app.exit();
                    ConfigurationStash.resetStash();
                }
            }, 5f);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    };
}
