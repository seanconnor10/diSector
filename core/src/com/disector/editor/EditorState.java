package com.disector.editor;

import com.disector.editor.actions.EditAction;

abstract class EditorState {

    final Editor editor;
    final Panel panel;

    boolean ignoreEditorClick;

    EditorState(Editor editor, Panel panel) {
        this.editor = editor;
        this.panel = panel;
    }

    abstract void init();

    abstract void step();

    abstract void click();

    abstract EditAction[] finish(); //Maybe return the EditAction for the undo stack here?
}
