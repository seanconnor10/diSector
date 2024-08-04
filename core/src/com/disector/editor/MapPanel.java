package com.disector.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Null;

class MapPanel extends Panel {
    public MapPanel(Editor editor) {
        super(editor);
    }

    @Override
    void step(float dt) {
        super.step(dt);

        if (editor.state == null) {
            editor.selection.setHighlights(getMouseWorldX(), getMouseWorldY());
        }

        keyActions();

        editor.selection.setHighlights(getMouseWorldX(), getMouseWorldY());
    }

    @Override
    void clickedIn() {
        super.clickedIn();

        if (editor.state == null) {
            editor.state = new STATE_PlacingCamera(editor, this);
        }
    }

    int getMouseWorldX() {
        float mouseX = relX();
        int mapRendererCenterX = Math.round(editor.mapRenderer.camX);
        return (int) ( mapRendererCenterX + (mouseX - (rect.width/2.0)) / editor.mapRenderer.zoom );
    }

    int getMouseWorldY() {
        float mouseY = relY();
        int mapRendererCenterY = Math.round(editor.mapRenderer.camY);
        return (int) ( mapRendererCenterY + (mouseY - (rect.height/2.0)) / editor.mapRenderer.zoom );
    }

    private void keyActions() {
        if (editor.state != null) {
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            editor.state = new STATE_ExtrudingSector(editor, this);
            return;
        }
        if (!Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            editor.state = new STATE_SplittingWall(editor, this);
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            editor.state = new STATE_CreatingSector(editor, this);
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            editor.state = new STATE_MovingVertices(editor, this);
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            editor.state = new STATE_MakeInnerSubSector(editor, this);
            return;
        }

    }

}
