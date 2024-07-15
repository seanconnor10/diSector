package com.disector.editor;

import java.util.ArrayList;
import java.util.List;


class EditorMessageLog {
    static final float MAX_LIFE = 7;

    List<String> messages        = new ArrayList<>();
    List<Float>  messageLifetime = new ArrayList<>();

    public EditorMessageLog() {
        log("-= WELCOME =-");
    }

    void log(String message) {
        messages.add(message);
        messageLifetime.add(0f);
    }

    void stepLifeTime(float dt) {
        for (int i=0; i<messages.size(); i++) {

            float val = messageLifetime.get(i);
            messageLifetime.set( i, val + dt);

            if (val > MAX_LIFE) {
                messages.remove(i);
                messageLifetime.remove(i);
                i--;
            }
        }
    }

}
