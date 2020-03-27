package io.ash.simpletoe.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class NougthCell extends Cell {
    private Sprite sprite;

    NougthCell(int row, int column) {
        super(row, column);

        this.sprite = new Sprite(new Texture("circle.png"));
        this.updateBounds(sprite);
    }

    @Override
    protected void updateBounds(Sprite sprite) {
        super.updateBounds(sprite);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(Color.WHITE);
        if (this.isClicked())
            batch.setColor(Color.RED);
        batch.draw(this.sprite, this.getActorX(), this.getActorY());
    }
}
