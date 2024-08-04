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

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void disable() {
        isActive = false;
        for (InputInterface child : children) {
            child.disable();
        }
    }

    @Override
    public void enable() {
        isActive = true;
        parent.enable();
        for (InputInterface child : children) {
            child.disable();
        }
    }
}
