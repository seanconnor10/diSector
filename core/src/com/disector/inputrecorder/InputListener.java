package com.disector.inputrecorder;

public class InputListener implements InputInterface {
    private final InputInterface inputMaster;

    private boolean isActive = false;

    public InputListener(InputRecorder inputMaster) {
        this.inputMaster = inputMaster;
    }

    public KeyPressData getKeyInfo(String actionName) {
        if (!isActive)
            return KeyPressData.BLANK;

        return inputMaster.getKeyInfo(actionName);
    }

    public boolean isActive() {
        return isActive;
    }

    public void disable() {
        isActive = false;
    }

    public void enable() {
        isActive = true;
    }
}
