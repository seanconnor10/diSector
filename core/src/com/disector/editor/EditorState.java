package com.disector.editor;

import com.disector.editor.actions.EditAction;

abstract class EditorState {

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

    int x(){
        int x = editor.mapPanel.getMouseWorldX();
        if (editor.isGridSnapping) x = editor.snap(x);
        return x;
    }

    int y(){
        int y = editor.mapPanel.getMouseWorldY();
        if (editor.isGridSnapping) y = editor.snap(y);
        return y;
    }
 
}
