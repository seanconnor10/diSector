package com.disector;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Physics {

    public static boolean containsPoint(Sector sec, float x, float y, Array<Wall> walls) {
        /**
         * For each wall in Sector, casts a ray, if an odd number of walls were contacted,
         * the point is within the sector
         */
        int intersections = 0;
        for (Integer wInd : sec.walls.toArray()) {
            Vector2 intersect = rayWallIntersection(walls.get(wInd), 0.f, x, y, false);
            if (intersect != null && intersect.x > x)
                intersections++;
        }

        return (intersections%2 == 1);
    }

    public static Vector2 rayWallIntersection(Wall w, float angle, float rayX, float rayY, boolean allowBehind) {
        /*
         * Returns the position of an intersection between a ray and a Wall
         * If allowBehind is enabled, the ray is cast backwards as well
         */
        float raySlope = (float) ( Math.sin(angle) / Math.cos(angle) );

        //If wall is horizontal
        if (w.y1 == w.y2) {
            if (!allowBehind) {
                if (Math.sin(angle) > 0 && rayY > w.y1) return null;
                if (Math.sin(angle) < 0 && rayY < w.y1) return null;
            }
            float deltaY = w.y1-rayY;
            float intersectX = rayX + deltaY/raySlope;
            if (!isBetween(intersectX, w.x1, w.x2)) return null;
            return new Vector2(intersectX, w.y1);
        }

        //Is wall is vertical
        if (w.x1 == w.x2) {
            if (!allowBehind) {
                if (Math.cos(angle) > 0.0 && rayX > w.x1) return null;
                if (Math.cos(angle) < 0.0 && rayY < w.x1) return null;
            }

            float deltaX = w.x1-rayX;
            float intersectY = rayY + deltaX*raySlope;
            if (!isBetween(intersectY, w.y1,w.y2)) return null;
            return new Vector2(w.x1, intersectY);
        }

        //If wall is neither vertical nor horizontal
        // "Given two line equations" Method from Wikipedia
        //ax+c=bx+d // a=wallSlope c=wallIntercept b=raySlope d=rayIntercept // iX = (d-c)/(a-b)
        float wallSlope = (w.y2-w.y1) / (w.x2-w.x1);
        float wallYIntercept = w.y1 - wallSlope*w.x1;
        float rayYIntercept = rayY - raySlope*rayX;
        float iX = (rayYIntercept-wallYIntercept) / (wallSlope-raySlope);
        float iY = (wallSlope*iX) + wallYIntercept;

        if ( ! (isBetween(iX,w.x1, w.x2) && isBetween(iY,w.y1,w.y2)) ) return null;

        //Check if actually behind
        if (!allowBehind) {
            if (Math.sin(angle) > 0.0 && iY < rayY) return null;
            if (Math.sin(angle) < 0.0 && iY > rayY) return null;
        }

        return new Vector2(iX,iY);

    }

    public static boolean isBetween(float var, float bound1, float bound2) {
        if (bound2 > bound1)
            return (var > bound1 && var < bound2);
        return (var > bound2 && var < bound1);
        //return (var > bound1) ^ (var > bound2); //May or may not include bounds
    }

}
