package com.disector.gameworld;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Array;

import com.disector.App;
import com.disector.Sector;
import com.disector.Wall;
import com.disector.gameworld.components.Movable;


public class GameWorld {
    private final App app;
    private final Array<Wall> walls;
    private final Array<Sector> sectors;

    private Player player1;

    private float dt;

    public GameWorld(App app) {
        this.app = app;
        this.walls = app.walls;
        this.sectors = app.sectors;
        player1 = new Player(this);
        player1.z = 100.f;
    }

    public void step(float dt) {
        this.dt = dt;
        Vector2 playerLastPosition = player1.movementInput(dt);
        player1.verticalMovement(dt, walls, sectors);

        //boolean colliding = boundingBoxCheck( walls.get(4), player1.getPos(), player1.getRadius() );
        //System.out.println(colliding);
        moveObjSimple(player1);
    }

    public Vector4 getPlayerPosition() {
        return new Vector4(player1.x, player1.y, player1.z+player1.height, player1.r);
    }

    public float getPlayerVLook() {
        return player1.vLook;
    }

    //WIP
    private void moveObj(Movable obj) {
        Vector3 prevPosition = obj.getPos();
        Vector2 velocity = obj.getVelocity();
        boolean[] checkedSectors = new boolean[app.sectors.size]; //Actually only init if we've gone through portal

        Sector currentSector = app.sectors.get( obj.getCurrentSector() );
        Array<WallInfoPack> currentSectorWalls = new Array<>();
        for (int wInd : currentSector.walls.toArray()) {
            //currentSectorWalls.add( new WallInfoPack(walls.get(wInd), wInd) );
        }

        //For each wall in current Sector
            //If within wall's bounding box (with added padding equal to obj's radius)
            //Set wall's Nearest Point relative to obj
            //If there is, add to a list of collisions for this frame
        //Order this list starting with collision nearest to starting point
        //Check and resolve only that collision, resolving away from wall? or back towards prevPosition?

        //Check for collisions again until there are none

        //If colliding with a portal and we fit vertically..
        // add the already checked sector to checkedSectors
    }

    //WIP
    private void moveObjSimple(Movable obj) {
        Vector3 prevPosition = obj.getPos();
        Vector2 velocity = obj.getVelocity();
        Sector currentSector = sectors.get( obj.getCurrentSector() );

        obj.setPos( new Vector3( prevPosition.x + velocity.x*dt, prevPosition.y + velocity.y*dt, prevPosition.z) );

        Array<WallInfoPack> wallsToCheck = new Array<>(); //First Stores what walls collide by BoundingBox

        for (int wInd : currentSector.walls.toArray()) {
            Wall w = walls.get(wInd);
            if (boundingBoxCheck(w, obj.getPos(), obj.getRadius())) {
                //Add the wall that already collided by bounding box
                //to a list to be checked accurately
                WallInfoPack wallInfo = new WallInfoPack(w, wInd, new Vector2(prevPosition.x, prevPosition.y))
                //if (wallInfo)
                //wallsToCheck.add( );
            }
        }

        wallsToCheck.
                /*sort( //Sort by shortest distance to object's starting position
                (WallInfoPack o1, WallInfoPack o2) -> Float.compare(o1.distToNearest, o2.distToNearest)
        );*/

    }

    private boolean boundingBoxCheck(Wall w, Vector3 objPos, float objRadius) {
        Rectangle boundingBox = new Rectangle(
          Math.min(w.x1, w.x2)-objRadius,
          Math.min(w.y1, w.y2)-objRadius,
          Math.max(w.x1, w.x2)+objRadius - (Math.min(w.x1, w.x2)-objRadius),
          Math.max(w.y1, w.y1)+objRadius - (Math.min(w.y1, w.y2)-objRadius)
        );

        if (objPos.x > boundingBox.x+boundingBox.width) return false;
        if (objPos.x < boundingBox.x) return false;
        if (objPos.y > boundingBox.y +boundingBox.height) return false;
        if (objPos.y < boundingBox.y) return false;

        return true;
    }

    private class WallInfoPack {
        private final Wall w;
        private final int wInd;
        private final Vector2 nearestPoint;
        private final float distToNearest;  //WARNING: This is be the actual distance squared
                                            //We spare ourselves the sqrt for performance

        private WallInfoPack(Wall w, int wInd, Vector2 point) {
            this.w = w;
            this.wInd = wInd;
            this.nearestPoint = new Vector2( w.findNearestTo(point) );
            this.distToNearest = (float) Math.abs( Math.pow(nearestPoint.x-point.x, 2) + Math.pow(nearestPoint.y-point.y, 2) );
        }

        @Override
        public String toString() {
            return wInd + ") Dist: " + distToNearest;
        }

    }

}