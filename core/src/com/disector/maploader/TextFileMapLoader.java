package com.disector.maploader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;

import com.disector.Sector;
import com.disector.Wall;
import com.disector.assets.Material;
import com.disector.assets.PixmapContainer;
import com.disector.editor.MapViewPanel;
import com.disector.gameworld.GameWorld;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;
import java.util.PrimitiveIterator;
import java.util.Scanner;

public class TextFileMapLoader implements MapLoader {
    private Array<Sector> sectors;
    private Array<Wall> walls;
    private GameWorld world;
    private PixmapContainer pixmapContainer;
    private Array<Material> materials;

    public TextFileMapLoader(Array<Sector> sectors, Array<Wall> walls, GameWorld world, PixmapContainer pixmapContainer, Array<Material> materials) {
        this.sectors = sectors;
        this.walls = walls;
        this.world = world;
        this.pixmapContainer = pixmapContainer;
        this.materials = materials;
    }

    @Override
    public boolean load(String path) {
        String mode = "NONE"; //"SECTOR" "WALL" "OBJECT" "MATERIAL"
        String subMode = "NONE";

        FileHandle file = Gdx.files.local(path);
        Scanner in = new Scanner(file.readString());
        String next = "";

        Array<Wall> newWalls = new Array<>();
        Array<Sector> newSectors = new Array<>();
        Array<Material> newMaterials = new Array<>();

        Sector sectorBuild = null;
        Wall wallBuild = null;
        Material materialBuild = null;

        while (in.hasNext()) {
            next = in.next().trim().toUpperCase();
            if (isObjectKeyword(next)) {
                //Finalize Previous Mode
                switch (mode) {
                    case "SECTOR":
                        newSectors.add(sectorBuild);
                        sectorBuild = null;
                        break;
                    case "WALL":
                        newWalls.add(wallBuild);
                        wallBuild = null;
                        break;
                    case "MATERIAL":
                        newMaterials.add(materialBuild);
                        materialBuild = null;
                        break;
                    default:
                        break;
                }
                subMode = "NONE";
                mode = next;

                //Init New Mode
                switch (mode) {
                    case "SECTOR":
                        sectorBuild = new Sector();
                        break;
                    case "WALL":
                        wallBuild = new Wall();
                        break;
                    case "MATERIAL":
                        materialBuild = new Material();
                        break;
                    default:
                        break;
                }

            } else { //If not ObjectKeyword...

                switch (mode) {
                    case "NONE":
                        if (!isObjectKeyword(next)) {
                            System.out.printf("Token %s is not Object Keyword\n", next);
                        }
                        break;
                    case "SECTOR":
                        if (isSectorKeyword(next)) {
                            subMode = next;
                        } else if (next.equals("::")) {
                            subMode = "NONE";
                        } else {
                            switch (subMode) {
                                case "HAS":
                                    sectorBuild.walls.add(Integer.parseInt(next));
                                    break;
                                case "MAT":
                                    sectorBuild.matFloor = Integer.parseInt(next);
                                    sectorBuild.matCeil = Integer.parseInt(in.next());
                                    subMode = "NONE";
                                    break;
                                case "HEIGHT":
                                    sectorBuild.floorZ = Float.parseFloat(next);
                                    sectorBuild.ceilZ = Float.parseFloat(in.next());
                                    subMode = "NONE";
                                    break;
                                case "LIGHT":
                                    sectorBuild.lightFloor = Float.parseFloat(next);
                                    sectorBuild.lightCeil = Float.parseFloat(in.next());
                                    subMode = "NONE";
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    case "WALL":
                        if (isWallKeyword(next)) {
                            subMode = next;
                        } else if (next.equals("::")) {
                            subMode = "NONE";
                        } else {
                            switch (subMode) {
                                case "POS":
                                    wallBuild.x1 = Float.parseFloat(next);
                                    wallBuild.y1 = Float.parseFloat(in.next());
                                    wallBuild.x2 = Float.parseFloat(in.next());
                                    wallBuild.y2 = Float.parseFloat(in.next());
                                    subMode = "NONE";
                                    break;
                                case "PORT":
                                    wallBuild.isPortal = true;
                                    wallBuild.linkA = Integer.parseInt(next);
                                    in.next(); //Absorb '->' or whatever is there
                                    wallBuild.linkB = Integer.parseInt(in.next());
                                    subMode = "NONE";
                                    break;
                                case "MAT":
                                    wallBuild.mat = Integer.parseInt(next);
                                    subMode = "NONE";
                                    break;
                                case "UPPERMAT":
                                    wallBuild.matUpper = Integer.parseInt(next);
                                    subMode = "NONE";
                                    break;
                                case "LOWERMAT":
                                    wallBuild.matLower = Integer.parseInt(next);
                                    subMode = "NONE";
                                    break;
                                case "LIGHT":
                                    wallBuild.light = Float.parseFloat(next);
                                    subMode = "NONE";
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    case "MATERIAL":
                        if (isMaterialKeyword(next)) {
                            subMode = next;
                            if (next.equals("SKY")) {
                                materialBuild.isSky = true;
                                subMode = "NONE";
                            }
                        } else if (next.equals("::")) {
                            subMode = "NONE";
                        } else {
                            switch(subMode) {
                                case "IMG":
                                    materialBuild.tex = pixmapContainer.pixmaps2.get(next.toUpperCase());
                                    subMode = "NONE";
                                    break;
                                case "SKY":
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    default:
                        break;
                }

            }
        }

        //Finalize Open Wall or Sector Builds
        if (sectorBuild != null)
            newSectors.add(sectorBuild);
        if (wallBuild != null)
            newWalls.add(wallBuild);
        if (materialBuild != null)
            newMaterials.add(materialBuild);

        //Validate..
        int errors = 0;
        for (Sector s : newSectors) {
            for (int wInd : s.walls.toArray()) {
                if (wInd >= newWalls.size) {
                    errors++;
                    System.out.printf("Wall index %d does not exist\n", wInd);
                }
            }
        }

        if (errors != 0) {
            System.out.println("MAP DATA BAD");
            return false;
        }

        //Copy To Applications' Sector and Wall and Materials Lists
        sectors.clear();
        for (Sector s : newSectors) {
            s.removeDuplicateIndices();
            sectors.add(new Sector(s));
        }

        walls.clear();
        for (Wall w : newWalls) {
            walls.add(new Wall(w));
        }

        materials.clear();
        for (Material m : newMaterials) {
            materials.add(new Material(m));
        }

        return true;
    }

    @Override
    public boolean save(String path) {
        FileHandle file = Gdx.files.local(path);

        file.writeString("", false); //Clear File

        int mInd = 0;
        StringBuilder allMaterialsString = new StringBuilder();
        for (Material m : materials) {
            allMaterialsString.append(materialToText(m, "" + mInd));
            mInd++;
        }
        file.writeString(allMaterialsString.toString(), true);

        StringBuilder allSectorsString = new StringBuilder();
        int sInd = 0;
        for (Sector s : sectors) {
            allSectorsString.append(sectorToText(s, "" + sInd));
            sInd++;
        }
        file.writeString(allSectorsString.toString(), true);

        int wInd = 0;
        StringBuilder allWallsString = new StringBuilder();
        for (Wall w : walls) {
            allWallsString.append(wallToText(w, "" + wInd));
            wInd++;
        }
        file.writeString(allWallsString.toString(), true);

        return true;
    }

    // -------------------------------------------------------------------------------

    private String sectorToText(Sector s, String note) {
        StringBuilder str = new StringBuilder("SECTOR ");
        if (note != null && !note.isEmpty()) {
            str.append("(").append(note).append(") ");
        }

        str.append(":: ");

        //Height
        str.append("HEIGHT ").append( form(s.floorZ) ).append(" ").append( form(s.ceilZ) ).append(" :: ");

        //Tex
        str.append("MAT ").append( form(s.matFloor) ).append(" ").append( form(s.matCeil) ).append(" :: ");

        //Light
        str.append("LIGHT ").append( form2(s.lightFloor) ).append(" ").append( form2(s.lightCeil)).append(" :: ");

        //Wall Indices
        str.append("HAS ");
        for (int wInd : s.walls.toArray()) {
            str.append(wInd).append(" ");
        }
        str.append("::\n");

        return str.toString();
    }

    private String wallToText(Wall w, String note) {
        StringBuilder str = new StringBuilder("WALL ");

        if (note != null && !note.isEmpty()) {
            str.append("(").append(note).append(") ");
        }

        str.append(":: ");

        str.append("POS ");
        str.append(form(w.x1)).append(" ");
        str.append(form(w.y1)).append(" ");
        str.append(form(w.x2)).append(" ");
        str.append(form(w.y2)).append(" ");
        str.append(":: ");

        //Materials
        str.append("MAT ").append(w.mat).append(" :: ");
        if (w.matLower != 0) {
            str.append("LOWERMAT ").append(w.matLower).append(" :: ");
        }
        if (w.matUpper != 0) {
            str.append("UPPERMAT ").append(w.matUpper).append(" :: ");
        }

        //Light
        str.append("LIGHT ").append( form2(w.light) ).append(" :: ");

        //Portal Links
        if (w.isPortal) {
            str.append(String.format("PORT %d -> %d :: ", w.linkA, w.linkB));
        }

        str.append("\n");

        return str.toString();
    }

    private String materialToText(Material m, String note) {
        StringBuilder str = new StringBuilder("MATERIAL ");

        if (note != null && !note.isEmpty()) {
            str.append("(").append(note).append(") ");
        }
        str.append(":: ");

        //Seek out name of image that the material references
        str.append("IMG ");
        String matOrigTexName = "ERROR";
        for (Map.Entry<String, Pixmap[]> entry : pixmapContainer.pixmaps2.entrySet()) {
            if (m.tex == entry.getValue()) {
                matOrigTexName = entry.getKey().toLowerCase();
                break;
            }
        }
        str.append(matOrigTexName).append(" :: ");

        if (m.isSky)
            str.append("SKY :: ");

        str.append("\n");

        return str.toString();
    }

    private String form(double num) {
        //Prints a double as integer if no partial value
        return num % 1 == 0 ? ("" + (int) num) : ("" + num);
    }

    private String form2(double num) {
        return new DecimalFormat("#0.00").format(num);
    }

    private boolean isObjectKeyword(String str) {
        return enumContains(str, ObjectKeyword.class);
    }

    private boolean isMaterialKeyword(String str) {
            return enumContains(str, MaterialKeyword.class);
    }

    private boolean isSectorKeyword(String str) {
        return enumContains(str, SectorKeyword.class);
    }

    private boolean isWallKeyword(String str) {
        return enumContains(str, WallKeyword.class);
    }

    private <E extends Enum<E>> boolean enumContains(String str, Class<E> enumClass) {
        E[] keywords = enumClass.getEnumConstants();

        // Alternative method
        // return Arrays.toString(keywords).toUpperCase().contains(str.toUpperCase());

        for (E word : keywords) {
            String wordName = word.toString();
            if (wordName.equals(str)) return true;
        }

        return false;
    }

}
