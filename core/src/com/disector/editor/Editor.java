package com.disector.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.ScreenUtils;

import com.disector.Application;
import com.disector.inputrecorder.InputRecorder;
import com.disector.renderer.DimensionalRenderer;
import com.disector.renderer.EditorTopDownRenderer;
import com.disector.renderer.SoftwareRenderer;

public class Editor {
    private Application app;

    private DimensionalRenderer _3DRenderer;
    private EditorTopDownRenderer topDownRenderer;

    public Rectangle renderWindPos = new Rectangle();

    private float camX, camY, camZ = 30, camR, camV;

    final float MOVE_SPEED = 100.f;
    final float MOUSE_SENS_X = 0.002f, MOUSE_SENS_Y = 0.5f;
    final float TURN_SPEED = 3.0f, VLOOK_SPEED = 200.0f;
    final float VLOOK_CLAMP = 300.f;

    public Editor(Application app) {
        this.app = app;
        _3DRenderer = new SoftwareRenderer(app);
        topDownRenderer = new EditorTopDownRenderer(app, 600, 400);

        renderWindPos.set(0, 0, 800, 450);
    }

    public void step(float dt) {
        cameraControls(dt);
    }

    public void draw(SpriteBatch batch) {
        TextureRegion renderView = getRenderView();
        TextureRegion mapView = getMapView();

        batch.begin();
        ScreenUtils.clear(Color.LIGHT_GRAY);
        batch.draw(renderView, 0, Gdx.graphics.getHeight()-renderWindPos.height-renderWindPos.y, renderWindPos.width, renderWindPos.height);
        batch.draw(mapView, 600, Gdx.graphics.getHeight()-renderWindPos.height-renderWindPos.y, renderWindPos.width, renderWindPos.height);

        batch.end();

        renderView.getTexture().dispose();
    }

    private TextureRegion getRenderView() {
        TextureRegion renderView;
        renderView = _3DRenderer.copyPixels();
        renderView.flip(false, true);
        return renderView;
    }

    private void updateRenderView() {
        _3DRenderer.placeCamera(new Vector4(camX, camY, camZ, camR), camV, 0);
        _3DRenderer.renderWorld();
    }

    private void updateMapView() {
        topDownRenderer.placeCamera(new Vector4(camX, camY, camZ, camR), camV, 0);
        topDownRenderer.renderWorld();
    }

    private TextureRegion getMapView() {
        TextureRegion mapView;
        mapView = topDownRenderer.copyPixels();
        mapView.flip(false, true);
        return mapView;
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

        boolean viewHasChanged = false;

        Vector2 inputVector = new Vector2();
        if (forwardDown) inputVector.x += 1.0f;
        if (backwardDown) inputVector.x -= 1.0f;
        if (leftDown) inputVector.y += 1.0f;
        if (rightDown) inputVector.y -= 1.0f;
        inputVector.rotateRad(camR);
        inputVector.nor();

        camX += inputVector.x*MOVE_SPEED*dt;
        camY += inputVector.y*MOVE_SPEED*dt;

        if (inputVector.len2() > 0.001)
            viewHasChanged = true;

        //Rotate player + look up and down
        float prev_camR = camR, prev_camV = camV;

        if (Gdx.input.isCursorCatched()) {
            camR -= InputRecorder.mouseDeltaX * MOUSE_SENS_X;
            camV -= InputRecorder.mouseDeltaY * MOUSE_SENS_Y;
        }
        if (turnLeftDown) camR += TURN_SPEED*dt;
        if (turnRightDown) camR -= TURN_SPEED*dt;
        if (lookUpDown) camV += VLOOK_SPEED*dt;
        if (lookDownDown) camV -= VLOOK_SPEED*dt;
        camV = Math.min( Math.max(camV, -VLOOK_CLAMP), VLOOK_CLAMP );

        if (prev_camV != camV || prev_camR != camR)
            viewHasChanged = true;

        if (viewHasChanged) {
            updateMapView();
            updateRenderView();
        }
    }
}
