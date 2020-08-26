package com.runningmanstudios.visualedit.tranfer;

import com.runningmanstudios.visualedit.StringSerializer;

import java.awt.*;
import java.io.IOException;
import java.io.Serializable;

public class TransferPackage implements Serializable {
    private final Point startLocation;
    private final Serializable source;
    private final IDragDropOrigin origin;
    public TransferPackage(Serializable source, IDragDropOrigin origin, Point startLocation) {
        this.source = source;
        this.origin = origin;
        this.startLocation = startLocation;
    }

    public Serializable getSource() {
        return source;
    }

    public IDragDropOrigin getOrigin() {
        return origin;
    }

    public Point getStartLocation() {
        return startLocation;
    }

    @Override
    public String toString() {
        try {
            return StringSerializer.serialize(this);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
