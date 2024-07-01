package com.disector.assets;

import com.badlogic.gdx.graphics.Pixmap;

public class Material {
    public Pixmap[] tex;
    public boolean isSky;

    public Material() {

    }

    public Material(Pixmap[] tex, boolean isSky) {
        this.tex = tex;
        this.isSky = isSky;
    }

    public Material(Material copySrc) {
        this.tex = copySrc.tex;
        this.isSky = copySrc.isSky;
    }
}
