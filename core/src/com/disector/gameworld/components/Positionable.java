package com.disector.gameworld.components;

import com.badlogic.gdx.math.Vector3;

public interface Positionable {

    /************ :D ********************
     * Interface to provide info needed for GameWorld/App to locate object
     * and find it's currentSector
     ************************* D: ******/

    Vector3 getPos();
    float getHeight();
    float getRadius();
    int getCurrentSector();
    void setCurrentSector(int sInd);

}
