package com.disector.editor;

import com.badlogic.gdx.math.Vector4;
import com.disector.Application;
import com.disector.inputrecorder.InputRecorder;
import com.disector.renderer.DimensionalRenderer;
import com.disector.renderer.Renderer;

public class Editor {
    private Application app;

    private float camX, camY, camZ, camR;

    public Editor(Application app) {
        this.app = app;
    }

    public void step(float dt) {
        boolean forwardDown = InputRecorder.getKeyInfo("FORWARD").isDown;
        boolean leftDown = InputRecorder.getKeyInfo("LEFT").isDown;
        boolean rightDown = InputRecorder.getKeyInfo("RIGHT").isDown;
        boolean backwardDown = InputRecorder.getKeyInfo("BACKWARD").isDown;
        boolean turnLeftDown = InputRecorder.getKeyInfo("TURN_LEFT").isDown;
        boolean turnRightDown = InputRecorder.getKeyInfo("TURN_RIGHT").isDown;
        boolean lookUpDown = InputRecorder.getKeyInfo("LOOK_UP").isDown;
        boolean lookDownDown = InputRecorder.getKeyInfo("LOOK_DOWN").isDown;

        if (forwardDown) {
            camX += 20f * dt;
        }
    }

    public Vector4 camPos() {
        return new Vector4(camX, camY, camZ, camR);
    }
}
