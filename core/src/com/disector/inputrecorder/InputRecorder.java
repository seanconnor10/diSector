package com.disector.inputrecorder;

import com.badlogic.gdx.Gdx;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.HashMap;

public class InputRecorder implements InputInterface {
    public static boolean ignoreInput;
    public static Map<String, Integer> keyBinds = new HashMap<>();
    public static Map<Integer, KeyPressData> keyPressMap = new HashMap<>();

    public static int keyCount;
    public static float mouseDeltaX, mouseDeltaY;

    private static final InputRecorder instance = new InputRecorder();

    private InputRecorder() {} //Disallow outside instantiation beyond InputRecorder::instance..

    @Override
    public KeyPressData getKeyInfo(String actionName) {
        return InputRecorder.getAbsoluteKeyInfo(actionName);
    }

    public static void updateKeys() {
        mouseDeltaX = Gdx.input.getDeltaX();
        mouseDeltaY = Gdx.input.getDeltaY();

        if (ignoreInput)
            return;

        for (Map.Entry<Integer, KeyPressData> keyEntry : keyPressMap.entrySet()) {
            keyEntry.getValue().justReleased = keyEntry.getValue().isDown && !Gdx.input.isKeyPressed(keyEntry.getKey());
            keyEntry.getValue().isDown = Gdx.input.isKeyPressed(keyEntry.getKey());
            keyEntry.getValue().justPressed = Gdx.input.isKeyJustPressed(keyEntry.getKey());
        }
    }

    public static void repopulateKeyCodeMap() {
        Field[] fields = KeyMapping.class.getFields();

        keyBinds.clear(); //Map of 'Action Names' and the keyCode they're assign to
        for (Field field : fields) {
            if (!field.isAnnotationPresent(KeyCode.class)) continue;;
            String fieldName = field.getName();
            try {
                keyBinds.put(fieldName, field.getInt(InputRecorder.class));
            } catch (Exception e) {
                System.out.println(" -=== ERROR ===-");
                System.out.println(" InputRecorder failed to repopulate KeyCodeMap via Reflection");
                System.out.println(" Exception Type: " + e.getClass().getName() );
                System.out.println(" " + e.getMessage());
                System.out.println(" -=============-");
                System.exit(1);
            }
        }

        System.out.println(mapToString());

        keyPressMap.clear(); //Map of keyCodes assigned to an action and a keyPressData for each
        for (Integer code : keyBinds.values() ) {
            keyPressMap.put(code, new KeyPressData() );
        }
        //System.out.println("InputRecorder::KeyPressMap = " + keyPressMap.entrySet().toString());

        keyCount = keyPressMap.size();
    }

    public static KeyPressData getAbsoluteKeyInfo(String actionName) {
        KeyPressData data = keyPressMap.getOrDefault( keyBinds.getOrDefault(actionName, -1), null );
        if (data == null) {
            throw new RuntimeException("Action: " + actionName + " not found in KeyBind map.");
        }
        return data;
    }

    public static String mapToString() {
        return "InputRecorder::KeyCodeMap = " + keyBinds.toString().replace("}", "\n}").replace("{", "{\n    ").replace(", ", ", \n    ");
    }

}
