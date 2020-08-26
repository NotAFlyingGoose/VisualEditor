package com.runningmanstudios.visualedit.tranfer;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public interface IDragDropOrigin extends Serializable {
    boolean canReceive(Point mousePos, TransferPackage data, TransferHandler.TransferSupport support);
    void receive(Point mousePos, TransferPackage data, IDragDropOrigin originator);
    Serializable request(Point mousePos);
}
