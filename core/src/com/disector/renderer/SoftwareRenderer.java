package com.disector.renderer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.IntArray;
import com.disector.App;
import com.disector.Sector;
import com.disector.Wall;

import java.util.HashSet;
import java.util.Stack;

public class SoftwareRenderer extends Renderer {
    private IntArray occlusionBottom = new IntArray(app.frameWidth);
    private IntArray occlusionTop = new IntArray(app.frameWidth);
    private Stack<Integer> drawnPortals = new Stack<>();
    private HashSet<Integer> transformedWalls = new HashSet<>();
    private HashSet<Integer> transformedSectors = new HashSet<>();

    public SoftwareRenderer(App app) {
        super(app);
    }

    @Override
    public void renderWorld() {
        //Clear drawing Data
        transformedWalls.clear();
        transformedSectors.clear();
        drawnPortals.clear();
    }

    @Override
    public void drawFrame() {
        batch.begin();
        TextureRegion frame = new TextureRegion(new Texture((buffer)), buffer.getWidth(), buffer.getHeight());
        frame.flip(false, true);
        batch.draw(frame, 0, 0);
        batch.end();
        frame.getTexture().dispose();
    }

    @Override
    public void resizeFrame(int w, int h) {
        super.resizeFrame(w, h);
        occlusionBottom.setSize(w);
        occlusionTop.setSize(w);
    }

    private void drawSector(int secInd, int spanStart, int spanEnd) {
        Sector sec = sectors.get(secInd);
    }

    private void drawWall(int wInd, int currentSectorIndex, int spanStart, int spanEnd) {
        Wall w = walls.get(wInd);

        float x1, y1, x2, y2; //Transform wall points relative to camera and store on stack
        x1 = w.x1 - camX;
        y1 = w.y1 - camY;
        x2 = w.x2 - camX;
        y2 = w.y2 - camY;
        float playerCos = (float) Math.cos(-camR) , playerSin = (float) Math.sin(-camR);
        float tempX = x1;
        x1 = x1 * playerCos - y1 * playerSin;
        y1 = y1 * playerCos + tempX * playerSin;
        tempX = x2;
        x2 = x2 * playerCos - y2 * playerSin;
        y2 = y2 * playerCos + tempX * playerSin;

        if (x1 < 0.f && x2 < 0.f) return; //Avoid drawing if totally behind camera

        float leftClipU = 0.f, rightClipU = 1.f;
        float wallLength = w.length();

        if (x1 < 0) { //If on-screen-left edge of wall is on camera, clip wall to point at edge of frame
            float slope = (y2-y1) / (x2-x1);
            float yAxisIntersect = y1 - slope*x1;
            leftClipU = (float) Math.sqrt( x1*x1 + (yAxisIntersect-y1)*(yAxisIntersect-y1) ) / wallLength;
            x1 = 0.f;
            y1 = yAxisIntersect;
        }

        if (x2 < 0) { //Now for right edge of wall and right edge of frame
            float slope = (y2-y1) / (x2-x1);
            float yAxisIntersect = y1 - slope*x1;
            rightClipU = 1.f - (float) ( Math.sqrt( x2*x2 + (yAxisIntersect-y2)*(yAxisIntersect-y2) ) / wallLength );
            x2 = 0.f;
            y2 = yAxisIntersect;
        }

        if (x1 < 0.01f) x1 = 0.01f; //Avoid future division by very small values
        if (x2 < 0.01f) x2 = 0.01f;

        float fov = camFOV;

        float p1_plotX = halfWidth - fov*y1/x1; //Plot edges of wall onto screen space
        float p2_plotX = halfWidth - fov*y2/x2;

        if (p1_plotX > p2_plotX) return; //Avoid drawing backside of wall

        int leftEdgeX = (int) p1_plotX; //Snap plots to integer representing pixel column
        if (leftEdgeX < 0)
            leftEdgeX = 0;

        int rightEdgeX = (int) p2_plotX;
        if (rightEdgeX > frameWidth-1)
            rightEdgeX = frameWidth-1;

        if (leftEdgeX > spanEnd) return; //Avoid more processing if out of span
        if (rightEdgeX < spanStart) return;

        //if ( spanFilled(spanStart, spanEnd) ) return;


    }
}
