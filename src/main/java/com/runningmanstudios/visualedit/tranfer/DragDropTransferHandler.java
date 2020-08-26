package com.runningmanstudios.visualedit.tranfer;

import com.runningmanstudios.visualedit.StringSerializer;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.IOException;
import java.io.Serializable;

public class DragDropTransferHandler extends TransferHandler implements DragSourceListener, DragGestureListener {
    Component component;
    IDragDropOrigin origin;
    DragSource ds = new DragSource();
    boolean autoDeserializeStrings;

    public DragDropTransferHandler(IDragDropOrigin origin, Component component, boolean autoDeserializeStrings) {
        this.origin = origin;
        this.component = component;
        this.autoDeserializeStrings = autoDeserializeStrings;
        DragGestureRecognizer dgr = ds.createDefaultDragGestureRecognizer(component,
                DnDConstants.ACTION_MOVE, this);
    }

    //detecting imports
    public boolean canImport(TransferHandler.TransferSupport support) {
        if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return false;
        }
        try {
            return origin.canReceive(support.getDropLocation().getDropPoint(), (TransferPackage) StringSerializer.deserialize(support.getTransferable().getTransferData(DataFlavor.stringFlavor).toString()), support);
        } catch (IOException | UnsupportedFlavorException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean importData(TransferHandler.TransferSupport support) {
        return canImport(support);
    }

    //managing exports
    public void dragGestureRecognized(DragGestureEvent dge) {
        Point mousePos = MouseInfo.getPointerInfo().getLocation();
        Serializable obj = origin.request(mousePos);
        if (obj != null) {
            if (obj instanceof String && autoDeserializeStrings) {
                try {
                    obj = (Serializable) StringSerializer.deserialize(obj.toString());
                } catch (IOException | ClassNotFoundException ignored) { }
            }
            StringSelection transferable = new StringSelection(new TransferPackage(obj, origin, mousePos).toString());
            ds.startDrag(dge, DragSource.DefaultCopyDrop, transferable, this);
        }
    }

    public void dragDropEnd(DragSourceDropEvent dsde) {
        if (dsde.getDropSuccess()) {
            IDragDropOrigin paste = findOriginUnderMouse();
            Component pasteCom = findComponentUnderMouse();
            if (paste == null) {
                System.err.println("Transfer Failed");
                return;
            }
            Transferable transferable = dsde.getDragSourceContext().getTransferable();
            try {
                String raw = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                Point mousePos = MouseInfo.getPointerInfo().getLocation();
                TransferPackage content = (TransferPackage) StringSerializer.deserialize(raw);
                paste.receive(mousePos, content, origin);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            System.err.println("Transfer Failed");
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

    public static IDragDropOrigin findOriginUnderMouse() {
        Window window = findWindow();
        assert window != null;
        Point location = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(location, window);
        Component c = SwingUtilities.getDeepestComponentAt(window, location.x, location.y);
        if (c instanceof IDragDropOrigin) {
            return (IDragDropOrigin) c;
        } else {
            return null;
        }
    }

    public static Component findComponentUnderMouse() {
        Window window = findWindow();
        assert window != null;
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
