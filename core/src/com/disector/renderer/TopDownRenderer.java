package com.disector.renderer;

import com.badlogic.gdx.Gdx;
import com.disector.Application;

public abstract class TopDownRenderer extends Renderer{
    public TopDownRenderer(Application app) {
        super(app);
    }

    @Override
    public void resizeFrame(int w, int h) {
        frameWidth = Gdx.graphics.getWidth();
        frameHeight = Gdx.graphics.getHeight();
        halfWidth = frameWidth / 2.f;
        halfHeight = frameHeight /2.f;
    }
}
