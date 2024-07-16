package com.disector.editor;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.function.Function;

class Panel {
    Editor editor;
    Rectangle rect;

    Function<Void, Void> stepFunction = (Void) -> {return Void;};

    boolean isForcingMouseFocus;

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
        int placedButtons = 0;
        for (Button b : buttons) {
            if (b.active) {
                b.rect.set(2 + placedButtons * 140, 2, 100, 28);
                placedButtons++;
            } else {
                b.rect.set(-1,-1,-1,-1);
            }
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

    void clickedIn() {

    }

    void step() {

    }

}
