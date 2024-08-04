package com.disector.inputrecorder;

import jdk.internal.util.xml.impl.Input;

public class KeyPressData {
    public static KeyPressData BLANK = new KeyPressData();

    public boolean isDown, justPressed, justReleased;

    @Override
    public String toString() {
        String str = (isDown ? "CurrentlyPressed " : "") + (justPressed ? "NewlyPressed " : "") + (justReleased ? "NewlyReleased " : "");
        if (str.isEmpty()) str = "none";

        return str;
    }
}
