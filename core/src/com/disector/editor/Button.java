package com.disector.editor;

import com.badlogic.gdx.math.Rectangle;

class Button {
    String text = "";

    final Rectangle panelRect;
    final Rectangle rect;

    Button(Panel p, String text) {
        this.rect = new Rectangle();
        this.text = text;
        panelRect = p.rect;
    }

    void setRect(int x, int y, int width, int height) {
        rect.x = x;
        rect.y = y;
        rect.width = width;
        rect.height = height;
    }
}
