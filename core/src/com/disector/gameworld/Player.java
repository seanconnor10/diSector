package com.disector.gameworld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.disector.inputrecorder.InputRecorder;

public class Player implements Positionable{
    private final GameWorld world;

    int currentSectorIndex;
    float x, y, z, r;
    float vLook; // 'Angle' of vertical view direction
    Vector2 vel = new Vector2(0.f, 0.f);

    Player(GameWorld world) {
        this.world = world;
    }

    void step(float dt) {
        Vector2 lastPosition = movementInput(dt);
    }

    private Vector2 movementInput(float dt) {
        Vector2 startingPosition = new Vector2(x, y);

        //Record needed button presses
        boolean forwardDown = InputRecorder.getKeyInfo("FORWARD").isDown;
        boolean leftDown = InputRecorder.getKeyInfo("LEFT").isDown;
        boolean rightDown = InputRecorder.getKeyInfo("RIGHT").isDown;
        boolean backwardDown = InputRecorder.getKeyInfo("BACKWARD").isDown;
        boolean turnLeftDown = InputRecorder.getKeyInfo("TURN_LEFT").isDown;
        boolean turnRightDown = InputRecorder.getKeyInfo("TURN_RIGHT").isDown;
        boolean lookUpDown = InputRecorder.getKeyInfo("LOOK_UP").isDown;
        boolean lookDownDown = InputRecorder.getKeyInfo("LOOK_DOWN").isDown;

        //Find input vector
        final float MAX_SPEED = 150.f, ACCEL = 2.0f, TURN_SPEED = 3.0f;
        Vector2 inputVector = new Vector2(0.f, 0.f);
        if (forwardDown) inputVector.x += 1.0f;
        if (backwardDown) inputVector.x -= 1.0f;
        if (leftDown) inputVector.y += 1.0f;
        if (rightDown) inputVector.y -= 1.0f;
        inputVector.rotateRad(r);
        inputVector.nor();

        //Update velocity with input vector
        vel.add( new Vector2(inputVector).scl(ACCEL) );
        float currentSpeed = vel.len();
        if (currentSpeed > MAX_SPEED) vel.setLength(MAX_SPEED);

        //Update postion with velocity
        x += vel.x * dt;
        y += vel.y * dt;

        //Friction when not inputting
        if (inputVector.isZero(0.05f)) vel.scl( 1.f - 10.f*dt);

        //Rotate player + look up and down
        if (Gdx.input.isCursorCatched())
            r -= InputRecorder.mouseDeltaX * 0.02f;
        if (turnLeftDown) r += TURN_SPEED*dt;
        if (turnRightDown) r -= TURN_SPEED*dt;
        if (lookUpDown) vLook += TURN_SPEED*dt;
        if (lookDownDown) vLook -= TURN_SPEED*dt;


        //Return starting position for collision function to use
        return startingPosition;
    }

    @Override
    public Vector3 getPos() {
        return new Vector3(x,y,z);
    }

    @Override
    public int getCurrentSector() {
        return currentSectorIndex;
    }

    @Override
    public void setCurrentSector(int sInd) {
        currentSectorIndex = sInd;
    }
}