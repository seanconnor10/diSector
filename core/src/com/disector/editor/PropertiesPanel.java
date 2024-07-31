package com.disector.editor;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import com.disector.Sector;
import com.disector.Wall;

class PropertiesPanel extends Panel {
    private final static Pixmap.Format pixelFormat = Pixmap.Format.RGBA8888;

    int chosenSectorIndex = -1;
    Sector chosenSector = null;

    FrameBuffer frame;
    ShapeRenderer shape = new ShapeRenderer();

    PropertiesPanel(Editor editor) {
        super(editor);
        frame = new FrameBuffer(pixelFormat, 1, 1, false);
        refreshPanelSize(rect);
    }

    void refreshPanelSize(Rectangle r) {
        frame.dispose();
        int w = Math.max( (int) r.width,  1 );
        int h = Math.max( (int) r.height, 1 );
        frame = new FrameBuffer(pixelFormat, w, h, false);
        shape.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, w, h));
    }

    private void choseSectorByIndex(int sInd) {
        if (sInd >= editor.sectors.size || sInd < 0) {
            chosenSector = null;
            chosenSectorIndex = -1;
            return;
        }
        chosenSectorIndex = sInd;
        chosenSector = editor.sectors.get(chosenSectorIndex);
    }

}
