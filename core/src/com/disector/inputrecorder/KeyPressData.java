package com.disector.inputrecorder;

public class KeyPressData {
    public static KeyPressData BLANK = new KeyPressData();

    public boolean isDown, justPressed, justReleased;

    public KeyPressData() {
    }

    public KeyPressData(boolean isDown, boolean justPressed, boolean justReleased) {
        this.isDown = isDown;
        this.justPressed = justPressed;
        this.justReleased = justReleased;
    }

    @Override
    public String toString() {
        String str =
                (isDown       ? "CurrentlyPressed " : "") +
                (justPressed  ? "NewlyPressed "     : "") +
                (justReleased ? "NewlyReleased "    : "");
        return str.isEmpty() ? "none" : str;
    }
}
