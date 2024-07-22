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
    float firstWallX, firstWallY;

    public STATE_CreatingSector(Editor editor, Panel panel) {
        super(editor, panel);
        ignoreEditorClick = true;
        init();
    }

    void init() {
        setSector();
        makeNextWall();
        firstWallX = createdWalls.get(0).x1;
        firstWallY = createdWalls.get(0).y1;
    }

    @Override
    void step() {
        if (shouldFinish) return;

        Wall newestWall = createdWalls.get(createdWalls.size-1);
        int x = x(), y = y();
        if (newestWall.x2 != x || newestWall.y2 != y) {
            newestWall.x2 = x();
            newestWall.y2 = y();
            editor.shouldUpdateViewRenderer = true;
        }
        newestWall.setNormalAngle();
    }

    @Override
    void click() {
        if (shouldFinish) return;

        //If Clicking On Beginning of first wall, finish
        if ( Math.abs(x() - firstWallX) < 0.05f && Math.abs(y() - firstWallY) < 0.05f ) {
            shouldFinish = true;
            return;
        }

        makeNextWall();
    }

    @Override
    void rightClick() {
        if (shouldFinish) return;

        int i = createdWallIndices.size-1;
        int wallIndex = createdWallIndices.get(i);
        createdWallIndices.removeIndex(i);
        createdWalls.removeIndex(i);
        sector.walls.removeValue(wallIndex);
        editor.app.walls.removeIndex(wallIndex);

        if (createdWalls.isEmpty())
            shouldFinish = true;
    }

    @Override
    EditAction[] finish() {
        return new EditAction[0];
    }

    private int x(){
        int x = ((MapPanel) panel).getMouseWorldX();
        if (editor.isGridSnapping) x = editor.snap(x);
        return x;
    }

    private int y(){
        int y = ((MapPanel) panel).getMouseWorldY();
        if (editor.isGridSnapping) y = editor.snap(y);
        return y;
    }

    private void makeNextWall() {
        int x = x(), y = y();
        Wall newWall = new Wall( x, y, x, y);
        int newIndex = editor.app.walls.size;
        createdWallIndices.add(newIndex);
        createdWalls.add(newWall);
        sector.walls.add(newIndex);
        editor.app.walls.add(newWall);
    }

    private void setSector() {
        sectorIndex = Physics.findCurrentSectorBranching(-1, x(), y());
        if (sectorIndex == -1) {
            isNewSector = true;
            sectorIndex = editor.sectors.size;
            editor.sectors.add(new Sector());
        }
        sector = editor.sectors.get(sectorIndex);
    }
}
