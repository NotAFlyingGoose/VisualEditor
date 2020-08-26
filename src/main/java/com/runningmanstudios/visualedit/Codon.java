package com.runningmanstudios.visualedit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Codon implements Serializable {
    private final List<Input> inputs = new ArrayList<>();
    private final String name;
    private String display;

    public Codon(String name) {
        this.name = name;
        this.display = name;
    }

    public Codon addInput(Input input) {
        inputs.add(input);
        if (!this.display.equals(this.name)) this.display+=",";
        this.display += " `"+input.getTitle()+"`";
        return this;
    }

    public Input getNextUnfilledInput() {
        for (Input input : inputs) {
            if (!input.isFilled()) return input;
        }
        return null;
    }

    public boolean hasNextInput() {
        return getNextUnfilledInput() != null;
    }

    public String getName() {
        return name;
    }

    public String getDisplay() {
        return display;
    }

    @Override
    public String toString() {
        return getDisplay();
    }
}
