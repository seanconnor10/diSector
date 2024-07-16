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

import com.disector.AppFocusTarget;
import com.disector.Application;
import com.disector.Sector;
import com.disector.Wall;
import com.disector.assets.Material;
import com.disector.editor.actions.EditAction;
import com.disector.maploader.MapLoader;
import com.disector.maploader.OldTextFormatMapLoader;
import com.disector.maploader.TextFileMapLoader;
import com.disector.renderer.SoftwareRenderer;
import sun.reflect.generics.tree.VoidDescriptor;

import java.util.Stack;

public class Editor {
    static BitmapFont font = new BitmapFont( Gdx.files.local("assets/font/fira.fnt") );

    final Application app;
    final Array<Wall> walls;
    final Array<Sector> sectors;
    final Array<Material> materials;
    final ShapeRenderer shape;
    final SpriteBatch batch;

    private final NewEditorMapRenderer mapRenderer;
    private final SoftwareRenderer viewRenderer;

    private final Stack<EditAction> undoStack = new Stack<>();

    private final int max3DViewWidth = 400;
    private final int max3DViewHeight = 225;
    private static final int MENU_BAR_HEIGHT = 32;

    private final Panel mapPanel = new Panel();
    private final Panel viewPanel = new Panel();
    private final Panel menuPanel = new Panel();
    private final Panel propertiesPanel = new Panel();


    private final EditorMessageLog messageLog = new EditorMessageLog();
    private Panel logPanel = mapPanel;

    private Panel focusedPanel = mapPanel;
    private final Panel[] panels = new Panel[] {
            mapPanel, viewPanel, menuPanel, propertiesPanel
    };

    private Button clickedButton = null;

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

        Button loadButton = new Button(this, menuPanel, "LOAD");
        loadButton.releaseAction = (Void) -> {
             if (app.loadMap("MAPS/test.txt") )
                 messageLog.log("Loaded from MAPS/test.txt");
             else
                 messageLog.log("FAILED TO LOAD MAPS/test.txt");
             return Void;
        };
        menuPanel.buttons.add(loadButton);


        Button saveButton = new Button(this, menuPanel, "SAVE");
        saveButton.releaseAction = (Void) -> {
            new TextFileMapLoader(app).save("MAPS/test.txt");
            messageLog.log("Saved to MAPS/test.txt");
            return Void;
        };
        menuPanel.buttons.add(saveButton);


