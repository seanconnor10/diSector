package com.disector.gameworld;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.IntArray;

import com.disector.App;
import com.disector.Sector;
import com.disector.Wall;
import com.disector.gameworld.components.Movable;


public class GameWorld {
    private final App app;

    private Player player1;

    public GameWorld(App app) {
        this.app = app;
        player1 = new Player(this);
        player1.z = 100.f;
    }

    public void step(float dt) {
        Vector2 playerLastPosition = player1.movementInput(dt);
        player1.verticalMovement(dt, app.walls, app.sectors);
    }

    public Vector4 getPlayerPosition() {
        return new Vector4(player1.x, player1.y, player1.z+player1.height, player1.r);
    }

    public float getPlayerVLook() {
        return player1.vLook;
    }

    private void moveObj(Movable obj) {
        Vector3 prevPosition = obj.getPos();
        Vector2 velocity = obj.getVelocity();
        boolean[] checkedSectors = new boolean[app.sectors.size]; //Actually only init if we've gone through portal

        Sector currentSector = app.sectors.get( obj.getCurrentSector() );
        IntArray currentSectorWalls = new IntArray();
        for (int wInd : currentSector.walls.toArray()) {
            currentSectorWalls.add(wInd);
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

}
