package com.runningmanstudios.visualedit;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Codon implements Serializable {
    public boolean bright = false;
    int x = 50;
    int y = 50;
    Color color = new Color(55, 200, 55);
    private final List<CodonInput> inputs = new ArrayList<>();
    private final String name;
    private String display = "";

    private Codon lastCode = null;
    private Codon nextCode = null;

    public Codon(String name) {
        this.name = name;

    }

    public Codon(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public Codon addInput(CodonInput input) {
        input.color = CodeDisplay.darken(this.color, 50);
        inputs.add(input);
        if (!this.display.equals("")) this.display+=", ";
        this.display += input.getTitle();
        return this;
    }

    public List<CodonInput> getInputs() {
        return inputs;
    }

    public CodonInput getNextUnfilledInput() {
        for (CodonInput input : inputs) {
            if (!input.isFilled()) return input;
        }
        return null;
    }

    public void setInput(CodonInput input) {
        for (int i = 0; i < inputs.size(); i++) {
            if (inputs.get(i).getTitle().equals(input.getTitle())) {
                inputs.set(i, input);
                break;
            }
        }
    }

    public boolean hasNextInput() {
        return getNextUnfilledInput() != null;
    }

    public String getName() {
        return name;
    }

    public String getDisplay() {
        for (CodonInput input : inputs) {
            if (input.getValue() != null) {
                display = display.replace(input.getTitle(), input.getValue().toString());
            }
        }
        return name + "(" + display + ")";
    }

    @Override
    public String toString() {
        return getDisplay();
    }

    @Override
    public boolean equals(Object obj) {
        return ((Codon) obj).getName().equals(getName());
    }

    public int getFullWidth(FontMetrics fontMetrics) {
        int width = 10;
        width += fontMetrics.stringWidth(name);
        for (CodonInput input : inputs) {
            width += input.getFullWidth(fontMetrics);
        }

        return width;
    }

    public Codon getNextCode() {
        return nextCode;
    }

    public void setNextCode(Codon nextCode) {
        this.nextCode = nextCode;
    }

    public Codon getLastCode() {
        return lastCode;
    }

    public void setLastCode(Codon lastCode) {
        this.lastCode = lastCode;
    }
}