        Button playButton = new Button(this, menuPanel, "PLAY");
        playButton.releaseAction = (Void) -> {
            app.swapFocus(AppFocusTarget.GAME);
            return Void;
        };
        menuPanel.buttons.add(playButton);

    }

    // -----------------------------------------------

    public void step(float dt) {
        messageLog.stepLifeTime(dt);

        updateMouse();

        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT))
            focusedPanel = getPanelUnderMouse();

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT))
            onMouseClick();
        else if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT) && clickedButton != null)
            onMouseRelease();

        temporaryControls(dt);

        if (shouldUpdateViewRenderer)
            viewRenderer.renderWorld();

        mapRenderer.render();

        shouldUpdateViewRenderer = false;
    }

    public void forceViewRefresh() {
        shouldUpdateViewRenderer = true;
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
                shape.setColor(button.pressed ? Color.MAROON : Color.TEAL);
                drawRect(offsetRectBy(button.rect, panel.rect));
            }
        }

        //Switch from shapeRenderer to SpriteBatch
        shape.end();
        batch.begin();


        //Draw Renderers' frames
        TextureRegion viewTex = viewRenderer.copyPixels();;
        drawFrameBuffer(mapPanel.rect, new TextureRegion(mapRenderer.frame.getColorBufferTexture()));
        drawFrameBuffer(viewPanel.rect, viewTex);

        //Draw Button Text
        for (Panel panel : panels) {
            for (Button button : panel.buttons) {
                font.setColor(button.pressed ? Color.YELLOW : Color.GREEN);
                font.draw(batch, button.text, button.rect.x + panel.rect.x+4, button.rect.y + panel.rect.y + font.getCapHeight()+4);
            }
        }

        //DrawLogText
        drawLog();

        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) messageLog.log("NEW!!S");

        //End Batch and clear frame Textures
        batch.end();
        viewTex.getTexture().dispose();

        //Draw Borders
        shape.setColor(Color.CORAL);
        shape.begin(ShapeRenderer.ShapeType.Line);
        for (Panel panel : panels) {
            shape.rect(panel.rect.x+1, panel.rect.y, panel.rect.width-1, panel.rect.height-1);
        }

        if (focusedPanel != null) {
            shape.setColor(Color.PINK);
            shape.rect(focusedPanel.rect.x+1, focusedPanel.rect.y, focusedPanel.rect.width-1, focusedPanel.rect.height-1);
        }

        shape.end();
    }

    // -----------------------------------------------

    private void onMouseClick() {
        Panel panelClicked = getPanelUnderMouse();

        for (Button b : panelClicked.buttons) {
            if (mouseIn(b)) {
                clickedButton = b;
                b.pressed = true;
                break;
            }
        }

    }

    private void onMouseRelease() {
        clickedButton.pressed = false;

        if (mouseIn(clickedButton)) {
            clickedButton.releaseAction.apply(null);
            /*switch(clickedButton.text.toLowerCase()) {
                case "load":
                    if (app.loadMap("MAPS/test.txt") )
                        messageLog.log("Loaded from MAPS/test.txt");
                    else
                        messageLog.log("FAILED TO LOAD MAPS/test.txt");
                    break;
                case "save":
                    new TextFileMapLoader(app).save("MAPS/test.txt");
                    messageLog.log("Saved to MAPS/test.txt");
                    break;
                case "play":
                    app.swapFocus(AppFocusTarget.GAME);
                    break;
                default:
                    break;
            }*/
        }

        clickedButton = null;
    }

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

        int view3DviewWidth = Math.min( max3DViewWidth, Math.round(viewPanel.rect.width));
        int view3DviewHeight = Math.min( max3DViewHeight, Math.round(viewPanel.rect.height));

        mapRenderer.refreshPanelSize(mapPanel.rect);
        viewRenderer.resizeFrame(view3DviewWidth, view3DviewHeight);
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

        logPanel = mapPanel;
    }

    private void resizeSWAP() {
        menuPanel.rect.height = MENU_BAR_HEIGHT;
        menuPanel.rect.width = width;
        menuPanel.rect.x = 0;
        menuPanel.rect.y = height-menuPanel.rect.height;

        viewPanel.rect.height = height-menuPanel.rect.height;
        viewPanel.rect.width = (int) (width*0.6f);
        viewPanel.rect.x = width-viewPanel.rect.width;
        viewPanel.rect.y = 0;

        mapPanel.rect.height = (int) ( (height-menuPanel.rect.height)*0.5f);
        mapPanel.rect.width = viewPanel.rect.x;
        mapPanel.rect.x = 0;
        mapPanel.rect.y = mapPanel.rect.height;

        propertiesPanel.rect.height = mapPanel.rect.y;
        propertiesPanel.rect.width = viewPanel.rect.x;
        propertiesPanel.rect.x = 0;
        propertiesPanel.rect.y = 0;

        logPanel = viewPanel;
    }

    private void resizeRENDER() {
        menuPanel.rect.height = MENU_BAR_HEIGHT;
        menuPanel.rect.width = width;
        menuPanel.rect.x = 0;
        menuPanel.rect.y = height-menuPanel.rect.height;

        viewPanel.rect.x = 0;
        viewPanel.rect.y = 0;
        viewPanel.rect.width = width;
        viewPanel.rect.height = height - MENU_BAR_HEIGHT;

        propertiesPanel.rect.set(-1,-1,-1,-1);
        mapPanel.rect.set(-1,-1,-1,-1);

        logPanel = viewPanel;
    }

    private void resizeMAP() {
        menuPanel.rect.height = MENU_BAR_HEIGHT;
        menuPanel.rect.width = width;
        menuPanel.rect.x = 0;
        menuPanel.rect.y = height-menuPanel.rect.height;

        mapPanel.rect.height = height-menuPanel.rect.height;
        mapPanel.rect.width = (int) (width*0.75f);
        mapPanel.rect.x = width-mapPanel.rect.width;
        mapPanel.rect.y = 0;

        propertiesPanel.rect.height = mapPanel.rect.height;
        propertiesPanel.rect.width = mapPanel.rect.x;
        propertiesPanel.rect.x = 0;
        propertiesPanel.rect.y = 0;

        viewPanel.rect.set(-1, -1, -1, -1);

        logPanel = mapPanel;
    }

    private void cycleLayout() {
        int nextLayoutOrdinal = layout.ordinal() + 1;
        if (nextLayoutOrdinal == Layouts.values().length)
            nextLayoutOrdinal = 0;
        layout = Layouts.values()[nextLayoutOrdinal];
        System.out.println("LAYOUT: " + layout.toString());
        resize(width, height);
    }

    // -----------------------------------------------

    private void drawRect(Rectangle rect) {
        shape.rect(rect.x, rect.y, rect.width, rect.height);
    }

    private void drawFrameBuffer(Rectangle rect, TextureRegion tex) {
        tex.flip(false, true);
        batch.draw(tex, rect.x, rect.y, rect.width, rect.height);
    }

    private void drawLog() {
        int logSize = messageLog.messages.size();
        int lineSpace = (int) font.getLineHeight();

        for (int i=logSize-1; i>=0; i--) {
            font.setColor(Color.GOLDENROD);
            font.getColor().lerp(Color.CLEAR, (logSize-1-i) * (1.f/(logSize+1)) );
            //font.getColor().lerp(Color.CLEAR, messageLog.messageLifetime.get(i) / EditorMessageLog.MAX_LIFE );
            font.draw(batch, messageLog.messages.get(i),
                    logPanel.rect.x+12,
                    logPanel.rect.y+logPanel.rect.height-12-lineSpace*(logSize-1-i)
            );
        }
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.L) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            MapLoader mapLoader = new OldTextFormatMapLoader(app);
            mapLoader.load("MAPS/SHED");
            shouldUpdateViewRenderer = true;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            cycleLayout();
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