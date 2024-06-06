package com.disector.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.disector.App;
import com.disector.Sector;
import com.disector.Wall;

import java.util.HashSet;
import java.util.Stack;

public class SoftwareRenderer extends Renderer {
    private int[] occlusionBottom;
    private int[] occlusionTop;
    private Stack<Integer> drawnPortals = new Stack<>();
    private HashSet<Integer> transformedWalls = new HashSet<>();
    private HashSet<Integer> transformedSectors = new HashSet<>();

    public SoftwareRenderer(App app) {
        super(app);
    }

    @Override
    public void renderWorld() {
        resetDrawData();
        buffer.fill();
        drawSector(0, 0, frameWidth-1);
    }

    @Override
    public void drawFrame() {
        TextureRegion frame = new TextureRegion(new Texture((buffer)), buffer.getWidth(), buffer.getHeight());
        frame.flip(false, true);
        batch.begin();
        batch.draw(frame, 0, 0);
        batch.end();
        frame.getTexture().dispose();
    }

    @Override
    public void resizeFrame(int w, int h) {
        super.resizeFrame(w, h);
        reInitDrawData(w);
    }

    private void reInitDrawData(int newFrameWidth) {
        occlusionBottom = new int[newFrameWidth];
        occlusionTop = new int[newFrameWidth];
    }

    private void resetDrawData() {
        transformedWalls.clear();
        transformedSectors.clear();
        drawnPortals.clear();

        for (int i=0; i<frameWidth; i++) {
            occlusionBottom[i] = 0;
            occlusionTop[i] = frameHeight;
        }
    }

    private boolean spanFilled(int spanStart, int spanEnd) {
        for (int i=spanStart; i<spanEnd; i++) {
            if (occlusionBottom[i] < occlusionTop[i]-1)
                return false;
        }
        return true;
    }

