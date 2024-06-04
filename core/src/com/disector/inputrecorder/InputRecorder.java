package com.disector.inputrecorder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.HashMap;

public class InputRecorder {
    public static Map<String, Integer> keyCodeMap = new HashMap<>();
    public static Map<Integer, keyPressData> keyPressMap = new HashMap<>();
    public static int keyCount;

    @KeyCode
    public static int P1_FORWARD = Input.Keys.W;
    @KeyCode
    public static int P1_LEFT = Input.Keys.A;
    @KeyCode
    public static int P1_BACKWARD = Input.Keys.S;
    @KeyCode
    public static int P1_RIGHT = Input.Keys.D;

    public static void updateKeys() {
        for (Map.Entry<Integer, keyPressData> keyEntry : keyPressMap.entrySet()) {
            keyEntry.getValue().justReleased = !keyEntry.getValue().isDown && Gdx.input.isKeyPressed(keyEntry.getKey());
            keyEntry.getValue().isDown = Gdx.input.isKeyPressed(keyEntry.getKey());
            keyEntry.getValue().justPressed = Gdx.input.isKeyJustPressed(keyEntry.getKey());
        }
    }

    public static void repopulateKeyCodeMap() throws Exception {
        Field[] fields = InputRecorder.class.getFields();

        keyCodeMap.clear(); //Map of 'Action Names' and the keyCode they're assign to
        for (Field field : fields) {
            if (!field.isAnnotationPresent(KeyCode.class)) continue;;
            String fieldName = field.getName();
            int fieldValue = field.getInt(InputRecorder.class);
            keyCodeMap.put(fieldName, fieldValue);
        }
        System.out.println("InputRecorder::KeyCodeMap = " + keyCodeMap.toString());

        keyPressMap.clear(); //Map of keyCodes assigned to an action and a keyPressData for each
        for (Integer code : keyCodeMap.values() ) {
            keyPressMap.put(code, new keyPressData() );
        }
        System.out.println("InputRecorder::KeyPressMap = " + keyPressMap.values().toString());
        keyCount = keyPressMap.size();
    }

    public static keyPressData getKeyInfo(String actionName) {
        return keyPressMap.get( keyCodeMap.get(actionName) );
    }

    public static class keyPressData {
        boolean isDown, justPressed, justReleased;
    }

}
