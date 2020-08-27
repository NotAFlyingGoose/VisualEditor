package com.runningmanstudios.visualedit.components;

import com.runningmanstudios.visualedit.tranfer.DragDropTransferHandler;
import com.runningmanstudios.visualedit.tranfer.IDragDropOrigin;
import com.runningmanstudios.visualedit.tranfer.TransferPackage;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public class DragDropPanel extends JPanel implements IDragDropOrigin {

    public DragDropPanel() {
        setTransferHandler(new DragDropTransferHandler(this, this, true));
    }

    @Override
    public void receive(Point mousePos, TransferPackage data, IDragDropOrigin originator) {
        System.out.println("receiving " + data.getSource() + " from " + data.getOrigin());
    }

    @Override
    public boolean canReceive(Point mousePos, TransferPackage data, TransferHandler.TransferSupport support) {
        return true;
    }

    @Override
    public Serializable request(Point mousePos) {
        return null;
    }
}
