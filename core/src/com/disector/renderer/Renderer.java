package com.disector.renderer;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Array;

import com.disector.Application;
import com.disector.Sector;
import com.disector.Wall;

public abstract class Renderer {
    final Pixmap.Format pixelFormat;

    final Application app;
    final Array<Wall> walls;
    final Array<Sector> sectors;
    final SpriteBatch batch;

    Pixmap buffer;

    int frameWidth, frameHeight;
    float halfWidth, halfHeight;

    float camX, camY, camZ, camR, camVLook;
    float camFOV = 175.f;
    int camCurrentSector;

    public Renderer(Application app) {
        this.app = app;
        this.pixelFormat = app.pixelFormat;
        this.batch = app.batch;
        this.walls = app.walls;
        this.sectors = app.sectors;
        resizeFrame(app.frameWidth, app.frameHeight);
    }

    public abstract void renderWorld();

    public void setFov(float val) {
        camFOV = val;
    }

    public void drawFrame() {
        TextureRegion frame = new TextureRegion(new Texture((buffer)), buffer.getWidth(), buffer.getHeight());
        frame.flip(false, true);
        batch.begin();
        batch.draw(frame, 0, 0);
        batch.end();
        frame.getTexture().dispose();
    }

    public void resizeFrame(int w, int h) {
        buffer = new Pixmap(w, h, pixelFormat);
        buffer.setColor(0x000000FF);
        frameWidth = w;
        frameHeight = h;
        halfWidth = w / 2.f;
        halfHeight = h /2.f;
        batch.setProjectionMatrix( new Matrix4().setToOrtho2D(0,0,frameWidth,frameHeight) );
    }

    public void placeCamera(Vector4 pos, float vLook, int camCurrentSector) {
        camX = pos.x; camY = pos.y; camZ = pos.z; camR = pos.w; camVLook = vLook;
        this.camCurrentSector = camCurrentSector;
    }
}
