package com.disector.maploader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import com.disector.Sector;
import com.disector.Wall;
import com.disector.gameworld.GameWorld;

public class TextFileMapLoader implements MapLoader{
    private Array<Sector> sectors;
    private Array<Wall> walls;
    private GameWorld world;

    public TextFileMapLoader(Array<Sector> sectors, Array<Wall> walls, GameWorld world) {
        this.sectors = sectors;
        this.walls = walls;
        this.world = world;
    }

    @Override
    public boolean load(String path) {
        return false;
    }

    @Override
    public boolean save(String path) {
        FileHandle file = Gdx.files.local(path);

        file.writeString("", false); //Clear File

        StringBuilder allSectorsString = new StringBuilder();
        for (Sector s : sectors) {
            allSectorsString.append(sectorToText(s));
        }
        file.writeString(allSectorsString.toString(), true);

        StringBuilder allWallsString = new StringBuilder();
        for (Wall w : walls) {
            allWallsString.append(wallToText(w));
        }
        file.writeString(allWallsString.toString(), true);

        return true;
    }

    private String sectorToText(Sector s) {
        StringBuilder str = new StringBuilder("SECTOR :: ");

        //Wall Indices
        for (int wInd : s.walls.toArray()) {
            str.append(wInd).append(" ");
        }
        str.append(":: ");

        //Height
        str.append("HEIGHT ").append(s.floorZ).append(" ").append(s.ceilZ).append(" ");

        str.append("\n");

        return str.toString();
    }

    private String wallToText(Wall w) {
        StringBuilder str = new StringBuilder("WALL :: ");

        str.append( String.format("POS %f %f %f %f :: ", w.x1, w.y1, w.x2, w.y2) );

        if (w.isPortal) {
            str.append( String.format("PORT %d -> %d :: ", w.linkA, w.linkB));
        }

        str.append("\n");

        return str.toString();
    }
}
