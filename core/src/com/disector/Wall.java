package com.disector;

public class Wall {
    public float x1, y1, x2, y2;
    public boolean isPortal;
    public int linkA, linkB;

    public Wall(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
}
