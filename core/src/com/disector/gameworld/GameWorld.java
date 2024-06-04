package com.disector.gameworld;

import com.badlogic.gdx.math.Vector4;
import com.disector.App;

public class GameWorld {
    private final App app;

    private Player player1;

    public GameWorld(App app) {
        this.app = app;
        player1 = new Player(this);
        player1.z = 20.f;
    }

    public void step(float dt) {
        player1.step(dt);
    }

    public Vector4 getPlayerPosition() {
        return new Vector4(player1.x, player1.y, player1.z, player1.r);
    }
}
