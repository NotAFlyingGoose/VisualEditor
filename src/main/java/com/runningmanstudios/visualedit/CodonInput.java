package com.runningmanstudios.visualedit;

import java.awt.*;
import java.io.Serializable;

public class CodonInput implements Serializable {
    public final static String NUMBER = "Number";
    public final static String BOOLEAN = "Boolean";
    public final static String DROPDOWN = "Dropdown";
    public final static String STRING = "String";
    public final static String VARIABLE = "Variable";
    public final static String ANY = "Any";
    private final String id;
    private final String title;
    public Color color = Color.gray;
    private Codon value = null;

    public CodonInput(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Codon value) {
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

    public int getFullWidth(FontMetrics fontMetrics) {
        int width = 10;
        if (isFilled()) {
            width += value.getFullWidth(fontMetrics);
        } else {
            width += fontMetrics.stringWidth(id+" "+title);
        }

        return width;
    }
}
