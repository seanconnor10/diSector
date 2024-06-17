package com.disector.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector4;
import com.disector.Application;
import com.disector.inputrecorder.InputRecorder;

public class Editor {
    private Application app;

    public Rectangle renderWindPos = new Rectangle();

    private float camX, camY, camZ, camR, camV;

    final float MOVE_SPEED = 100.f;
    final float MOUSE_SENS_X = 0.002f, MOUSE_SENS_Y = 0.5f;
    final float TURN_SPEED = 3.0f, VLOOK_SPEED = 200.0f;
    final float VLOOK_CLAMP = 300.f;

    public Editor(Application app) {
        this.app = app;
        renderWindPos.set(0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void step(float dt) {
        cameraControls(dt);



    }

    public Vector4 camPos() {
        return new Vector4(camX, camY, camZ, camR);
    }

    public float camVLook() {
        return camV;
    }

    private void cameraControls(float dt) {
        boolean forwardDown = InputRecorder.getKeyInfo("FORWARD").isDown;
        boolean leftDown = InputRecorder.getKeyInfo("LEFT").isDown;
        boolean rightDown = InputRecorder.getKeyInfo("RIGHT").isDown;
        boolean backwardDown = InputRecorder.getKeyInfo("BACKWARD").isDown;
        boolean turnLeftDown = InputRecorder.getKeyInfo("TURN_LEFT").isDown;
        boolean turnRightDown = InputRecorder.getKeyInfo("TURN_RIGHT").isDown;
        boolean lookUpDown = InputRecorder.getKeyInfo("LOOK_UP").isDown;
        boolean lookDownDown = InputRecorder.getKeyInfo("LOOK_DOWN").isDown;

        Vector2 inputVector = new Vector2();
        if (forwardDown) inputVector.x += 1.0f;
        if (backwardDown) inputVector.x -= 1.0f;
        if (leftDown) inputVector.y += 1.0f;
        if (rightDown) inputVector.y -= 1.0f;
        inputVector.rotateRad(camR);
        inputVector.nor();

        camX += inputVector.x*MOVE_SPEED*dt;
        camY += inputVector.y*MOVE_SPEED*dt;

        //Rotate player + look up and down
        if (Gdx.input.isCursorCatched()) {
            camR -= InputRecorder.mouseDeltaX * MOUSE_SENS_X;
            camV -= InputRecorder.mouseDeltaY * MOUSE_SENS_Y;
        }
        if (turnLeftDown) camR += TURN_SPEED*dt;
        if (turnRightDown) camR -= TURN_SPEED*dt;
        if (lookUpDown) camV += VLOOK_SPEED*dt;
        if (lookDownDown) camV -= VLOOK_SPEED*dt;
        camV = Math.min( Math.max(camV, -VLOOK_CLAMP), VLOOK_CLAMP );
    }
}
