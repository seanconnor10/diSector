package com.disector.inputrecorder;

import com.badlogic.gdx.utils.Array;

public interface InputInterface {
    KeyPressData getKeyInfo(String actionName);
    boolean isKeyDown(int keyCode); //A name of keyDown intersects with InputMultiplexer::keyDown
    boolean justPressed(int keyCode);
    Array<InputInterface> getChildren();
    InputInterface getParent();
    InputBranch addChild();
}