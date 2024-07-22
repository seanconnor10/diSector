package com.disector.editor;

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Array;

import com.disector.Physics;
import com.disector.Sector;
import com.disector.Wall;

import com.disector.editor.actions.EditAction;

public class STATE_CreatingSector extends EditorState {

    boolean isNewSector = false;
    int sectorIndex;
    Sector sector;
    Array<Wall> createdWalls = new Array<>();
    IntArray createdWallIndices = new IntArray();

    public STATE_CreatingSector(Editor editor, Panel panel) {
        super(editor, panel);
        ignoreEditorClick = true;
        init();
    }

    @Override
    void init() {
        int x = x(), y = y();

        //Find sector clicked in
        sectorIndex = Physics.findCurrentSectorBranching(-1, x, y);

        if (sectorIndex == -1) {
            isNewSector = true;
            sector = new Sector();
            sectorIndex = editor.sectors.size;
            editor.sectors.add(sector);
        } else {
            sector = editor.sectors.get(sectorIndex);
        }

        sector = editor.sectors.get(sectorIndex);

        Wall firstWall = new Wall(x, y, x, y);
        createdWallIndices.add(editor.walls.size);
        createdWalls.add(firstWall);
        sector.walls.add(editor.walls.size);
        editor.app.walls.add(firstWall);
    }

    @Override
    void step() {
        Wall newestWall = createdWalls.get(createdWalls.size-1);
        newestWall.x2 = x();
        newestWall.y2 = y();
        newestWall.setNormalAngle();
    }

    @Override
    void click() {

    }

    @Override
    EditAction[] finish() {
        return new EditAction[0];
    }

    private int x(){
        return ((MapPanel) panel).getMouseWorldX();
    }

    private int y(){
        return ((MapPanel) panel).getMouseWorldY();
    }
}
