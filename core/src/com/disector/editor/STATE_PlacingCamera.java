package com.disector.editor;

import com.disector.Physics;
import com.disector.editor.actions.EditAction;

class STATE_PlacingCamera extends EditorState{

    int cameraX, cameraY;

    public STATE_PlacingCamera(Editor editor, Panel panel) {
        super(editor, panel);
        ignoreEditorClick = true;
        init();
    }

    void init() {
        cameraX = x();
        cameraY = y();
        editor.placeViewCamera(cameraX, cameraY);
    }

    @Override
    void step() {
        int x = x();
        int y = y();
        editor.viewRenderer.camR = (float) Math.atan((double)(y-cameraY)/(double)(x-cameraX) );
        if (x<cameraX) editor.viewRenderer.camR += (float) Math.PI;
        editor.shouldUpdateViewRenderer = true;
    }

    @Override
    void click() {
        finish();
    }

    @Override
    void rightClick() {

    }

    @Override
    EditAction[] finish() {
        editor.state = null;
        return new EditAction[0];
    }

}
