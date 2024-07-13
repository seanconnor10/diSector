package com.disector.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import com.disector.Application;
import com.disector.Sector;
import com.disector.Wall;
import com.disector.assets.Material;
import com.disector.maploader.MapLoader;
import com.disector.maploader.OldTextFormatMapLoader;
import com.disector.maploader.TextFileMapLoader;
import com.disector.renderer.SoftwareRenderer;

public class Editor {
    final Application app;
    final Array<Wall> walls;
    final Array<Sector> sectors;
    final Array<Material> materials;
    final ShapeRenderer shape;
    final SpriteBatch batch;

    private final NewEditorMapRenderer mapRenderer;
    private final SoftwareRenderer viewRenderer;

    static BitmapFont font = new BitmapFont( Gdx.files.local("assets/font/fira.fnt") );

    private static final int MENU_BAR_HEIGHT = 32;
    private final Panel mapPanel = new Panel();
    private final Panel viewPanel = new Panel();
    private final Panel menuPanel = new Panel();
    private final Panel propertiesPanel = new Panel();

    private final Panel[] panels = new Panel[] {
            mapPanel, viewPanel, menuPanel, propertiesPanel
    };

    private Panel focusedPanel = mapPanel;

    private Layouts layout = Layouts.DEFAULT;

    private float mouseX, mouseY;
    private int width, height;

    private boolean shouldUpdateViewRenderer;

    public Editor(Application app) {
        this.app = app;
        this.walls = app.walls;
        this.sectors = app.sectors;
        this.materials = app.materials;
        this.shape = app.shape;
        this.batch = app.batch;
        this.mapRenderer = new NewEditorMapRenderer(app, mapPanel.rect);
        this.viewRenderer = new SoftwareRenderer(app);
        this.viewRenderer.placeCamera(100, 30, -(float)Math.PI/4f);
        this.viewRenderer.camZ = 20;
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        makeButtons();
        for (Panel panel : panels)
            panel.rearrangeButtons();
    }

    private void makeButtons() {
        Button loadButton = new Button(menuPanel, "LOAD");
        Button saveButton = new Button(menuPanel, "SAVE");
        menuPanel.buttons.add(saveButton);
        menuPanel.buttons.add(loadButton);
    }

    // -----------------------------------------------

    public void step(float dt) {
        updateMouse();

        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            focusedPanel = getPanelUnderMouse();
        }

        shouldUpdateViewRenderer = false;

        temporaryControls(dt);

        if (shouldUpdateViewRenderer /* Map Changed or Camera Moved */ )
            viewRenderer.renderWorld();

