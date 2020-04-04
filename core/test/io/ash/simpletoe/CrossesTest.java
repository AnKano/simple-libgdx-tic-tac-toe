package io.ash.simpletoe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import org.json.JSONArray;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.net.URISyntaxException;

import io.ash.simpletoe.entities.Cell;
import io.ash.simpletoe.entities.CrossCell;
import io.ash.simpletoe.entities.Grid;
import io.ash.simpletoe.entities.NougthCell;
import io.ash.simpletoe.enums.Figures;
import io.ash.simpletoe.screens.GameScreen;
import io.socket.client.IO;
import io.socket.client.Socket;

import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CrossesTest {
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
                    Figures result = ConfigurationStash.setCurentTurnFromInt(0);

                    Stage current = screen[0].stage;
                    Grid data = screen[0].grid;
                    Actor actor = data.state[i][j];
                    actor.remove();

                    Cell cell = (result == Figures.NOUGHT)
                            ? new NougthCell(i, j)
                            : new CrossCell(i, j);
                    data.state[i][j] = cell;
                    current.addActor(cell);
                }
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    Grid data = screen[0].grid;
                    Actor actor = data.state[i][j];
                    assertTrue(actor instanceof CrossCell);
                }
        });
        Thread.sleep(5000);
    }
}
