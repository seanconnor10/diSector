package com.disector.console;

import com.disector.Application;
import com.badlogic.gdx.Gdx;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class CommandExecutor {

    private Application app;

    private Map<String, Method> methods = new HashMap<>();

    public CommandExecutor(Application app) {
        this.app = app;
        Method[] allMethods = CommandExecutor.class.getMethods();
        for (Method m : allMethods) {
            if (m.isAnnotationPresent(ConsoleCommand.class))
                methods.put(m.getName(), m);
        }
    }

    public void execute(String command) {
        String[] commandParts = command.split(" ");
        Method method = methods.get(commandParts[0]);
        Parameter[] params = method.getParameters();
        Object[] args = new Object[params.length];

        try {
            for (int i = 0; i < params.length; i++) {
                args[i] = stringToObject(commandParts[i+1], params[i]);
            }
        } catch (NumberFormatException e) {
            //Print error to game console
        }

        try {
            method.invoke(this, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.out.println(e.getMessage());
        }
    }

    private Object stringToObject(String argStr, Parameter p) {
        switch (p.getType().getName()) {
            case "int":
                return Integer.parseInt(argStr);
            case "String":
                return argStr;
            case "double":
                return Double.parseDouble(argStr);
            case "float":
                return Float.parseFloat(argStr);
            case "boolean":
                return Boolean.parseBoolean(argStr);
            default:
                System.out.println("wtf");
                return null;
        }
    }

    // -------------------------------------------------------------------

    @ConsoleCommand
    public void toggleFullscreen() {
        if (Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setWindowedMode(app.frameWidth, app.frameHeight);
        } else {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
    }

    @ConsoleCommand
    public void setGameFrameSize(int w, int h) {
        //if (w < 1 || h < 1) return;
        app.setGameRenderFrameSize(w, h);
    }

}
