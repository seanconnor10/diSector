package com.disector.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Stage {

    FrameBuffer frame;
    ShapeRenderer shape = new ShapeRenderer();
    SpriteBatch batch = new SpriteBatch();
    BitmapFont font = new BitmapFont( Gdx.files.local("assets/font/fira.fnt") );

    Array<Table> components = new Array<>();

    int scroll = 0;

    public Stage() {
        frame = new FrameBuffer(Pixmap.Format.RGBA8888, 1, 1, false);
        components.add( new Table() );
        components.add( new Table() );
    }

    public void render() {
        setStageObjectPositions();

        frame.begin();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.SALMON);
        for (Table component : components) {
             for (DataField dataField : component.fields) {
                drawShapeRect(dataField.rect);
             }
        }
        shape.end();

        batch.begin();
        for (Table component : components) {
            for (DataField dataField : component.fields) {
                font.draw(batch, dataField.name, dataField.rect.x, dataField.rect.y);
            }
        }
        batch.end();

        frame.end();
    }

    public void setStageObjectPositions() {
        final float LH = font.getLineHeight(); //Line Height
        float drawX = frame.getWidth()/2, drawY = 10;
        float thingWidth = frame.getWidth()-(drawX*2);
        for (Table component : components) {
            for (DataField dataField : component.fields) {
                dataField.rect.set(drawX, drawY, thingWidth * 0.6f, LH);
                dataField.rect.set(drawX + thingWidth*0.65f, drawY, thingWidth * 0.35f - drawX, LH);
                drawY += LH * 1.2f;
            }
            drawY += LH*0.5f;
        }
    }

    public void refreshPanelSize(float width, float height) {
        if (width < 1) width = 1;
        if (height < 1) height = 1;
        frame.dispose();
        frame = new FrameBuffer(Pixmap.Format.RGBA8888, (int) width, (int) height, false);
        shape.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, width, height));
        batch.setProjectionMatrix(shape.getProjectionMatrix());
    }

    public TextureRegion getTexture() {
        TextureRegion reg = new TextureRegion(frame.getColorBufferTexture());
        return new TextureRegion(reg);
    }

    private void drawShapeRect(Rectangle r) {
        shape.rect(r.x, r.y, r.width, r.height);
    }
}
