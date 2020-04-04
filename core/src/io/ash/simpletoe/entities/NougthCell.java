package io.ash.simpletoe.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import io.ash.simpletoe.ConfigurationStash;
import io.ash.simpletoe.enums.Figures;

public class NougthCell extends Cell {
    private Sprite sprite;

    public NougthCell(int row, int column) {
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
        if (ConfigurationStash.figure == Figures.NOUGHT)
            batch.setColor(Color.GREEN);
        else
            batch.setColor(Color.RED);
        batch.draw(this.sprite, this.getActorX(), this.getActorY());
    }
}
