package com.disector.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

import java.util.Locale;

public class PixmapContainer {
    public Pixmap[] pixmaps;

    private static final FileHandle imgDir = Gdx.files.local("assets/img");

    public void loadImages() {
        Array<FileHandle> imgFiles = new Array<>();

        for (FileHandle file : imgDir.list()) {
            if (handleIsImage(file))
                imgFiles.add(file);
        }

        pixmaps = new Pixmap[imgFiles.size];

        for (int i=0; i<imgFiles.size; i++) {
            Texture temp = new Texture(imgFiles.get(i));
            if (!temp.getTextureData().isPrepared()) temp.getTextureData().prepare();
            pixmaps[i] = temp.getTextureData().consumePixmap();
            temp.dispose();
        }
    }

    private boolean handleIsImage(FileHandle handle) {
        if (handle.isDirectory())
            return false;

        String str = handle.toString().toLowerCase(Locale.ROOT);
        return str.endsWith(".png") || str.endsWith(".jpg") || str.endsWith(".bmp");
    }
}
