package com.disector.editor;

class MapPanel extends Panel {
    public MapPanel(Editor editor) {
        super(editor);
    }

    @Override
    void clickedIn() {
        super.clickedIn();

        if (editor.state == null) {
            //editor.state = new STATE_PlacingCamera(editor, this);
            editor.state = new STATE_CreatingSector(editor, this);
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
}
