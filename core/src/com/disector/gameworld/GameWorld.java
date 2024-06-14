package com.disector.gameworld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.utils.IntArray;
import com.disector.App;
import com.disector.Sector;
import com.disector.Wall;
import com.disector.WallInfoPack;
import com.disector.gameworld.components.Movable;
import com.disector.gameworld.components.Positionable;

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

        player1.movementInput(dt);
        player1.verticalMovement(dt, walls, sectors);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            player1.currentSectorIndex++;
            if (player1.currentSectorIndex == sectors.size)
                player1.currentSectorIndex = 0;
        }

        moveObj(player1);
    }

    //*****************************************************

    public Vector4 getPlayerPosition() {
        return new Vector4(player1.copyPosition(), player1.z+player1.height, player1.r);
    }

    public int getPlayerSectorIndex() {
        return player1.currentSectorIndex;
    }

    public float getPlayerVLook() {
        return player1.vLook;
    }

    //*****************************************************

    private void moveObj(Movable obj) {
        /**
         * Takes any game object with a position and velocity and moves it,
         * colliding it against walls and updating its currentSectorIndex
         */
        Sector currentSector = sectors.get( obj.getCurrentSector() );

        Vector2 objPos = obj.snagPosition(); //Snag grabs a reference to the Vector so we can change it
        Vector2 velocity = obj.snagVelocity();

        objPos.x += velocity.x * dt;
        objPos.y += velocity.y * dt;

        float stepUpAllowance = obj.isOnGround() && obj.getZSpeed() >= 0.0f ? 10.f : 0.f;

        Array<WallInfoPack> wallsCollided;
        IntArray potentialNewSectors = new IntArray();
        float teeterHeight = currentSector.floorZ;
        float lowestCeilHeight = currentSector.ceilZ;
        int collisionsProcessed = 0;

        while (collisionsProcessed < 100) {
            wallsCollided = findCollisions(currentSector, obj);

            //Remove Portal Walls that we can vertically fit through,
            //putting the destination sector index into another array
            for (int i=0; i<wallsCollided.size; i++) {
                WallInfoPack wallInfo = wallsCollided.get(i);
                if (wallInfo.w.isPortal) {
                    int destSector = wallInfo.w.linkA;
                    if (destSector == obj.getCurrentSector())
                        destSector = wallInfo.w.linkB;
                    Sector dest = sectors.get(destSector);
                    if (heightCheck(dest, obj, stepUpAllowance)) {
                        teeterHeight = Math.max(teeterHeight, dest.floorZ);
                        lowestCeilHeight = Math.min(lowestCeilHeight, dest.ceilZ);
                        potentialNewSectors.add(destSector);
                        wallsCollided.removeIndex(i);
                        i--;
                    }
                }
            }

            for (int sInd : potentialNewSectors.toArray()) {
                if (containsPoint( sectors.get(sInd), objPos.x, objPos.y)) {
                    obj.setCurrentSector(sInd);
                    break;
                }
            }

            if (wallsCollided.isEmpty()) break;

            wallsCollided.sort( ////Sort collided walls to get the first one we should collide with
                (WallInfoPack o1, WallInfoPack o2) -> Float.compare(o1.distToNearest, o2.distToNearest)
            );

            //Get reference to closest collision
            WallInfoPack closestCollision = wallsCollided.get(0);

            resolveCollision(closestCollision, obj);
            velocity.set(bounceVector(velocity, closestCollision.w));

            collisionsProcessed++;

        }

        obj.setOnGround(obj.getZ() < teeterHeight+0.5f);

        //Grav
        if (obj.getZ() > teeterHeight) obj.setZSpeed(obj.getZSpeed() - 200.f*dt);
        if (obj.getZSpeed() < -100.0f) obj.setZSpeed(-100.0f);
        //Enact motion
        obj.setZ( obj.getZ() + obj.getZSpeed()*dt );
        //Hit Floor
        if (obj.getZ()<teeterHeight) {
            obj.setZ(teeterHeight);
            if (obj.getZSpeed() < 0) obj.setZSpeed(0);
        }
        //HitCeiling
        if (obj.getZ()+obj.getHeight()>lowestCeilHeight) {
            obj.setZ(lowestCeilHeight-obj.getHeight());
            if (obj.getZSpeed() > 0) obj.setZSpeed(0);
        }

    }

    private boolean heightCheck(Sector s, Positionable obj, float stepUpAllowance) {
        //Return whether the obj can fit the sector height-wise
        return obj.getZ()+obj.getHeight() < s.ceilZ && obj.getZ() >= s.floorZ-stepUpAllowance;
    }

    private Array<WallInfoPack> findCollisions(Sector sector, Positionable obj) {
        Array<WallInfoPack> collisions = new Array<>();
        Vector2 objPos = obj.copyPosition();
        //For every wall in sector, check collision by bounding box
        //If collided, check collision accurately
        //and if still colliding, add to list of collisions
        for (int wInd : sector.walls.toArray()) {
            Wall w = walls.get(wInd);
            if (boundingBoxCheck(w, obj.copyPosition(), obj.getRadius())) {
                WallInfoPack info = new WallInfoPack(w, wInd, objPos);
                if (info.distToNearest < obj.getRadius()) {
                    collisions.add(info);
                }
            }
        }
        return collisions;
    }

    private void resolveCollision (WallInfoPack collisionInfo, Movable obj) {
        float resolutionDistance = obj.getRadius() - collisionInfo.distToNearest;
        if (collisionInfo.w.isPortal && collisionInfo.w.linkA == obj.getCurrentSector())
            resolutionDistance *= -1;
        Vector2 objPos = obj.snagPosition();
        objPos.x += (float) Math.cos(collisionInfo.w.normalAngle) * resolutionDistance;
        objPos.y += (float) Math.sin(collisionInfo.w.normalAngle) * resolutionDistance;
    }

    private Vector2 bounceVector(Vector2 velocity, Wall wall) {
        float wallXNormal = (float) Math.cos(wall.normalAngle);
        float wallYNormal = (float) Math.sin(wall.normalAngle);
        float proj_norm = velocity.x * wallXNormal + velocity.y * wallYNormal;
        float perpendicularVelX = proj_norm * wallXNormal;
        float perpendicularVelY = proj_norm * wallYNormal;
        float parallelVelX = velocity.x - perpendicularVelX;
        float parallelVelY = velocity.y - perpendicularVelY;
        float elasticity = 0.2f;
        float restitution = 0.9f;
        return new Vector2(
            parallelVelX * restitution - perpendicularVelX * elasticity,
            parallelVelY * restitution - perpendicularVelY * elasticity
        );
    }

    private boolean boundingBoxCheck(Wall w, Vector2 objPos, float objRadius) {
        float leftBound = Math.min(w.x1, w.x2) - objRadius;
        float rightBound = Math.max(w.x1, w.x2) + objRadius;
        float topBound = Math.min(w.y1, w.y2) - objRadius;
        float bottomBound = Math.max(w.y1, w.y2) + objRadius;
        if (objPos.x > rightBound) return false;
        if (objPos.x < leftBound) return false;
        if (objPos.y > bottomBound) return false;
        return !(objPos.y < topBound);
    }

    private boolean isBetween(float var, float bound1, float bound2) {
        if (bound2 > bound1)
            return (var > bound1 && var < bound2);
        return (var > bound2 && var < bound1);
        //return (var > bound1) ^ (var > bound2); //May or may not include bounds
    }

    private Vector2 rayWallIntersection(Wall w, float angle, float rayX, float rayY, boolean allowBehind) {
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

    private boolean containsPoint(Sector sec, float x, float y) {
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

}