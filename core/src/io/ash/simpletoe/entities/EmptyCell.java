package io.ash.simpletoe.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class EmptyCell extends Cell {
    private Sprite sprite;

    public EmptyCell(int row, int column) {
        super(row, column);

        sprite = new Sprite(new Texture("empty.png"));
        this.updateBounds(sprite);
    }

    @Override
    protected void updateBounds(Sprite sprite) {
        super.updateBounds(sprite);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(Color.WHITE);
        batch.draw(this.sprite, this.getActorX(), this.getActorY());
    }
}
