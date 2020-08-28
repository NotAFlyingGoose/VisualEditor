package com.runningmanstudios.visualedit;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BigBoiCodon extends Codon {
    private Codon inner = null;
    public BigBoiCodon(String name) {
        super(name);
    }

    public BigBoiCodon(String name, Color color) {
        super(name, color);
    }

    public void setInner(Codon codon) {
        this.inner = codon;
    }

    public Codon getInnerCodon() {
        return inner;
    }

    public List<Codon> getInnerCodonList() {
        List<Codon> inners = new ArrayList<>();
        Codon next = inner;
        while (next != null) {
            inners.add(next);
            next = next.getNextCode();
        }
        return inners;
    }
}