    private void drawSector(int secInd, int spanStart, int spanEnd) {
        Sector sec = sectors.get(secInd);

        for (int wInd : sec.walls.toArray()) {
            drawWall(wInd, camCurrentSector, spanStart, spanEnd);
        }
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
        if ( spanFilled(spanStart, spanEnd)) return;

        Sector currentSector = sectors.get(currentSectorIndex);
        float secFloorZ = currentSector.floorZ, secCeilZ = currentSector.ceilZ;

        float p1_plotLow = halfHeight - camVLook + fov*(secFloorZ-camZ)/x1; //Plot wall points vertically
        float p1_plotHigh = halfHeight - camVLook + fov*(secCeilZ-camZ)/x1;
        float p2_plotLow = halfHeight - camVLook + fov*(secFloorZ-camZ)/x2;
        float p2_plotHigh = halfHeight - camVLook + fov*(secCeilZ-camZ)/x2;

        float hProgress; //Horizontal per-pixel progress for this wall
        float quadBottom, quadTop, quadHeight; //Stores the top and bottom of the wall for each pixel column
        int rasterBottom, rasterTop; //Where to stop and start drawing for this pixel column

                                //SHOULD PROBABLY BE <= rightEdgeX
        for (int drawX = leftEdgeX; drawX < rightEdgeX; drawX++) { //Per draw column loop
            if (occlusionTop[drawX] -1 <= occlusionBottom[drawX] ) continue;

            hProgress = (drawX-p1_plotX) / (p2_plotX-p1_plotX);

            if (hProgress<0) hProgress = 0.0f;
            if (hProgress>1.0) hProgress = 1.0f;

            quadBottom = p1_plotLow + hProgress*(p2_plotLow-p1_plotLow);
            quadTop = p1_plotHigh + hProgress*(p2_plotHigh-p1_plotHigh);
            quadHeight = quadTop - quadBottom;

            float fog = (x1 + hProgress*(x2-x1)) / 600.0f;

            rasterBottom = Math.max( (int) quadBottom, occlusionBottom[drawX]);
            rasterTop = Math.min( (int) quadTop, occlusionTop[drawX]);

            float u =  ((1 - hProgress)*(leftClipU/x1) + hProgress*(rightClipU/x2)) / ( (1-hProgress)*(1/x1) + hProgress*(1/ x2));
            if (u<0.0001f) u = 0.0001f; if (u>0.9999) u = 0.9999f;

            for (int drawY = rasterBottom; drawY < rasterTop; drawY++) { //Per Pixel draw loop
                float v = (drawY - quadBottom) /quadHeight;
                boolean checkerboardColor = ( (int)(u*8)%2 == (int)(v*8)%2 );
                Color pixelColor = new Color( checkerboardColor ? 0xFFA0BB00 : 0xFF00A0BB);
                pixelColor.lerp(0f,0f,0f,1f,fog);
                buffer.drawPixel(drawX, drawY, pixelColor.toIntBits() );
            } //End Per Pixel Loop

            //Draw Floor
            final float scaleFactor = 32.f;
            float floorXOffset = camX/scaleFactor, floorYOffset = camY/scaleFactor;
            int vOffset = (int) camVLook;
            if (occlusionBottom[drawX] < quadBottom) {
                float heightOffset = (camZ - secFloorZ) / scaleFactor;
                int floorEndScreenY = (int)(quadBottom);
                for (int drawY = occlusionBottom[drawX] + vOffset; drawY<floorEndScreenY + vOffset; drawY++) {
                    float floorX = heightOffset * (drawX-halfWidth) / (drawY-halfHeight);
                    float floorY = heightOffset * fov / (drawY-halfHeight);

                    float rotFloorX = floorX*playerSin - floorY*playerCos + floorXOffset;
                    float rotFloorY = floorX*playerCos + floorY*playerSin + floorYOffset;

                    if (rotFloorX<=0) rotFloorX = -rotFloorX;
                    if (rotFloorY<0) rotFloorY = -rotFloorY;

                    rotFloorX /= 4;
                    rotFloorY /= 4;

                    rotFloorX = rotFloorX%1;
                    rotFloorY = rotFloorY%1;

                    while(rotFloorX<0.0) rotFloorX+=1.0f;
                    while(rotFloorX>1.0f) rotFloorX-=1.0f;
                    while(rotFloorY<0.0) rotFloorY+=1.0f;
                    while(rotFloorY>1.0f) rotFloorY-=1.0f;

                    boolean checkerBoard = ( (int)(rotFloorX*8%2) == (int)(rotFloorY*8%2) );
                    Color drawColor = new Color( checkerBoard ? 0xFFD08010 : 0xFF10D080 );
                    float floorFogValue = 1.f - ((halfHeight-heightOffset-drawY)/(halfHeight-heightOffset));
                    Math.clamp(floorFogValue, 0.f, 1.f);
                    drawColor.lerp( Color.BLACK, floorFogValue);
                    buffer.drawPixel(drawX, drawY - vOffset, drawColor.toIntBits() );
                }
            }
            if (occlusionTop[drawX] > quadTop) {
                float heightOffset = (secCeilZ-camZ) / scaleFactor;
                int ceilEndScreenY = occlusionTop[drawX] + vOffset;
                for (int drawY = rasterTop + vOffset; drawY < ceilEndScreenY; drawY++) {
                    float ceilX = heightOffset * (drawX - halfWidth) / (drawY - halfHeight);
                    float ceilY = heightOffset * fov / (drawY - halfHeight);

                    float rotX = ceilX * playerSin - ceilY * playerCos - floorXOffset;
                    float rotY = ceilX * playerCos + ceilY * playerSin - floorYOffset;

                    if (rotX <= 0) rotX = -rotX;
                    if (rotY < 0) rotY = -rotY;

                    rotX /= 4f;
                    rotY /= 4f;

                    rotX = rotX % 1;
                    rotY = rotY % 1;

                    while (rotX < 0.0) rotX += 1.0f;
                    while (rotX > 1.0f) rotX -= 1.0f;
                    while (rotY < 0.0) rotY += 1.0f;
                    while (rotY > 1.0f) rotY -= 1.0f;

                    boolean checkerBoard = ( (int)(rotX*8%2) == (int)(rotY*8%2) );
                    Color drawColor = new Color( checkerBoard ? 0xFF302005 : 0xFF251503 );
                    float ceilFogValue = 1.0f - (((-halfHeight + drawY) / halfHeight));
                    Math.clamp(ceilFogValue, 0.f, 1.f);
                    drawColor.lerp( Color.BLACK, ceilFogValue);
                    buffer.drawPixel(drawX, drawY - vOffset, drawColor.toIntBits() );
                }
            }

            //Update Occlusion Matrix
            int meetingPoint = (rasterTop + rasterBottom) / 2;
            if (occlusionBottom[drawX] < meetingPoint)
                occlusionBottom[drawX] = meetingPoint;
            if (occlusionTop[drawX] > meetingPoint)
                occlusionTop[drawX] = meetingPoint;

        } //End Per Column Loop
    }
}
