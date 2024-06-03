package com.disector.renderer;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.disector.App;
import com.disector.Sector;
import com.disector.Wall;

public abstract class Renderer {
    final static Pixmap.Format pixelFormat = Pixmap.Format.RGBA8888;

    final App app;
    final Array<Wall> walls;
    final Array<Sector> sectors;
    final SpriteBatch batch;

    Pixmap buffer;

    public Renderer(App app) {
        this.app = app;
        this.batch = app.batch;
        this.walls = app.walls;
        this.sectors = app.sectors;
        resizeFrame(app.frameWidth, app.frameHeight);
    }

    public abstract void renderWorld();

    public void drawFrame() {
        batch.begin();
        TextureRegion frame = new TextureRegion(new Texture((buffer)), buffer.getWidth(), buffer.getHeight());
        frame.flip(false, true);
        batch.draw(frame, 0, 0);
        batch.end();
        frame.getTexture().dispose();
    }

    public void resizeFrame(int w, int h) {
        buffer = new Pixmap(w, h, pixelFormat);
    }
}
