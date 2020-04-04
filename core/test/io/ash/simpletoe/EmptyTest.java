package io.ash.simpletoe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.Actor;

import org.json.JSONArray;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.net.URISyntaxException;

import io.ash.simpletoe.entities.EmptyCell;
import io.ash.simpletoe.entities.Grid;
import io.ash.simpletoe.screens.GameScreen;
import io.socket.client.IO;
import io.socket.client.Socket;

import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EmptyTest {
    @Test
    public void checkSet() throws URISyntaxException, InterruptedException {
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

        final GameScreen[] screen = new GameScreen[1];
        Gdx.app.postRunnable(() -> {
            screen[0] = new GameScreen(game, array);
            game.setScreen(screen[0]);

            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    Grid data = screen[0].grid;
                    Actor actor = data.state[i][j];
                    assertTrue(actor instanceof EmptyCell);
                }
        });
        Thread.sleep(5000);
    }
}
