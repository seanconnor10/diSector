package com.disector.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;

public class Console {
    private final SpriteBatch batch;
    private final ShapeRenderer shape;
    private final Matrix4 renderTransform;

    private boolean visible;
    private boolean active;
    private float y;
    private final float lineHeight;
    private int lineScroll;
    private final float screenPercentage = 0.65f;
    private final int xBorder = 42;

    private String currentIn;
    private final Array<String> textLines;

    private InputAdapter inputAdapter;
    private CommandExecutor executor;

    BitmapFont font = new BitmapFont( Gdx.files.local("assets/font/fira.fnt") );
    private Color backgroundColor;

    public Console(CommandExecutor executor) {
        active = false;
        visible = false;
        y=Gdx.graphics.getHeight();
        lineHeight = font.getLineHeight()+8;
        lineScroll = 0;

        backgroundColor = new Color(0.05f, 0.f, 0.10f, 0.7f);

        batch = new SpriteBatch();
        shape = new ShapeRenderer();
        renderTransform = new Matrix4();

        textLines = new Array<>(25);
        currentIn = "";
        textLines.add("    -= welcome =-");

        this.executor = executor;

        createInputProcessor();
    }

    public Matrix4 getRenderTransform() {
        return renderTransform;
    }

    public void insertText(String str) {
        textLines.insert(0, str);
    }

    public InputAdapter getInputAdapter() {
        return inputAdapter;
    }

    private void createInputProcessor() {
        inputAdapter = new InputAdapter() {
            @Override
            public boolean keyTyped (char character) {
                if (active &&
                        character != '\b' &&
                        character != '\n' &&
                        character != '\t' &&
                        character != '\r' &&
                        character != '\f' &&
                        character != '\\' &&
                        character != '`' &&
                        character != '~' )
                    currentIn += character;

                if (character == '\b') { //Backspace
                    currentIn = currentIn.substring(0, Math.max( 0, (currentIn+"MARK!MARK%MARK?").indexOf("MARK!MARK%MARK?")-1) );
                }

                return true;
            }

            @Override
            public boolean keyDown (int keycode) {
                if (active) {
                    switch(keycode) {
                        case Input.Keys.FORWARD_DEL: //'Delete' Key
                            currentIn = ""; //Bug fix to actually do this in update
                            break;
                        case Input.Keys.ENTER:
                            lineScroll = 0;
                            processInput();
                            break;
                        case Input.Keys.PAGE_DOWN:
                            lineScroll = Math.max(0, lineScroll - (int) (Gdx.graphics.getHeight()*screenPercentage/lineHeight) );
                            break;
                        case Input.Keys.PAGE_UP:
                            lineScroll = Math.min(
                                    (textLines.size+2) - (int) (screenPercentage*Gdx.graphics.getHeight()/lineHeight),
                                    lineScroll + (int) (Gdx.graphics.getHeight()*screenPercentage/lineHeight) );
                            break;
                        case Input.Keys.END:
                            lineScroll = 0;
                            break;
                        case Input.Keys.HOME:
                            lineScroll = (textLines.size+2) - (int) (screenPercentage*Gdx.graphics.getHeight()/lineHeight);
                        default:
                            break;
                    }
                }

                return true;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                lineScroll -= Math.round(amountY);
                if (lineScroll < 0) lineScroll = 0;

                float maxUp = (float)(textLines.size+3) - screenPercentage*Gdx.graphics.getHeight()/lineHeight;
                maxUp = Math.max(0.0f, maxUp);
                if (lineScroll > (int) maxUp) lineScroll = (int) maxUp;

                return true;
            }
        };
    }

    public void updateAndDraw(float delta) {
        //Fix bug where commands don't register currently after delete was pressed ?
        if (active && Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL) ) {
            currentIn = "";
        }

        //Toggle Active
        if ( Gdx.input.isKeyJustPressed(Input.Keys.GRAVE)) {
            active = !active;
        }

        //Slide console up or down
        float yGoal = Gdx.graphics.getHeight() * (1.0f - screenPercentage);
        if (active) {
            visible = true;
            if (y != yGoal)
                y =  Math.max(y-1500.0f*delta, yGoal);
        } else {
            if (y != Gdx.graphics.getHeight())
                y = Math.min(y+1500.0f*delta, Gdx.graphics.getHeight());
            if ( Math.round(y) == Gdx.graphics.getHeight())
                visible = false;
        }

        //Draw when not hidden
        if (active || y != Gdx.graphics.getHeight()) {
            draw();
        }
    }

    public void updateTransform() {
        renderTransform.setToOrtho2D(0.0f,0.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
    }

    public void setBackgroundColor(float r, float g, float b) {
        backgroundColor.set(r,g,b, backgroundColor.a);
    }

    private void processInput() {
        insertText(">> " + currentIn);

        String response = executor.execute(currentIn);

        currentIn = "";

        if (response == null)
            return;

        String[] responses = response.split("\n");

        for (String line : responses)
            insertText(line);
    }

    private void draw() {
        if (!visible) return;

        updateTransform();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shape.setProjectionMatrix(renderTransform);
        //Green Back
        shape.setColor(backgroundColor);
        shape.rect(0.0f,  y, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()-y );
        //Entry Field
        shape.rect(0.0f,  y, Gdx.graphics.getWidth(), lineHeight );
        //Thin Line
        shape.setColor(1.0f, 1.0f, 0.9f, 0.5f);
        shape.rect(0.0f,  y-1, Gdx.graphics.getWidth(), 1 );
        shape.end();

        batch.begin();
        batch.setProjectionMatrix(renderTransform);
        font.setColor(Color.WHITE);

        //Draw Fps
        font.draw(batch, "FPS: " + String.valueOf((int)(1.0f/Gdx.graphics.getDeltaTime()) ), Gdx.graphics.getWidth()-175.0f, y+lineHeight-10.0f);

        //Draw Console Input
        font.draw(batch, currentIn, xBorder, y - 5 + lineHeight);

        //Draw Console Log
        for (int i = lineScroll; i < textLines.size; i++) {
            font.draw(batch, textLines.get(i), xBorder, y + (i+2-lineScroll)*lineHeight);
        }
        batch.end();
    }

}
