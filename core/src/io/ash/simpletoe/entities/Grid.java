package io.ash.simpletoe.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.ash.simpletoe.ConfigurationStash;
import io.ash.simpletoe.enums.Figures;
import io.ash.simpletoe.screens.GameScreen;

public class Grid {
    private int rows, colums;
    public Cell[][] state;

    public Grid(GameScreen parent, JSONArray fields) {
        try {
            this.rows = fields.length();
            this.colums = fields.getJSONArray(0).length();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int[][] temp = new int[this.rows][this.colums];

        for (int row = 0; row < this.rows; row++) {
            try {
                JSONArray rec = fields.getJSONArray(row);
                for (int col = 0; col < this.colums; col++) {
                    temp[row][col] = rec.getInt(col);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        this.state = new Cell[rows][colums];

        for (int row = 0; row < this.rows; row++)
            for (int col = 0; col < this.colums; col++) {
                switch (temp[row][col]) {
                    case 2:
                        this.state[row][col] = new EmptyCell(row, col); break;
                    case 0:
                        this.state[row][col] = new CrossCell(row, col); break;
                    case 1:
                        this.state[row][col] = new NougthCell(row, col); break;
                    default: return;
                }
            }

        ConfigurationStash.socket.on("moveIsCorrect", args -> {
            JSONObject messageJson = (JSONObject) args[0];

            int row_index = 0, column_index = 0;
            io.ash.simpletoe.enums.Figures result = io.ash.simpletoe.enums.Figures.NONE;
            try {
                row_index = messageJson.getJSONObject("point").getInt("Y");
                column_index = messageJson.getJSONObject("point").getInt("X");
                result = ConfigurationStash.setCurentTurnFromInt(messageJson.getInt("figure"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ConfigurationStash.currentTurn = result;
            
            Stage current = parent.stage;
            this.state[row_index][column_index].remove();
            this.state[row_index][column_index] = null;

            int finalRow_index = row_index;
            int finalColumn_index = column_index;

            io.ash.simpletoe.enums.Figures finalResult = result;
            Gdx.app.postRunnable(() -> {
                Cell cell = (finalResult == Figures.NOUGHT)
                        ? new NougthCell(finalRow_index, finalColumn_index)
                        : new CrossCell(finalRow_index, finalColumn_index);
                cell.setClicked();
                this.state[finalRow_index][finalColumn_index] = cell;
                current.addActor(cell);
            });

            Gdx.input.vibrate(20);

            parent.changeClearColor(ConfigurationStash.isYourTurn() ? 0x9657A8FF : 0x3C2243FF, 2);
        });
    }

    public int getRows() {
        return this.rows;
    }

    public int getColums() {
        return this.colums;
    }
}
