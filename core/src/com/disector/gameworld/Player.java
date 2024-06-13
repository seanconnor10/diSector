package com.disector.gameworld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import com.disector.Sector;
import com.disector.Wall;
import com.disector.gameworld.components.Movable;
import com.disector.inputrecorder.InputRecorder;

public class Player implements Movable {
    private final GameWorld world;

    Vector2 position = new Vector2(0.f, 0.f);
    float z, r;
    float vLook; // 'Angle' of vertical view direction
    float height;
    Vector2 velocity = new Vector2(0.f, 0.f);
    float zSpeed;
    int currentSectorIndex;
    boolean onGround;

    final float MAX_SPEED = 150.f, ACCEL = 4.0f;
    final float MOUSE_SENS_X = 0.002f, MOUSE_SENS_Y = 0.5f;
    final float TURN_SPEED = 3.0f, VLOOK_SPEED = 200.0f;
    final float VLOOK_CLAMP = 275.f;
    final int STANDING_HEIGHT = 20;
    final int CROUCHING_HEIGHT = 5;
    final int HEAD_SPACE = 0;
    final float RADIUS = 10.f;

    Player(GameWorld world) {
        this.world = world;
    }

    public Vector2 movementInput(float dt) {
        Vector2 startingPosition = copyPosition();

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
        Vector2 inputVector = new Vector2(0.f, 0.f);
        if (forwardDown) inputVector.x += 1.0f;
        if (backwardDown) inputVector.x -= 1.0f;
        if (leftDown) inputVector.y += 1.0f;
        if (rightDown) inputVector.y -= 1.0f;
        inputVector.rotateRad(r);
        inputVector.nor();

        //Update velocity with input vector
        velocity.add( new Vector2(inputVector).scl(ACCEL) );
        float currentSpeed = velocity.len();
        if (currentSpeed > MAX_SPEED) velocity.setLength(MAX_SPEED);

        //Friction when not inputting
        if (inputVector.isZero(0.05f)) velocity.scl( 1.f - 3.f*dt);

        //Rotate player + look up and down
        if (Gdx.input.isCursorCatched()) {
            r -= InputRecorder.mouseDeltaX * MOUSE_SENS_X;
            vLook -= InputRecorder.mouseDeltaY * MOUSE_SENS_Y;
        }
        if (turnLeftDown) r += TURN_SPEED*dt;
        if (turnRightDown) r -= TURN_SPEED*dt;
        if (lookUpDown) vLook += VLOOK_SPEED*dt;
        if (lookDownDown) vLook -= VLOOK_SPEED*dt;
        vLook = Math.min( Math.max(vLook, -VLOOK_CLAMP), VLOOK_CLAMP );

        //Return starting position for collision function to use
        return startingPosition;
    }

    public void verticalMovement(float dt, Array<Wall> walls, Array<Sector> sectors) {
        Sector currentSector = sectors.get(currentSectorIndex);
        float secFloor = currentSector.floorZ, secCeil = currentSector.ceilZ;

        //Crouching
        height = (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) ? CROUCHING_HEIGHT : STANDING_HEIGHT;



        //Jump
        if (onGround && Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            zSpeed = 100.0f;

//        //Grav
//        if (z > secFloor) zSpeed -= 200.f * dt; if (zSpeed < -100.0f) zSpeed = -100.0f;
//        //Enact motion
//        z += zSpeed * dt;
//        //Hit Floor
//        if (z<secFloor) {z = secFloor; zSpeed = 0.f;}

    }

    //Positionable Implementations //////////////
    @Override
    public Vector2 copyPosition() {
        return new Vector2(position);
    }

    @Override
    public Vector3 copyPosition3D() {
        return new Vector3(position.x, position.y, z);
    }

    @Override
    public float getZ() {
        return z;
    }

    @Override
    public int getCurrentSector() {
        return currentSectorIndex;
    }

    @Override
    public void setCurrentSector(int sInd) {
        currentSectorIndex = sInd;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public float getRadius() {
        return RADIUS;
    }

    //Movable Implementations ////////////////
    @Override
    public Vector2 snagVelocity() {
        return velocity;
    }

    @Override
    public float getZSpeed() {
        return zSpeed;
    }

    @Override
    public Vector2 snagPosition() {
        return position;
    }

    @Override
    public Vector2 copyVelocity() {
        return new Vector2(velocity);
    }

    @Override
    public void setZSpeed(float zSpeed) {
        this.zSpeed = zSpeed;
    }

    @Override
    public void setZ(float z) {
        this.z = z;
    }

    @Override
    public void setOnGround(boolean val) {
        onGround = val;
    }

    @Override
    public boolean isOnGround() {
        return onGround;
    }
}