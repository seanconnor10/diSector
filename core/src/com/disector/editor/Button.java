package com.disector.editor;

import com.badlogic.gdx.math.Rectangle;

class Button {
    String text = "";

    final Panel panel;
    final Editor editor;
    final Rectangle panelRect;
    final Rectangle rect;

    boolean pressed;

    Button(Editor editor, Panel panel, String text) {
        this.editor = editor;
        this.panel = panel;
        this.rect = new Rectangle();
        this.text = text;
        panelRect = panel.rect;
    }

    void setRect(int x, int y, int width, int height) {
        rect.x = x;
        rect.y = y;
        rect.width = width;
        rect.height = height;
    }
}
