package com.disector.editor;

import com.disector.AppFocusTarget;
import com.disector.maploader.TextFileMapLoader;

class MenuPanel extends Panel {
    public MenuPanel(Editor editor) {
        super(editor);

        Button loadButton = new Button(editor, this, "LOAD");
        loadButton.releaseAction = (Void) -> {
            editor.loadMap("MAPS/test.txt");
            return Void;
        };
        buttons.add(loadButton);

        Button saveButton = new Button(editor, this, "SAVE");
        saveButton.releaseAction = (Void) -> {
            new TextFileMapLoader(editor.app).save("MAPS/test.txt");
            editor.messageLog.log("Saved to MAPS/test.txt");
            return Void;
        };
        buttons.add(saveButton);

        Button playButton = new Button(editor, this, "PLAY");
        playButton.releaseAction = (Void) -> {
            editor.app.swapFocus(AppFocusTarget.GAME);
            return Void;
        };
        buttons.add(playButton);
    }
}
