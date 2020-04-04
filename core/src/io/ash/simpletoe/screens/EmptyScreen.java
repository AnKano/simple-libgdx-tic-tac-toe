package io.ash.simpletoe.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import io.ash.simpletoe.ConfigurationStash;
import io.ash.simpletoe.screens.generic.DynamicScreen;

public class EmptyScreen extends DynamicScreen {

    public EmptyScreen() {
        // pre-init baked font for another screens. anyway, that's screen useless
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        int FONT_SIZE = 16;
        parameter.size = Math.round(FONT_SIZE * Gdx.graphics.getDensity());
        parameter.borderColor = Color.GRAY;
        parameter.borderWidth = 2;
        ConfigurationStash.bakedFont = generator.generateFont(parameter);
        generator.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(150f / 255f, 87f / 255f, 168f / 255f, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
    }
}
