package com.disector.gui;

import com.badlogic.gdx.math.Rectangle;

import java.util.function.Function;

public class DataField {
    protected String name;

    Rectangle rect = new Rectangle();

    Function<Void, Void> finalizeAction = (Void) -> {
        System.out.println("DataField has no action..");
        return Void;
    };

    public DataField(String name) {
        this.name = name;
    }
}
