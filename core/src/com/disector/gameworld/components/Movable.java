package com.disector.gameworld.components;

import com.badlogic.gdx.math.Vector2;

public interface Movable {

    /************ :D ********************
     * Interface to return velocity components
     * for GameWorld to act upon
     ************************* D: ******/

    Vector2 getVelocity();
    float getZSpeed();
}
