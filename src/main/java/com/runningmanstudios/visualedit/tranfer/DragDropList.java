package com.runningmanstudios.visualedit.tranfer;

import com.runningmanstudios.visualedit.StringSerializer;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DragDropList <T extends Serializable> extends JList<T> implements IDragDropOrigin {
    private final List<T> objects = new ArrayList<>();
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

        setTransferHandler(new DragDropTransferHandler(this, this, true));

        setCellRenderer(new ListRenderer<>());
    }

    public DragDropList(T[] items) {
        super(new DefaultListModel<T>());
        model = (DefaultListModel<String>) getModel();
        setDragEnabled(true);
        setDropMode(DropMode.INSERT);

        setTransferHandler(new DragDropTransferHandler(this, this, true));

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
        objects.add(item);
        reloadObjects();
    }

    public void addItem(int index, T item) {
        objects.add(index, item);
        reloadObjects();
    }

    public void setItem(T item) {
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i).equals(item)) {
                objects.set(i, item);
                break;
            }
        }
        reloadObjects();
    }

    public void setItem(int index, T item) {
        objects.set(index, item);
        reloadObjects();
    }

    public void removeItem(int index) {
        model.remove(index);
        objects.remove(index);
    }

    public void reloadObjects() {
        model.clear();
        for (T object : objects) {
            try {
                model.addElement(StringSerializer.serialize(object));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean canReceive(Point mousePos, TransferPackage data, TransferHandler.TransferSupport support) {
        JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
        return dl.getIndex() != -1;
    }

    @Override
    public void receive(Point mousePos, TransferPackage data, IDragDropOrigin originator) {
        boolean listToList = originator instanceof DragDropList;
        DragDropList<?> otherList = null;
        DefaultListModel<?> otherModel = null;
        if (listToList) {
            otherList = (DragDropList<?>) originator;
            otherModel = (DefaultListModel<?>) otherList.getModel();
        }
        int pasteIndex;
        int copyIndex = 0;
        try {
            Point start = data.getStartLocation();
            SwingUtilities.convertPointFromScreen(start, this);
            SwingUtilities.convertPointFromScreen(mousePos, this);
            pasteIndex = locationToIndex(mousePos);
            if (listToList) copyIndex = locationToIndex(start);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        pasteIndex = Math.max(0, Math.min(pasteIndex, model.size()));
        if (listToList) copyIndex = Math.max(0, Math.min(copyIndex, otherModel.size()));

            /*System.out.println("paste index : "+pasteIndex);
            System.out.println("copy index : "+copyIndex);
            System.out.println("paste form size : "+pasteModel.size());
            System.out.println("copy form size : "+copyModel.size());*/
        if (listToList && this.equals(otherList)) {
            //settings checks
            if (!isCAN_DROP_ON_SELF()) {
                return;
            }
            if (isREMOVE_CLONE()) {
                removeItem(copyIndex);
            }

            //actually add item
            try {
                addItem(pasteIndex, (T) data.getSource());
            } catch (ArrayIndexOutOfBoundsException e) {
                addItem((T) data.getSource());
            }

            //notify listeners
            otherList.emitDrop(new DragDropEvent(data.getSource()));
        } else if (listToList) {
            //settings checks
            if (!isOPEN_TO_OTHERS()) {
                return;
            }
            if (!otherList.isCAN_DROP_ON_OTHER()) {
                return;
            }
            if (otherList.isREMOVE_CLONE()) {
                otherList.removeItem(copyIndex);
            }
            //actually add the item
            try {
                addItem(pasteIndex + (pasteIndex!=0?1:0), (T) data.getSource());
            } catch (ArrayIndexOutOfBoundsException e) {
                addItem((T) data.getSource());
            }

            //notify the listeners
            emitDrop(new DragDropEvent(data.getSource()));
        } else {
            //settings checks
            if (!isOPEN_TO_OTHERS()) {
                return;
            }
            //actually add the item
            try {
                addItem(pasteIndex + (pasteIndex!=0?1:0), (T) data.getSource());
            } catch (ArrayIndexOutOfBoundsException e) {
                addItem((T) data.getSource());
            }

            //notify the listeners
            emitDrop(new DragDropEvent(data.getSource()));
        }
    }

    @Override
    public Serializable request(Point mousePos) {
        String obj = model.getElementAt(getSelectedIndex());
        try {
            emitDrag(new DragDropEvent(StringSerializer.deserialize(obj)));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
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
