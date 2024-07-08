package com.disector.editor2;

import com.badlogic.gdx.math.Rectangle;

class Button {
    final Rectangle panelRect;
    final Rectangle rect;

    Button(Panel p) {
        this.rect = new Rectangle();
        panelRect = p.rect;
    }

    void setRect(int x, int y, int width, int height) {
        rect.x = x;
        rect.y = y;
        rect.width = width;
        rect.height = height;
    }
}
