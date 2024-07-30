package com.disector.maploader;

public interface MapLoader {
    boolean load(String path) throws Exception;
    boolean save(String path);
}
