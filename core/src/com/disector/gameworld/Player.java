package com.disector.gameworld;

import com.badlogic.gdx.math.Vector2;
import com.disector.inputrecorder.InputRecorder;

public class Player {
    private final GameWorld world;

    float x, y, z, r;
    Vector2 vel = new Vector2(0.f, 0.f);

    public Player(GameWorld world) {
        this.world = world;
    }

    void step(float dt) {
        boolean forwardDown = InputRecorder.getKeyInfo("P1_FORWARD").isDown;
        boolean leftDown = InputRecorder.getKeyInfo("P1_LEFT").isDown;
        boolean rightDown = InputRecorder.getKeyInfo("P1_RIGHT").isDown;
        boolean backwardDown = InputRecorder.getKeyInfo("P1_BACKWARD").isDown;
        boolean turnLeftDown = InputRecorder.getKeyInfo("P1_TURN_LEFT").isDown;
        boolean turnRightDown = InputRecorder.getKeyInfo("P1_TURN_RIGHT").isDown;


        final float MAX_SPEED = 100.f, ACCEL = 2.0f, TURN_SPEED = 3.0f;
        Vector2 inputVector = new Vector2(0.f, 0.f);
        if (forwardDown) inputVector.x += 1.0f;
        if (backwardDown) inputVector.x -= 1.0f;
        if (leftDown) inputVector.y += 1.0f;
        if (rightDown) inputVector.y -= 1.0f;
        inputVector.rotateRad(r);
        inputVector.nor();

        vel.add( new Vector2(inputVector).scl(ACCEL) );
        float currentSpeed = vel.len();
        if (currentSpeed > MAX_SPEED) vel.setLength(MAX_SPEED);

        x += vel.x * dt;
        y += vel.y * dt;

        if (inputVector.isZero(0.01f)) vel.scl( 1.f - 10.f*dt);

        //r += InputRecorder.mouseDeltaX * 0.02f;
        if (turnLeftDown) r += TURN_SPEED*dt;
        if (turnRightDown) r -= TURN_SPEED*dt;

    }


}