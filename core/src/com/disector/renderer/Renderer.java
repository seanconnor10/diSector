package com.disector.renderer;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

    int frameWidth, frameHeight;
    float halfWidth, halfHeight;

    float camX, camY, camZ, camR;
    float camFOV = 100.f;
    int camCurrentSector;

    public Renderer(App app) {
        this.app = app;
        this.batch = app.batch;
        this.walls = app.walls;
        this.sectors = app.sectors;
        resizeFrame(app.frameWidth, app.frameHeight);
    }

    public abstract void renderWorld();

    public abstract void drawFrame();

    public void resizeFrame(int w, int h) {
        buffer = new Pixmap(w, h, pixelFormat);
        frameWidth = w;
        frameHeight = h;
        halfWidth = w / 2.f;
        halfHeight = h /2.f;
    }

    public void placeCamera(float x, float y, float z, float r, int camCurrentSector) {
        camX = x; camY = y; camZ = z; camR = r;
        this.camCurrentSector = camCurrentSector;
    }
}
