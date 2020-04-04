package io.ash.simpletoe.screens.generic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;

public class DynamicScreen implements Screen {
    private final Color clearColor = new Color();
    private final Color startingClearColor = new Color();
    private final Color targetClearColor = new Color();
    private float elapsedClearColorChangeTime;
    private float clearChangeDuration;

    /**
     * set target color
     * @param colorHex hex num of target color
     * @param duration duration in secs on color transition
     */
    public void changeClearColor(int colorHex, float duration){
        targetClearColor.set(colorHex);
        startingClearColor.set(clearColor);
        elapsedClearColorChangeTime = 0;
        clearChangeDuration = duration;
    }

    /**
     * just update the color
     * @param delta current libgdx deltatime
     */
    protected void updateClearColor(float delta){
        if (elapsedClearColorChangeTime < clearChangeDuration){
            elapsedClearColorChangeTime = Math.min(elapsedClearColorChangeTime + delta, clearChangeDuration);
            clearColor.set(startingClearColor).lerp(targetClearColor, Interpolation.fade.apply(elapsedClearColorChangeTime / clearChangeDuration));
        }
        Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
    }

    public Color getClearColor() {
        return clearColor;
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
