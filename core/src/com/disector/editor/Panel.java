package com.disector.editor;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

class Panel {
    Rectangle rect;

    final Array<Button> buttons = new Array<>();

    public Panel() {
        this.rect = new Rectangle();
    }

    void resize(Rectangle rect) {
        resize(rect.x, rect.y, rect.width, rect.height);
    }

    void resize(float x, float y, float width, float height) {
        rect.set(x, y, width, height);
        rearrangeButtons();
    }

    void rearrangeButtons() {
        for (int i=0; i<buttons.size; i++) {
            buttons.get(i).rect.set(2 + i*140, 2, 100, 28);
        }
    }

}
