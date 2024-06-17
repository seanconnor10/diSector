package com.disector.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.disector.Application;

public abstract class TopDownRenderer extends Renderer{
    FrameBuffer buffer;

    public TopDownRenderer(Application app) {
        super(app);
        resizeFrame(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void resizeFrame(int w, int h) {
        //w and h should probably always be Gdx.graphics.getWidth/Height?
        buffer = new FrameBuffer(app.pixelFormat, w, h,false);
        frameWidth = w;
        frameHeight = h;
        halfWidth = frameWidth / 2.f;
        halfHeight = frameHeight /2.f;
    }

    @Override
    public TextureRegion copyPixels() {
        return new TextureRegion(buffer.getColorBufferTexture());
    }
}
