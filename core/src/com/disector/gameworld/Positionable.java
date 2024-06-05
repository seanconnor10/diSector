package com.disector.gameworld;

import com.badlogic.gdx.math.Vector3;

public interface Positionable {

    /************ :D ********************
     * Interface to provide info needed for GameWorld/App to locate object
     * and find it's currentSector
     ************************* D: ******/

    Vector3 getPos();
    int getCurrentSector();
    void setCurrentSector(int sInd);

}
