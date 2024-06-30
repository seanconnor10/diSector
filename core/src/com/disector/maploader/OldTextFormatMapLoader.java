package com.disector.maploader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import com.disector.Sector;
import com.disector.Wall;
import com.disector.gameworld.GameWorld;

import java.util.Locale;
import java.util.Scanner;

public class OldTextFormatMapLoader implements MapLoader{
    private Array<Sector> sectors;
    private Array<Wall> walls;
    private GameWorld world;

    public OldTextFormatMapLoader(Array<Sector> sectors, Array<Wall> walls, GameWorld world) {
        this.sectors = sectors;
        this.walls = walls;
        this.world = world;
    }

    @Override
    public boolean load(String path) {
        FileHandle file = Gdx.files.local(path);
        Scanner scanner = new Scanner( file.readString() );

        sectors.clear();
        walls.clear();

        //READ SECTORS
        int numSectors = Integer.parseInt( scanner.next() );
        scanner.next(); //Skip "SECTORS"

        scanner.useDelimiter(",");

        for(int i=0; i<numSectors; i++) {
            Sector s = new Sector();
            s.floorZ = Integer.parseInt( scanner.next().trim() );
            s.ceilZ = Integer.parseInt( scanner.next().trim() );
            s.lightFloor = Float.parseFloat( scanner.next().trim() );
            s.lightCeil = Float.parseFloat( scanner.next().trim() );
            /*s.floorTexIndex / Integer.parseInt(*/ scanner.next();//.trim();
            /*s.ceilTexIndex = Integer.parseInt(*/ scanner.next();//.trim();

            String next = scanner.next().trim();
            while(!next.equals("END")) {
                s.walls.add( Integer.parseInt(next.trim() ));
                next = scanner.next().trim();
            }

            sectors.add(s);
        }

        //READ WALLS
        int numWalls = Integer.parseInt( scanner.next().trim() );
        scanner.next(); //Skip "WALLS"

        for(int i=0; i<numWalls; i++) {
            int x1 = Integer.parseInt( scanner.next().trim() );
            int y1 = Integer.parseInt( scanner.next().trim() );
            int x2 = Integer.parseInt( scanner.next().trim() );
            int y2 = Integer.parseInt( scanner.next().trim() );
            String isPortalInput = scanner.next().trim();
            boolean isPortal = Boolean.parseBoolean(isPortalInput);
            /*Color c = new Color( Color.valueOf(*/ scanner.next().trim() /* )) */ ;
            float light = Float.parseFloat(scanner.next().trim());
            int linkA = Integer.parseInt( scanner.next().trim() );
            int linkB = Integer.parseInt( scanner.next().trim() );
            /* int texIndex = Integer.parseInt(*/ scanner.next().trim() /* ) */ ;
            /* int texXOffset = Integer.parseInt(*/ scanner.next().trim() /* ) */ ;
            /* float texXScale = Float.parseFloat(*/ scanner.next().trim() /* ) */ ;
            /*int texYOffset = Integer.parseInt(*/ scanner.next().trim() /* ) */ ;
            /*float texYScale = Float.parseFloat(*/ scanner.next().trim() /* ) */ ;


            Wall w = new Wall(x1, y1, x2, y2);
            w.isPortal = isPortal;
            w.linkA = linkA;
            w.linkB = linkB;
            w.light = light;
            w.setNormalAngle();
            walls.add(w);
        }

        return true;
    }

    @Override
    public boolean save(String path) {
        return false;
    }
}
