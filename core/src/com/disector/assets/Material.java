package com.disector.assets;

import com.badlogic.gdx.graphics.Pixmap;

public class Material {
    public Pixmap[] tex;
    public boolean isSky;
    public String nameReference;

    public Material() {

    }

    public Material(Pixmap[] tex, String name, boolean isSky) {
        this.tex = tex;
        this.isSky = isSky;
        this.nameReference = name;
    }

    public Material(Material copySrc) {
        this.tex = copySrc.tex;
        this.isSky = copySrc.isSky;
        this.nameReference = copySrc.nameReference;
    }
}
