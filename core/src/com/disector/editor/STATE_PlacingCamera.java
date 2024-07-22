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

    @Override
    void init() {
        cameraX = x();
        cameraY = y();
        editor.viewRenderer.placeCamera(cameraX, cameraY);
        editor.viewRenderer.camCurrentSector = Physics.findCurrentSectorBranching(
                editor.viewRenderer.camCurrentSector,
                cameraX,
                cameraY
        );
        editor.shouldUpdateViewRenderer = true;
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
    EditAction[] finish() {
        editor.state = null;
        return new EditAction[0];
    }

    private int x(){
        return ((MapPanel) panel).getMouseWorldX();
    }

    private int y(){
        return ((MapPanel) panel).getMouseWorldY();
    }
}
