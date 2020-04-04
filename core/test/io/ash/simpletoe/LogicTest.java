package io.ash.simpletoe;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import org.json.JSONArray;
import org.junit.Test;

import java.net.URISyntaxException;

import io.ash.simpletoe.screens.EmptyScreen;
import io.ash.simpletoe.screens.GameScreen;
import io.ash.simpletoe.screens.generic.DynamicScreen;
import io.socket.client.IO;
import io.socket.client.Socket;

import static org.junit.Assert.assertEquals;

public class LogicTest {

    @Test
    public void checkFadeTransition() {
        DynamicScreen screen = new DynamicScreen();
        Game game = new Game() {
            @Override
            public void create() {
                this.setScreen(new EmptyScreen());
            }
        };

        screen.changeClearColor(0x3C2243FF, 1f);

        Runnable thread = () -> {
            try {
                Thread.sleep(1200);
                assertEquals("3C2243FF", screen.getClearColor().toString());

                screen.dispose();
                game.dispose();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }

    @Test(expected = Test.None.class)
    public void boardInit() throws URISyntaxException {
        LwjglApplicationConfiguration conf = new LwjglApplicationConfiguration();
        Socket socket = IO.socket("");
        GdxWrapper game = new GdxWrapper("", "", "", "", socket);
        new LwjglApplication(game, conf);
        JSONArray array = new JSONArray();
        for (int i = 0; i < 4; i++) {
            JSONArray internal = new JSONArray();
            for (int j = 0; j < 4; j++)
                internal.put("2");
            array.put(internal);
        }
        Gdx.app.postRunnable(() -> {
            game.setScreen(new GameScreen(game, array));
        });
        game.dispose();
        // It checks only initializing gl context. It should end correctly
    }
}
