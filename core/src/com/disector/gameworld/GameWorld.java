package com.disector.gameworld;

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

        player1.movementInput(dt);
        player1.verticalMovement(dt, walls, sectors);

        //boolean colliding = boundingBoxCheck( walls.get(1), player1.snagPosition(), player1.getRadius() );
        //System.out.println(colliding);

        moveObj(player1);
    }

    public Vector4 getPlayerPosition() {
        return new Vector4(player1.copyPosition(), player1.z+player1.height, player1.r);
    }

    public float getPlayerVLook() {
        return player1.vLook;
    }

    private void moveObj(Movable obj) {
        /**
         * Takes any game object with a position and velocity and moves it,
         * colliding it against walls and updating its currentSectorIndex
         */
        Vector2 objPos = obj.snagPosition(); //Snag grabs a reference to the Vector so we can change it
        Vector2 velocity = obj.snagVelocity();

        Sector currentSector = sectors.get( obj.getCurrentSector() );

        objPos.x += velocity.x * dt;
        objPos.y += velocity.y * dt;

        Array<WallInfoPack> wallsCollided = new Array<>();

        do {
            wallsCollided.clear();

            //For every wall in sector, check collision by bounding box, if collided check collision accurately
            //and if colliding, add to list of collidedWalls
            for (int wInd : currentSector.walls.toArray()) {
                Wall w = walls.get(wInd);
                if (boundingBoxCheck(w, objPos, obj.getRadius())) {
                    WallInfoPack wallInfo = new WallInfoPack(w, wInd, objPos);
                    if (wallInfo.distToNearest < obj.getRadius())
                        wallsCollided.add(wallInfo);
                }
            }

            if (wallsCollided.isEmpty()) return;

            wallsCollided.sort( //Sort collided walls to get the first one we should collide with
                    (WallInfoPack o1, WallInfoPack o2) -> Float.compare(o1.distToNearest, o2.distToNearest)
            );

            WallInfoPack closestCollision = wallsCollided.get(0); //Get reference to closest collision

            //Resolve Collision
            float resolutionDistance = obj.getRadius() - closestCollision.distToNearest;
            objPos.x += (float) Math.cos(closestCollision.w.normalAngle) * resolutionDistance;
            objPos.y += (float) Math.sin(closestCollision.w.normalAngle) * resolutionDistance;

            //Bounce off of wall
            float wallXNormal = (float) Math.cos(closestCollision.w.normalAngle);
            float wallYNormal = (float) Math.sin(closestCollision.w.normalAngle);
            float proj_norm = velocity.x * wallXNormal + velocity.y * wallYNormal;
            float perpendicularVelX = proj_norm * wallXNormal;
            float perpendicularVelY = proj_norm * wallYNormal;
            float parallelVelX = velocity.x - perpendicularVelX;
            float parallelVelY = velocity.y - perpendicularVelY;
            final float elasticity = 0.2f;
            final float restitution = 0.9f;
            velocity.x = ( parallelVelX * restitution - perpendicularVelX * elasticity);
            velocity.y = ( parallelVelY * restitution - perpendicularVelY * elasticity);

        } while (!wallsCollided.isEmpty());

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

    private static class WallInfoPack {
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