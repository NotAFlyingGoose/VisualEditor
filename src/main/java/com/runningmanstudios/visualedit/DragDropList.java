package com.runningmanstudios.visualedit;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DragDropList <T extends Serializable> extends JList<T> {
    private final DefaultListModel<String> model;
    private boolean selfCopy = true;
    private boolean originDel = true;
    List<DragDropListener> listeners = new ArrayList<>();
    public boolean CAN_DROP_ON_OTHER = true;

    public boolean isCAN_DROP_ON_OTHER() {
        return CAN_DROP_ON_OTHER;
    }

    public void setCAN_DROP_ON_OTHER(boolean CAN_DROP_ON_OTHER) {
        this.CAN_DROP_ON_OTHER = CAN_DROP_ON_OTHER;
    }

    public boolean isCAN_DROP_ON_SELF() {
        return CAN_DROP_ON_SELF;
    }

    public void setCAN_DROP_ON_SELF(boolean CAN_DROP_ON_SELF) {
        this.CAN_DROP_ON_SELF = CAN_DROP_ON_SELF;
    }

    public boolean isREMOVE_CLONE() {
        return REMOVE_CLONE;
    }

    public void setREMOVE_CLONE(boolean REMOVE_CLONE) {
        this.REMOVE_CLONE = REMOVE_CLONE;
    }

    public boolean isOPEN_TO_OTHERS() {
        return OPEN_TO_OTHERS;
    }

    public void setOPEN_TO_OTHERS(boolean OPEN_TO_OTHERS) {
        this.OPEN_TO_OTHERS = OPEN_TO_OTHERS;
    }

    public boolean CAN_DROP_ON_SELF = true;
    public boolean REMOVE_CLONE = true;
    public boolean OPEN_TO_OTHERS = true;

    public void addDragDropListener(DragDropListener dragDropListener) {
        listeners.add(dragDropListener);
    }

    public void emitDrop(DragDropEvent dragDropEvent) {
        for (DragDropListener listener : listeners) {
            listener.onDropAction(dragDropEvent);
        }
    }

    public void emitDrag(DragDropEvent dragDropEvent) {
        for (DragDropListener listener : listeners) {
            listener.onDragAction(dragDropEvent);
        }
    }

    public DragDropList() {
        super(new DefaultListModel<>());
        model = (DefaultListModel<String>) getModel();
        setDragEnabled(true);
        setDropMode(DropMode.INSERT);

        setTransferHandler(new MyListDropHandler(this));

        new MyDragListener(this);

        setCellRenderer(new ListRenderer<>());
    }

    public DragDropList(T[] items) {
        super(new DefaultListModel<T>());
        model = (DefaultListModel<String>) getModel();
        setDragEnabled(true);
        setDropMode(DropMode.INSERT);

        setTransferHandler(new MyListDropHandler(this));

        new MyDragListener(this);

        addAll(items);
    }

    public void addAll(Iterable<T> items) {
        for (T item : items) {
            addItem(item);
        }
    }

    public void addAll(T[] items) {
        for (T item : items) {
            addItem(item);
        }
    }

    public void addItem(T item) {
        try {
            model.addElement(StringSerializer.serialize(item));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ListRenderer<T> extends JLabel implements ListCellRenderer<T> {

    public ListRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends T> list,
                                                  T value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        try {
            setText(StringSerializer.deserialize(list.getModel().getElementAt(index).toString()).toString());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return this;
    }
}

class MyDragListener implements DragSourceListener, DragGestureListener {
    DragDropList<?> list;

    DragSource ds = new DragSource();

    public MyDragListener(DragDropList<?> list) {
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
            try {
                String raw = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                copyIndex = Integer.parseInt(raw.substring(0, raw.indexOf(':')));
                Point mousePos = MouseInfo.getPointerInfo().getLocation();
                SwingUtilities.convertPointFromScreen(mousePos, pasteList);
                pasteIndex = pasteList.locationToIndex(mousePos);
                content = raw.substring(raw.indexOf(':')+1);
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
                if (!list.isCAN_DROP_ON_SELF()) {
                    return;
                }
                if (list.isREMOVE_CLONE()) {
                    copyModel.remove(copyIndex);
                }
                pasteModel.add(pasteIndex, content);
                try {
                    list.emitDrop(new DragDropEvent(StringSerializer.deserialize(content)));
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                if (!pasteList.isOPEN_TO_OTHERS()) {
                    return;
                }
                if (!list.isCAN_DROP_ON_OTHER()) {
                    return;
                }
                if (list.isREMOVE_CLONE()) {
                    copyModel.remove(copyIndex);
                }
                try {
                    pasteModel.add(pasteIndex + (pasteIndex!=0?1:0), content);
                } catch (ArrayIndexOutOfBoundsException e) {
                    pasteModel.addElement(content);
                }

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

class MyListDropHandler extends TransferHandler {
    DragDropList<?> list;

    public MyListDropHandler(DragDropList<?> list) {
        this.list = list;
    }

    public boolean canImport(TransferHandler.TransferSupport support) {
        if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return false;
        }
        JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
        return dl.getIndex() != -1;
    }

    public boolean importData(TransferHandler.TransferSupport support) {
        return canImport(support);
    }
}
