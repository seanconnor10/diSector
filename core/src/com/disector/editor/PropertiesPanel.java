package com.disector.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.utils.ScreenUtils;
import com.disector.Application;
import com.disector.Sector;
import com.disector.Wall;
import com.disector.gui.Stage;

class PropertiesPanel extends Panel {
    private final static Pixmap.Format pixelFormat = Pixmap.Format.RGBA8888;
    private final static BitmapFont font = new BitmapFont( Gdx.files.local("assets/font/fira.fnt") );

    int chosenSectorIndex = -1;
    Sector chosenSector = null;

    FrameBuffer frame;
    ShapeRenderer shape = new ShapeRenderer();
    SpriteBatch batch = new SpriteBatch();

    Stage sectorFieldsStage = new Stage();

    PROPERTIES_PANEL_STATES state = PROPERTIES_PANEL_STATES.SHOW_SECTOR_FIELDS;

    PropertiesPanel(Editor editor) {
        super(editor);
        frame = new FrameBuffer(pixelFormat, 1, 1, false);
        refreshPanelSize(rect);
        font.setColor(Color.WHITE);
    }

    void refreshPanelSize(Rectangle r) {
        frame.dispose();
        int w = Math.max( (int) r.width,  1 );
        int h = Math.max( (int) r.height, 1 );
        frame = new FrameBuffer(pixelFormat, w, h, false);
        shape.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, w, h));
        batch.setProjectionMatrix(shape.getProjectionMatrix());

        sectorFieldsStage.refreshPanelSize(r.width, r.height);
    }

    void render() {
        sectorFieldsStage.render();

        frame.begin();
        ScreenUtils.clear(Color.CLEAR);
        drawByState();
        frame.end();
    }

    private void drawByState() {
        switch(state) {
            case DISAMBIGUATE_SECTOR_SELECTION:
                //draw_DISAMBIGUATE_SECTOR_SELECTION();
                break;
            case SHOW_SECTOR_FIELDS:
                draw_SHOW_SECTOR_FIELDS();
                break;
            default:
                break;
        }
    }

    private void draw_SHOW_SECTOR_FIELDS() {
        Sector sec = editor.selection.highlightedSector;
        int sInd = editor.selection.highlightedSectorIndex;

        if (sec == null) return;

        TextureRegion reg = sectorFieldsStage.getTexture();
        batch.begin();
        batch.draw(sectorFieldsStage.getTexture(), 0, 0 );
        batch.end();

        /*final float LH = font.getLineHeight() + 6;
        final float ST = rect.height - LH;

        batch.begin();
            font.draw(batch,
                "HIGHLIGHTED SECTOR Index: " + sInd,
                30, ST
            );
            font.draw(batch,
                "Floor Z: " + sec.floorZ,
                30, ST - LH
            );
            font.draw(batch,
                 "Ceiling Z: " + sec.ceilZ,
                30, ST - LH*2
            );
        batch.end();*/

    }

}
