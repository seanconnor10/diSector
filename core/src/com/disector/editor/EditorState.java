package com.disector.editor;

import com.disector.editor.actions.EditAction;

abstract class EditorState {
    String visibleName = "MISSING STATE NAME";

    final Editor editor;
    final Panel panel;

    boolean ignoreEditorClick;
    boolean shouldFinish;

    EditorState(Editor editor, Panel panel) {
        this.editor = editor;
        this.panel = panel;
    }

    abstract void step();

    abstract void click();

    abstract void rightClick();

    abstract EditAction[] finish(); //Maybe return the EditAction for the undo stack here?

    int x(){return editor.isGridSnapping ? xSnapped() : xUnSnapped();}

    int y(){return editor.isGridSnapping ? ySnapped() : yUnSnapped();}

    int xUnSnapped() {return editor.mapPanel.getMouseWorldX();}

    int yUnSnapped() {return editor.mapPanel.getMouseWorldY();}

    int xSnapped() {return editor.snap(xUnSnapped());}

    int ySnapped() {return editor.snap(yUnSnapped());}

}
