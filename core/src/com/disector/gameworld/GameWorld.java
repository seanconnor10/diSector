package com.disector.gameworld;

import com.disector.App;

public class GameWorld {
    private final App app;

    private Player player1;

    public GameWorld(App app) {
        this.app = app;
        player1 = new Player(this);
        player1.x = 20.f;
    }

    public void step(float dt) {
        player1.step(dt);
    }
}
