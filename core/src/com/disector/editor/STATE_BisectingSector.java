package com.disector.editor;

import com.disector.editor.actions.EditAction;

public class STATE_BisectingSector extends EditorState {
    public STATE_BisectingSector(Editor editor, Panel panel) {
        super(editor, panel);
    }

    @Override
    void step() {

    }

    @Override
    void click() {

    }

    @Override
    void rightClick() {

    }

    @Override
    EditAction[] finish() {
        return new EditAction[0];
    }
}
