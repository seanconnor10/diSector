package com.disector.gui;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Table {
    boolean isFolded;

    Rectangle rect = new Rectangle();

    Array<DataField> fields = new Array<>();

    public Table() {
        fields.add( new FloatDataField("Floor"));
        fields.add( new FloatDataField("Ceiling"));
    }
}
