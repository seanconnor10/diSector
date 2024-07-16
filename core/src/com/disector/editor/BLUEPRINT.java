package com.disector.editor;

import com.disector.AppFocusTarget;
import com.disector.maploader.TextFileMapLoader;

final class BLUEPRINT {
    /*
        Static Functions to create Panel and their buttons
     */

    private BLUEPRINT() {
        throw new RuntimeException("Don't Instantiate Me");
    }

    static Panel createViewPanel(Editor editor) {
        return new Panel(editor);
    }

    static Panel createMenuPanel(Editor editor) cd {
        Panel p = new Panel(editor);

        Button loadButton = new Button(editor, p, "LOAD");
        loadButton.releaseAction = (Void) -> {
             if (editor.app.loadMap("MAPS/test.txt") )
                 editor.messageLog.log("Loaded from MAPS/test.txt");
             else
                 editor.messageLog.log("FAILED TO LOAD MAPS/test.txt");
             return Void;
        };
        p.buttons.add(loadButton);


        Button saveButton = new Button(editor, p, "SAVE");
        saveButton.releaseAction = (Void) -> {
            new TextFileMapLoader(editor.app).save("MAPS/test.txt");
            editor.messageLog.log("Saved to MAPS/test.txt");
            return Void;
        };
        p.buttons.add(saveButton);


        Button playButton = new Button(editor, p, "PLAY");
        playButton.releaseAction = (Void) -> {
            editor.app.swapFocus(AppFocusTarget.GAME);
            return Void;
        };
        p.buttons.add(playButton);

        return p;
    }
}
