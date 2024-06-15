package com.disector.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;

import java.util.Locale;

public class PixmapContainer {
    public Pixmap[] pixmaps = new Pixmap[0];

    private static final FileHandle imgDir = Gdx.files.local("assets/img");

    public PixmapContainer() {

    }

    public void loadImages() {
        Array<FileHandle> imgFile = new Array<>();

        for (FileHandle file : imgDir.list()) {
            if (handleIsImage(file))
                imgFile.add(file);
        }
    }

    private boolean handleIsImage(FileHandle handle) {
        if (handle.isDirectory())
            return false;

        String str = handle.toString().toLowerCase(Locale.ROOT);
        return str.endsWith(".png") || str.endsWith(".jpg") || str.endsWith(".bmp");
    }
}
