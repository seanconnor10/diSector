package com.disector.editor;

import com.badlogic.gdx.utils.Array;

class EditorMessageLog {
    static final int MAX_LIFE = 7; //Max time a message is displayed in seconds

    private final Array<Message> messages = new Array<>();

    EditorMessageLog() {
        log("-= WELCOME =-");
    }

    int size() {
        return messages.size;
    }

    String get(int index) {
        return (index<0 || index>=messages.size) ? null : messages.get(index).text;
    }

    void log(String message) {
        //messages.add(message);
        //messageLifetime.add(0f);
        messages.add( new Message(message) );
    }

    void stepLifeTime(float dt) {
        for (int i=0; i<messages.size; i++) {
            messages.get(i).lifeTime += dt;

            if (messages.get(i).lifeTime > MAX_LIFE) {
                messages.removeIndex(i);
                i--;
            }
        }
    }

    private static class Message {
        private final String text;
        private Float lifeTime = 0f;

        private Message(String messageText) {
            this.text = messageText;
        }
    }

}
