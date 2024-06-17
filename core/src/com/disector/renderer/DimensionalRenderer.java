package com.disector.renderer;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.disector.Application;

public abstract class DimensionalRenderer extends Renderer{
    Pixmap buffer;

    public DimensionalRenderer(Application app) {
        super(app);
        resizeFrame(app.frameWidth, app.frameHeight);
    }

    public void drawFrame() {
        TextureRegion frame = copyPixels();
        frame.flip(false, true);
        batch.begin();
        batch.draw(frame,0 , 0);
        batch.end();
        frame.getTexture().dispose();
    }

    @Override
    public void resizeFrame(int w, int h) {
        buffer = new Pixmap(w, h, pixelFormat);
        buffer.setColor(0x000000FF);
        frameWidth = w;
        frameHeight = h;
        halfWidth = w / 2.f;
        halfHeight = h /2.f;
        batch.setProjectionMatrix( new Matrix4().setToOrtho2D(0,0,frameWidth,frameHeight) );
    }

    @Override
    public TextureRegion copyPixels() {
        //Must dispose() the TextureRegion sometime after calling this
        return new TextureRegion(new Texture((buffer)), buffer.getWidth(), buffer.getHeight());
    }
}
