package com.disector.editor2;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

class Panel {
    Rectangle rect;

    final Array<Button> buttons = new Array<>();

    public Panel() {
        this.rect = new Rectangle();
    }

    public void setRect(int x, int y, int width, int height) {
        rect.x = x;
        rect.y = y;
        rect.width = width;
        rect.height = height;
    }
}
