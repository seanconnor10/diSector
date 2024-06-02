package com.disector;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.disector.editor.EditorInterface;
import com.disector.game.World;
import com.disector.renderer.RendererInterface;

public class App extends ApplicationAdapter {
        SpriteBatch batch;
        EditorInterface Editor;
        RendererInterface Renderer;
        World world;

        @Override
        public void create () {
            batch = new SpriteBatch();
        }

        @Override
        public void render () {
            ScreenUtils.clear(1, 0, 0, 1);
            batch.begin();
            batch.end();
        }

        @Override
        public void dispose () {
            batch.dispose();
        }
}
