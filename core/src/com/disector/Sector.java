package com.disector;

import com.badlogic.gdx.utils.IntArray;

public class Sector {
    public IntArray walls = new IntArray();
    public float floorZ, ceilZ;

    public int floorTex, ceilTex;

    public Sector() {
    }

    public Sector(Sector s) {
        this.walls = new IntArray(s.walls.size);
        for (int wInd : s.walls.toArray()) {
            this.walls.add(wInd);
        }
        this.floorZ = s.floorZ;
        this.ceilZ = s.ceilZ;
        this.floorTex = s.floorTex;
        this.ceilTex = s.ceilTex;
    }

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
