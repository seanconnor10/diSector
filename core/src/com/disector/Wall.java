package com.disector;

import com.badlogic.gdx.math.Vector2;

public class Wall {
    public float x1, y1, x2, y2;
    public boolean isPortal;
    public int linkA, linkB;
    public float normalAngle; //Angle of line protruding outward perpendicularly from wall into the sector
    public int tex, texUpper, texLower;

    public Wall() {

    }

    public Wall(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        setNormalAngle();
    }

    public Wall(Wall w) {
        this.x1 = w.x1;
        this.y1 = w.y1;
        this.x2 = w.x2;
        this.y2 = w.y2;
        this.isPortal = w.isPortal;
        this.linkA = w.linkA;
        this.linkB = w.linkB;
        this.tex = w.tex;
        this.texUpper = w.texUpper;
        this.texLower = w.texLower;
        //this.normalAngle = w.normalAngle;
        setNormalAngle();
    }

    public float length() {
        if (x1 == x2)
            return Math.abs(y2-y1); //(y2 > y1) ? y2-y1 : y1-y2;
        else if (y1 == y2)
            return Math.abs(x2-x1); //(x2 > x1) ? x2-x1 : x1-x2;

        return (float) Math.sqrt( (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) );
    }

    public void setNormalAngle() {
        if (x1 == x2) {
            normalAngle = (y2>y1) ? 0.f : (float) Math.PI;
            return;
        }
        if (y1 == y2) {
            normalAngle = (x2>x1) ? (float) Math.PI*1.5f : (float) Math.PI*0.5f;
            return;
        }
        normalAngle = (float) -Math.atan2(x2-x1, y2-y1);
    }

    public Vector2 findNearestTo(Vector2 point) {
        //Project Vector that is the playerPosition Relative to the wall origin onto the vector that
        // is the Wall's point2 relative to its point1
        // ... DotProduct divided by length of the wall (squared for some reason?) To not take sqrt of numerator..
        float projection = ( (point.x-x1)*(x2-x1) + (point.y-y1)*(y2-y1) ) / (float) Math.pow( length(), 2);
        projection = Math.min(0.99f, Math.max(0.01f, projection));
        return new Vector2(x1 + ((x2-x1)*projection), y1 + ( (y2-y1)*projection));
    }

    public Vector2 findNearestTo_OLD(Vector2 point) {
        //Project Vector that is the playerPosition Relative to the wall origin onto the vector that is the Wall... DotProduct divided by length of the wall(squared?)
        float projection =
                ( (point.x-x1)*(x2-x1) + (point.y-y1)*(y2-y1) )
                        /
                        (float) Math.pow( length(), 2);

        float tempX = x1 + ( (x2-x1)*projection);
        float tempY = y1 + ( (y2-y1)*projection);

        if (x2>x1) {
            if (tempX > x2) tempX = x2;
            if (tempX < x1) tempX = x1;
        } else {
            if (tempX > x1) tempX = x1;
            if (tempX < x2) tempX = x2;
        }

        if (y2>y1) {
            if (tempY > y2) tempY = y2;
            if (tempY < y1) tempY = y1;
        } else {
            if (tempY > y1) tempY = y1;
            if (tempY < y2) tempY = y2;
        }

        //We want to ensure the nearest point is never on the absolute end of the wall
        //so that two touching walls do not get ordered by distance as if they are the same.
        //Otherwise, we draw out of order and get pushed through protruding corners
        if (tempX == x1 && tempY == y1) {
            tempX += 2.0f*(float)Math.cos( normalAngle+(Math.PI/2.0) );
            tempY += 2.0f*(float)Math.sin( normalAngle+(Math.PI/2.0) );
        } else if (tempX == x2 && tempY == y2) {
            tempX -= 2.0f*(float)Math.cos( normalAngle+(Math.PI/2.0) );
            tempY -= 2.0f*(float)Math.sin( normalAngle+(Math.PI/2.0) );
        }

        return new Vector2(tempX, tempY);
    }

}
