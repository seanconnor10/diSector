package com.disector.editor;

import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.utils.IntArray;
import com.disector.Sector;
import com.disector.Wall;

import java.util.Arrays;

class EditorActiveSelection {
    private final Array<Wall> allWalls;
    private final Array<Sector> allSectors;

    private final IntArray sectorIndices;
    private final Array<Sector> selectedSectors;
    private final IntArray wallIndices;
    private final Array<Wall> selectedWalls;

    private int highlightedSectorIndex;
    private Sector highlightedSector;
    private int highlightedWallIndex;
    private Wall highlightedWall;

    EditorActiveSelection(Array<Sector> sectors, Array<Wall> walls) {
        selectedSectors = new Array<>();
        selectedWalls = new Array<>();
        sectorIndices = new IntArray();
        wallIndices = new IntArray();
        allWalls = walls;
        allSectors = sectors;
    }

    void clear() {
        clearWalls();
        clearSectors();
    }

    void clearWalls() {
        selectedWalls.clear();
        wallIndices.clear();
    }

    void clearSectors() {
        sectorIndices.clear();
        selectedSectors.clear();
    }

    Array<Wall> getWalls() {
        return new Array<Wall>(selectedWalls);
    }

    Array<Sector> getSectors() {
        return new Array<Sector>(selectedSectors);
    }

    int getWallHighlightIndex() {
        return highlightedWallIndex;
    }

    Wall getWallHighlight() {
        return highlightedWall;
    }

    int getSectorHighlightIndex() {
        return highlightedSectorIndex;
    }

    Sector getSectorHighlight() {
        return highlightedSector;
    }

    void setHighlights(int mouseWorldX, int mouseWorldY) {
        
    }

    void setWallHighlight(int wallIndex) {
        if (wallIndex >= allWalls.size)
            return;
        if (wallIndex < 0) {
            highlightedWallIndex = -1;
            highlightedWall = null;
        } else {
            highlightedWallIndex = wallIndex;
            highlightedWall = allWalls.get(wallIndex);
        }
    }

    void setSectorHighlight(int sectorIndex) {
        if (sectorIndex >= allSectors.size)
            return;
        if (sectorIndex < 0) {
            highlightedSectorIndex = -1;
            highlightedSector = null;
        } else {
            highlightedSectorIndex = sectorIndex;
            highlightedSector = allSectors.get(sectorIndex);
        }
    }

    void addHighlightedWallToSelection() {

    }
}
