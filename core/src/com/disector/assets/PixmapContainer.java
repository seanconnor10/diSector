package com.disector.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

import java.util.Locale;

public class PixmapContainer {
    public Pixmap[][] pixmaps;

    public static final int MipMapNumber = 5;
    private static final FileHandle imgDir = Gdx.files.local("assets/img");

    public void loadImages() {
        Array<FileHandle> imgFiles = new Array<>();

        System.out.println("Pixmap Container Loading Images...");

        for (FileHandle file : imgDir.list()) {
            if (handleIsImage(file)) {
                imgFiles.add(file);
                System.out.println("   " + file);
            } else {
                System.out.println("   REJECTED " + file);
            }
        }

        pixmaps = new Pixmap[imgFiles.size][MipMapNumber];

        for (int i=0; i<imgFiles.size; i++) {
            Texture temp = new Texture(imgFiles.get(i), Pixmap.Format.RGBA8888, false);
            if (!temp.getTextureData().isPrepared()) temp.getTextureData().prepare();
            pixmaps[i][0] = temp.getTextureData().consumePixmap();
            for (int k=1; k<MipMapNumber; k++) {
                pixmaps[i][k] = halvePixmap(pixmaps[i][k-1]);
            }
            temp.dispose();
        }
    }

    private boolean handleIsImage(FileHandle handle) {
        if (handle.isDirectory())
            return false;

        String str = handle.toString().toLowerCase(Locale.ROOT);
        return str.endsWith(".png") || str.endsWith(".jpg") || str.endsWith(".bmp");
    }

    private Pixmap halvePixmap(Pixmap pix) {
        Pixmap newPix = new Pixmap(pix.getWidth()/2, pix.getHeight(), pix.getFormat());
        newPix.setFilter(Pixmap.Filter.BiLinear);
        newPix.drawPixmap(
            pix, 1, 1, pix.getWidth()-1, pix.getHeight()-1, //Source Pixmap The -1's seem to help it align
            0, 0, newPix.getWidth(), newPix.getHeight() //Destination Pixmap
        );
        return newPix;
    }

}
