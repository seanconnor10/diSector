package com.disector.editor;

import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.utils.IntArray;
import com.disector.Sector;
import com.disector.Wall;

class EditorActiveSelection {
    private final Array<Wall>

    private final IntArray sectorIndices;
    private final Array<Sector> selectedSectors;
    private final IntArray wallIndices;
    private final Array<Wall> selectedWalls;

    private int highlightedSectorIndex;
    private Sector highlightedSector;
    private int highlightedWallIndex;
    private Wall highlightedWall;

    EditorActiveSelection() {
        sectors = new Array<>();
        walls = new Array<>();
        sectorIndices = new IntArray();
        wallIndices = new IntArray();
    }

    void clear() {
        clearWalls();
        clearSectors();
    }

    void clearWalls() {
        walls.clear();
        wallIndices.clear();
    }

    void clearSectors() {
        sectorIndices.clear();
        sectors.clear();
    }

    void setWallHighlight(int wInd) {

    }

    void addHighlightedWallToSelection() {

    }
}