        mapRenderer.render();
    }

    public void draw() {
        //Draw Backgrounds of Panels and Buttons
        shape.begin(ShapeRenderer.ShapeType.Filled);
        Color col = new Color(0x100508FF);
        for (Panel panel : panels) {
            col = col.mul(1.5f);
            shape.setColor(col);
            drawRect(panel.rect);
            for (Button button : panel.buttons) {
                shape.setColor(Color.TEAL);
                drawRect(offsetRectBy(button.rect, panel.rect));
            }
        }
        shape.end();

        batch.begin();
        font.setColor(Color.GREEN);
        //Draw Button Text
        for (Panel panel : panels) {
            for (Button button : panel.buttons) {
                font.draw(batch, button.text, button.rect.x + panel.rect.x+4, button.rect.y + panel.rect.y + font.getCapHeight()+4);
            }
        }

        //Draw Renderers' frames
        TextureRegion viewTex = viewRenderer.copyPixels();;
        drawFrameBuffer(mapPanel.rect, new TextureRegion(mapRenderer.frame.getColorBufferTexture()));
        drawFrameBuffer(viewPanel.rect, viewTex);

        batch.end();
        viewTex.getTexture().dispose();

        //Draw Borders
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(Color.CORAL);
        for (Panel panel : panels) {
            shape.rect(panel.rect.x+1, panel.rect.y, panel.rect.width-1, panel.rect.height-1);
        }
        shape.end();
    }

    // -----------------------------------------------

    private void updateMouse() {
        mouseX = Gdx.input.getX();
        mouseY = Gdx.input.getY();
    }

    private Panel getPanelUnderMouse() {
        for (Panel p : panels) {
            if (mouseIn(p)) {
                return p;
            }
        }
        return focusedPanel;
    }

    // -----------------------------------------------

    private boolean mouseIn(Panel p) {
        return /*p != null &&*/ p.rect.contains(mouseX, height-mouseY);
    }

    private boolean mouseIn(Button b) {
        return /*b != null && b.panelRect != null &&*/ new Rectangle(
                b.panelRect.x + b.rect.x,
                b.panelRect.y + b.rect.y,
                b.rect.width,
                b.rect.height
        ).contains(mouseX, height-mouseY);
    }

    // -----------------------------------------------

    public void resize(int w, int h) {
        width = w;
        height = h;

        switch (layout) {
            case SWAP:      resizeSWAP();       break;
            case RENDER:    resizeRENDER();     break;
            case MAP:       resizeMAP();        break;
            default:        resizeDEFAULT();    break;
        }

        mapRenderer.refreshPanelSize(mapPanel.rect);
        viewRenderer.resizeFrame(Math.round(viewPanel.rect.width), Math.round(viewPanel.rect.height));
        mapRenderer.render();
        viewRenderer.renderWorld();
    }

    private void resizeDEFAULT() {
        menuPanel.rect.height = MENU_BAR_HEIGHT;
        menuPanel.rect.width = width;
        menuPanel.rect.x = 0;
        menuPanel.rect.y = height-menuPanel.rect.height;

        mapPanel.rect.height = height-menuPanel.rect.height;
        mapPanel.rect.width = (int) (width*0.6f);
        mapPanel.rect.x = width-mapPanel.rect.width;
        mapPanel.rect.y = 0;

        viewPanel.rect.height = (int) ( (height-menuPanel.rect.height)*0.5f);
        viewPanel.rect.width = mapPanel.rect.x;
        viewPanel.rect.x = 0;
        viewPanel.rect.y = viewPanel.rect.height;

        propertiesPanel.rect.height = viewPanel.rect.y;
        propertiesPanel.rect.width = mapPanel.rect.x;
        propertiesPanel.rect.x = 0;
        propertiesPanel.rect.y = 0;
    }

    private void resizeSWAP() {

    }

    private void resizeRENDER() {

    }

    private void resizeMAP() {

    }

    // -----------------------------------------------

    private void drawRect(Rectangle rect) {
        shape.rect(rect.x, rect.y, rect.width, rect.height);
    }

    private void drawFrameBuffer(Rectangle rect, TextureRegion tex) {
        tex.flip(false, true);
        batch.draw(tex, rect.x, rect.y);
    }

    private Rectangle offsetRectBy(Rectangle rect, Rectangle offset) {
        return new Rectangle(offset.x + rect.x, offset.y+rect.y, rect.width, rect.height);
    }

    // -----------------------------------------------

    private void temporaryControls(float dt) {
        if (focusedPanel == mapPanel)
            moveMapWithKeyBoard(dt);
        else if (focusedPanel == viewPanel)
            moveViewWithKeyBoard(dt);

        //Temporary Load and Save
        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                MapLoader mapLoader = new OldTextFormatMapLoader(app);
                mapLoader.load("MAPS/SHED");
                shouldUpdateViewRenderer = true;
            } else {
                //MapLoader mapLoader = new TextFileMapLoader(app);
                //mapLoader.load("MAPS/test.txt");
                app.loadMap("MAPS/test.txt");
                shouldUpdateViewRenderer = true;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            MapLoader mapLoader = new TextFileMapLoader(app);
            mapLoader.save("MAPS/test.txt");
        }

        //Temporary Toggle FullBright
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            viewRenderer.fullBright = !viewRenderer.fullBright;
            shouldUpdateViewRenderer = true;
        }

        //GridSize
        if (Gdx.input.isKeyJustPressed(Input.Keys.G))
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                mapRenderer.gridSize /= 2;
            else
                mapRenderer.gridSize *= 2;
        if (mapRenderer.gridSize < 4)
            mapRenderer.gridSize = 4;

    }

    private void moveMapWithKeyBoard(float dt) {
        //Temporary Movement
        float mapZoom = mapRenderer.zoom;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            mapRenderer.camY += 200.f * dt / (float)Math.sqrt(mapZoom);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            mapRenderer.camX += 200.f * dt / (float)Math.sqrt(mapZoom);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            mapRenderer.camY -= 200.f * dt / (float)Math.sqrt(mapZoom);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            mapRenderer.camX -= 200.f * dt / (float)Math.sqrt(mapZoom);
        }

        //Temporary Zoom
        if (Gdx.input.isKeyJustPressed(Input.Keys.EQUALS)) mapZoom += mapZoom < 1 ? 0.1f : mapZoom < 4 ? 0.5f : 2;
        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) mapZoom -= mapZoom < 1 ? 0.1f : mapZoom < 4 ? 0.5f : 2;
        if (mapZoom < 0.1f) mapZoom = 0.1f;
        if (mapZoom > 20) mapZoom = 20;

        mapRenderer.zoom = mapZoom;
    }

    private void moveViewWithKeyBoard(float dt) {
        //Temporary Movement
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            viewRenderer.camVLook += 200 * dt;
            shouldUpdateViewRenderer = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            viewRenderer.camVLook -= 200 * dt;
            shouldUpdateViewRenderer = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            viewRenderer.camR += 2*dt;
            shouldUpdateViewRenderer = true;
        }
         if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            viewRenderer.camR -= 2*dt;
            shouldUpdateViewRenderer = true;
        }
         
        //Temporary Zoom
        if (Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
            viewRenderer.camFOV *= 1 + dt;
            shouldUpdateViewRenderer = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            viewRenderer.camFOV *= 1 - Math.min(1, dt);
            shouldUpdateViewRenderer = true;
        }
        if (viewRenderer.camFOV < 50) viewRenderer.camFOV = 50;
        if (viewRenderer.camFOV > 1000) viewRenderer.camFOV = 1000;

    }

}