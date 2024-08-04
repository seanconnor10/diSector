package com.disector.inputrecorder;

import com.badlogic.gdx.utils.Array;

public class InputBranch implements InputInterface {
    private final InputInterface parent;
    private final Array<InputInterface> children;

    private boolean isActive = false;

    public InputBranch(InputInterface inputMaster) {
        this.parent = inputMaster;
        children = new Array<>();
    }

    @Override
    public KeyPressData getKeyInfo(String actionName) {
        return isActive ? parent.getKeyInfo(actionName) : KeyPressData.BLANK;
    }

    @Override
    public boolean isKeyDown(int keyCode) {
        return isActive && parent.isKeyDown(keyCode);
    }

    @Override
    public boolean justPressed(int keyCode) {
        return isActive && parent.justPressed(keyCode);
    }

    @Override
    public InputBranch addChild() {
        InputBranch newBranch = new InputBranch(this);
        children.add(newBranch);
        return newBranch;
    }

    @Override
    public Array<InputInterface> getChildren() {
        return children;
    }

    @Override
    public InputInterface getParent() {
        return parent;
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
