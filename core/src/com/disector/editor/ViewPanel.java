package com.disector.editor;

class ViewPanel extends Panel{
    public ViewPanel(Editor editor) {
        super(editor);
    }

    @Override
    void clickedIn() {
        isForcingMouseFocus = true;
    }
}
