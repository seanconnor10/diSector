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
import com.disector.renderer.EditorMapRenderer;
import com.disector.renderer.SoftwareRenderer;

public class OldEditor {
    private Application app;

    private RenderViewPanel viewPanel;
    private MenuBarPanel menuPanel;

    private DimensionalRenderer _3DRenderer; //Remove after refactor
    private EditorMapRenderer topDownRenderer; //Remove after refactor

    public Rectangle renderWindPos = new Rectangle(); //Remove after refactor
    public Rectangle mapViewPos = new Rectangle(); //Remove after refactor

    private float camX, camY, camZ = 30, camR, camV;

    final float MOVE_SPEED = 100.f;
    final float MOUSE_SENS_X = 0.002f, MOUSE_SENS_Y = 0.5f;
    final float TURN_SPEED = 3.0f, VLOOK_SPEED = 200.0f;
    final float VLOOK_CLAMP = 300.f;

    public OldEditor(Application app) {
        this.app = app;
        _3DRenderer = new SoftwareRenderer(app);
        topDownRenderer = new EditorMapRenderer(app, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        renderWindPos.set(0, 0, 800, 450);
        mapViewPos.set(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void step(float dt) {
        cameraControls(dt);
    }

    public void resize(int width, int height) {
        mapViewPos.set(renderWindPos.getWidth(), 0, width-renderWindPos.getWidth(), Gdx.graphics.getHeight());
        if (topDownRenderer != null) topDownRenderer.changeSize((int)mapViewPos.width, (int)mapViewPos.height);
        updateRenderView();
        updateMapView();
    }

    public void draw(SpriteBatch batch) {
        TextureRegion renderView = getRenderView();
        TextureRegion mapView = getMapView();

        batch.begin();
        ScreenUtils.clear(Color.GRAY);
        if (topDownRenderer != null) batch.draw(mapView, mapViewPos.x, Gdx.graphics.getHeight()-mapViewPos.height-mapViewPos.y, mapViewPos.width, mapViewPos.height);
        if (_3DRenderer != null) batch.draw(renderView, 0, Gdx.graphics.getHeight()-renderWindPos.height-renderWindPos.y, renderWindPos.width, renderWindPos.height);
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
        if (topDownRenderer == null) return;
        topDownRenderer.placeCamera(new Vector4(camX, camY, camZ, camR), camV, 0);
        topDownRenderer.renderWorld();
    }

    private TextureRegion getMapView() {
        if (topDownRenderer == null) return null;
        TextureRegion mapView;
        mapView = topDownRenderer.copyPixels();
        mapView.flip(true, false);
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
