package com.disector.editor;

import com.badlogic.gdx.math.Rectangle;

public class Button {
    Rectangle rect = new Rectangle();
    String text = "";

    public Button() {
    }

    public Button(String text) {
        this.text = text;
    }

    void setRect(float x, float y, float w, float h) {
        rect.x = x;
        rect.y = y;
        rect.width = w;
        rect.height = h;
    }
}
