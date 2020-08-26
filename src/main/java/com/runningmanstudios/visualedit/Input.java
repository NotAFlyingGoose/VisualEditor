package com.runningmanstudios.visualedit;

import java.io.Serializable;

public class Input implements Serializable {
    public final static String NUMBER = "Number";
    public final static String CHARACTER = "Character";
    public final static String STRING = "String";
    public final static String VARIABLE = "Variable";
    public final static String ANY = "Any";
    private Object value = null;
    private String id;
    private String title;

    public Input(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isFilled() {
        return value != null;
    }
}
