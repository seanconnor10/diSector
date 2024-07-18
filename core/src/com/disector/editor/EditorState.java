package com.disector.editor;

import com.disector.editor.actions.EditAction;

abstract class EditorState {

    private final Editor editor;
    private final Panel panel;

    EditorState(Editor editor, Panel panel) {
        this.editor = editor;
        this.panel = panel;
    }

    abstract void step();

    abstract EditAction finish(); //Maybe return the EditAction for the undo stack here?
}
