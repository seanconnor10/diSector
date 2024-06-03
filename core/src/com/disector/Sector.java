package com.disector;

import com.badlogic.gdx.utils.IntArray;

public class Sector {
    public IntArray walls = new IntArray();
    public float floorZ, ceilZ;

    public void addWallSafely(int wInd) {
        if (!walls.contains(wInd))
            walls.add(wInd);
    }

    public int[] getWallIndices() {
        return walls.toArray();
    }

    public void removeDuplicateIndices() {
        if (walls.size<2) return;
        walls.sort();
        int thisValue = walls.items[0];
        for (int i=1; i<walls.size; i++) {
            if (walls.items[i] == thisValue) {
                walls.removeIndex(i);
                i--; // :)
            } else {
                thisValue = walls.items[i];
            }
        }
    }

}
