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

    public void setInput(Input input) {
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
        for (Input input : inputs) {
            if (input.getValue() != null) {
                display = display.replace(input.getTitle(), input.getValue().toString());
            }
        }
        return display;
    }

    @Override
    public String toString() {
        return getDisplay();
    }

    @Override
    public boolean equals(Object obj) {
        return ((Codon) obj).getName().equals(getName());
    }
}
