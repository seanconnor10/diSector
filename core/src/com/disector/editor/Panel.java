package com.disector.editor;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

class Panel {
    Editor editor;
    Rectangle rect;

    final Array<Button> buttons = new Array<>();

    public Panel(Editor editor) {
        this.editor = editor;
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

    Button getButtonByText(String text) {
        //Needing .toArray() makes this first method probably slower?
        /*return Arrays.stream(buttons.toArray())
                .filter( b -> b.text.equals(text) ).findFirst().orElse(null);*/
        for (Button b : buttons) {
            if (b.text.equalsIgnoreCase(text))
                return b;
        }
        return null;
    }

}
