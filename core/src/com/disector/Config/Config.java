package com.disector.Config;

import com.badlogic.gdx.files.FileHandle;
import jdk.jpackage.internal.PackagerException;
import org.xml.sax.helpers.ParserAdapter;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Config {
    public boolean printFps = false;
    public int frameWidth = 320;
    public int frameHeight = 180;

    public Config(FileHandle file) {
        System.out.println("Loading Config at " + file.path());

        Map<String, String> fileValues = new HashMap<>();

        Scanner fileScan = new Scanner(file.readString());


        while(fileScan.hasNextLine()) {
            String line = fileScan.nextLine();
            int equalsPosition = line.indexOf('=');
            fileValues.put(line.substring(0, equalsPosition), line.substring(equalsPosition+1));
        }

        Field[] fields = Config.class.getFields();

        try {
            for (Field field : fields) {
                String fieldName = field.getName();
                String fieldType = field.getType().getSimpleName();
                boolean fieldFound = false;

                String valueStr = "";

                switch (fieldType) {
                    case "boolean":
                        valueStr = fileValues.get(fieldName);
                        if (valueStr != null) {
                            field.set(this, Boolean.parseBoolean(valueStr));
                            fieldFound = true;
                        }
                        break;
                    case "int":
                        valueStr = fileValues.get(fieldName);
                        if (valueStr != null) {
                            field.set(this, Integer.parseInt(valueStr));
                            fieldFound = true;
                        }
                        break;
                }

                System.out.println(
                        "    " + fieldName +
                        (!fieldFound ? "NOT FOUND.." : " set to " + field.get(this).toString() ) +
                        " (" + fieldType + ")"
                );

            }
        } catch (IllegalAccessException | NumberFormatException e) {
            System.out.println(e.getMessage());
        }

    }

}
