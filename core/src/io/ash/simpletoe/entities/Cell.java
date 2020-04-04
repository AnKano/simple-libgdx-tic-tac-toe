package io.ash.simpletoe.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;

import org.json.JSONException;
import org.json.JSONObject;

import io.ash.simpletoe.ConfigurationStash;
import io.ash.simpletoe.enums.GameState;

public abstract class Cell extends Actor {
    private int row_index, column_index;
    private float actorX = 0.0f, actorY = 0.0f;

    Cell(int row, int column) {
        this.row_index = row;
        this.column_index = column;

        setTouchable(Touchable.enabled);

        // each one cell use it for creating emit with "makeMove state"
        addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if (ConfigurationStash.gameState == GameState.IN_PROGRESS) {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("lobbyID", ConfigurationStash.lobbyId);
                        object.put("clientID", ConfigurationStash.uId);
                        object.put(
                                "point", new JSONObject()
                                        .put("X", column_index)
                                        .put("Y", row_index)
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ConfigurationStash.network.emit("makeMove", object);
                }
            }
        });
    }

    void updateBounds(Sprite sprite) {
        this.setActorX(sprite.getWidth() * this.getRow_index());
        this.setActorY(sprite.getHeight() * this.getColumn_index());

        setBounds(
                this.getActorX(),
                this.getActorY(),
                sprite.getWidth(),
                sprite.getHeight()
        );
    }

    private void setActorX(float actorX) {
        this.actorX = actorX;
    }

    private void setActorY(float actorY) {
        this.actorY = actorY;
    }

    private int getRow_index() {
        return row_index;
    }

    private int getColumn_index() {
        return column_index;
    }

    float getActorX() {
        return actorX;
    }

    float getActorY() {
        return actorY;
    }
}
