package com.disector;

import com.badlogic.gdx.math.Vector2;

public class Wall {
    public float x1, y1, x2, y2;
    public boolean isPortal;
    public int linkA, linkB;
    public float normalAngle; //Angle of line protruding outward perpendicularly from wall into the sector

    public Wall(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        setNormalAngle();
    }

    public float length() {
        if (x1 == x2)
            return (y2 > y1) ? y2-y1 : y1-y2;
        else if (y1 == y2)
            return (x2 > x1) ? x2-x1 : x1-x2;

        return (float) Math.sqrt( (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) );
    }

    public void setNormalAngle() {
        float nX1, nY1, nX2, nY2;
        nX1 = (x1+x2)/2.0f; nY1 = (y1+y2)/2.0f;
        normalAngle = (float) -Math.atan2(x2-x1, y2-y1);
        nX2 = nX1 + 5.0f * (float) Math.cos(normalAngle);
        nY2 = nY1 + 5.0f * (float) Math.sin(normalAngle);
    }

    public Vector2 findNearestTo(Vector2 point) {
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
