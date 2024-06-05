package com.disector.inputrecorder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.HashMap;

public class InputRecorder {
    public static Map<String, Integer> keyBinds = new HashMap<>();
    public static Map<Integer, keyPressData> keyPressMap = new HashMap<>();
    public static int keyCount;
    public static float mouseDeltaX;

    @KeyCode
    public static int FORWARD = Input.Keys.W;
    @KeyCode
    public static int LEFT = Input.Keys.A;
    @KeyCode
    public static int BACKWARD = Input.Keys.S;
    @KeyCode
    public static int RIGHT = Input.Keys.D;
    @KeyCode
    public static int TURN_LEFT = Input.Keys.LEFT;
    @KeyCode
    public static int TURN_RIGHT = Input.Keys.RIGHT;
    @KeyCode
    public static int LOOK_UP = Input.Keys.UP;
    @KeyCode
    public static int LOOK_DOWN = Input.Keys.DOWN;

    public static void updateKeys() {
        mouseDeltaX = Gdx.input.getDeltaX();

        for (Map.Entry<Integer, keyPressData> keyEntry : keyPressMap.entrySet()) {
            keyEntry.getValue().justReleased = keyEntry.getValue().isDown && !Gdx.input.isKeyPressed(keyEntry.getKey());
            keyEntry.getValue().isDown = Gdx.input.isKeyPressed(keyEntry.getKey());
            keyEntry.getValue().justPressed = Gdx.input.isKeyJustPressed(keyEntry.getKey());
        }
    }

    public static void repopulateKeyCodeMap() throws Exception {
        Field[] fields = InputRecorder.class.getFields();

        keyBinds.clear(); //Map of 'Action Names' and the keyCode they're assign to
        for (Field field : fields) {
            if (!field.isAnnotationPresent(KeyCode.class)) continue;;
            String fieldName = field.getName();
            int fieldValue = field.getInt(InputRecorder.class);
            keyBinds.put(fieldName, fieldValue);
        }
        System.out.println("InputRecorder::KeyCodeMap = " + keyBinds.toString());

        keyPressMap.clear(); //Map of keyCodes assigned to an action and a keyPressData for each
        for (Integer code : keyBinds.values() ) {
            keyPressMap.put(code, new keyPressData() );
        }
        //System.out.println("InputRecorder::KeyPressMap = " + keyPressMap.entrySet().toString());

        keyCount = keyPressMap.size();
    }

    public static keyPressData getKeyInfo(String actionName) {
        keyPressData data = keyPressMap.getOrDefault( keyBinds.getOrDefault(actionName, -1), null );
        if (data == null) {
            throw new RuntimeException("Action: " + actionName + " not found in KeyBind map.");
        }
        return data;
    }

    public static class keyPressData {
        public boolean isDown, justPressed, justReleased;

        @Override
        public String toString() {
            String str = "" + (isDown ? "CurrentlyPressed " : "") + (justPressed ? "NewlyPressed " : "") + (justReleased ? "NewlyReleased " : "");
            if (str.isEmpty()) str = "none";
            return str;
            //return  "Pressed:" + isDown + "  JustPressed:" + justPressed + "  JustReleased:" + justReleased;
        }
    }

}
