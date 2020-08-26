package com.runningmanstudios.visualedit.tranfer;

import com.runningmanstudios.visualedit.StringSerializer;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.IOException;
import java.io.Serializable;

public class DragHandler implements DragSourceListener, DragGestureListener {
    DragDropList<?> list;

    DragSource ds = new DragSource();

    public DragHandler(DragDropList<?> list) {
        this.list = list;
        DragGestureRecognizer dgr = ds.createDefaultDragGestureRecognizer(list,
                DnDConstants.ACTION_MOVE, this);

    }

    public void dragGestureRecognized(DragGestureEvent dge) {
        StringSelection transferable = new StringSelection(list.getSelectedIndex() +":"+list.getModel().getElementAt(list.getSelectedIndex()));
        ds.startDrag(dge, DragSource.DefaultCopyDrop, transferable, this);
        try {
            list.emitDrag(new DragDropEvent(StringSerializer.deserialize((String) list.getModel().getElementAt(list.getSelectedIndex()))));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void dragDropEnd(DragSourceDropEvent dsde) {
        if (dsde.getDropSuccess()) {
            DragDropList pasteList = (DragDropList) findComponentUnderMouse();
            DefaultListModel<String> pasteModel = (DefaultListModel<String>) pasteList.getModel();
            DefaultListModel<String> copyModel = (DefaultListModel<String>) list.getModel();
            Transferable transferable = dsde.getDragSourceContext().getTransferable();
            int copyIndex;
            int pasteIndex;
            String content;
            Serializable contentObject;
            try {
                String raw = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                copyIndex = Integer.parseInt(raw.substring(0, raw.indexOf(':')));
                Point mousePos = MouseInfo.getPointerInfo().getLocation();
                SwingUtilities.convertPointFromScreen(mousePos, pasteList);
                pasteIndex = pasteList.locationToIndex(mousePos);
                content = raw.substring(raw.indexOf(':')+1);
                contentObject = (Serializable) StringSerializer.deserialize(content);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            pasteIndex = Math.max(0, Math.min(pasteIndex, pasteModel.size()));
            copyIndex = Math.max(0, Math.min(copyIndex, copyModel.size()));

            /*System.out.println("paste index : "+pasteIndex);
            System.out.println("copy index : "+copyIndex);
            System.out.println("paste form size : "+pasteModel.size());
            System.out.println("copy form size : "+copyModel.size());*/
            if (pasteList.equals(list)) {
                //settings checks
                if (!list.isCAN_DROP_ON_SELF()) {
                    return;
                }
                if (list.isREMOVE_CLONE()) {
                    list.removeItem(copyIndex);
                }

                //actually add item
                try {
                    pasteList.addItem(pasteIndex, contentObject);
                } catch (ArrayIndexOutOfBoundsException e) {
                    pasteList.addItem(contentObject);
                }

                //notify listeners
                try {
                    list.emitDrop(new DragDropEvent(StringSerializer.deserialize(content)));
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                //settings checks
                if (!pasteList.isOPEN_TO_OTHERS()) {
                    return;
                }
                if (!list.isCAN_DROP_ON_OTHER()) {
                    return;
                }
                if (list.isREMOVE_CLONE()) {
                    list.removeItem(copyIndex);
                }
                //actually add the item
                try {
                    pasteList.addItem(pasteIndex + (pasteIndex!=0?1:0), contentObject);
                } catch (ArrayIndexOutOfBoundsException e) {
                    pasteList.addItem(contentObject);
                }

                //notify the listeners
                try {
                    pasteList.emitDrop(new DragDropEvent(StringSerializer.deserialize(content)));
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        } else {
            System.out.println("Failed");
        }
    }

    @Override
    public void dragEnter(DragSourceDragEvent dsde) {

    }

    @Override
    public void dragOver(DragSourceDragEvent dsde) {

    }

    @Override
    public void dropActionChanged(DragSourceDragEvent dsde) {

    }

    @Override
    public void dragExit(DragSourceEvent dse) {

    }

    public static Component findComponentUnderMouse() {
        Window window = findWindow();
        Point location = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(location, window);
        return SwingUtilities.getDeepestComponentAt(window, location.x, location.y);
    }

    private static Window findWindow() {
        for (Window window : Window.getWindows()) {
            if (window.getMousePosition(true) != null)
                return window;
        }

        return null;
    }
}
