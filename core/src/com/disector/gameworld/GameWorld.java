package com.disector.gameworld;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.utils.IntArray;
import com.disector.*;
import com.disector.gameworld.components.Movable;
import com.disector.gameworld.components.Positionable;
import com.disector.inputrecorder.InputRecorder;

import static com.disector.Physics.containsPoint;

public class GameWorld {
    private final Application app;
    private final Array<Wall> walls;
    private final Array<Sector> sectors;

    private Player player1;

    private float dt;
    private boolean shouldDisplayMap;

    public GameWorld(Application app) {
        this.app = app;
        this.walls = app.walls;
        this.sectors = app.sectors;
        player1 = new Player(this);
        player1.z = 100.f;
    }

    public void step(float dt) {
        this.dt = dt;

        if (InputRecorder.getKeyInfo("DISPLAY_MAP").justPressed)
            shouldDisplayMap = !shouldDisplayMap;

        player1.movementInput(dt);
        moveObj(player1);
    }

    public void drawMap() {
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

    public boolean shouldDisplayMap() {
        return shouldDisplayMap;
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
                if (containsPoint( sectors.get(sInd), objPos.x, objPos.y, walls)) {
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
        final float elasticity = 0.2f;
        final float restitution = 0.9f;
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




}