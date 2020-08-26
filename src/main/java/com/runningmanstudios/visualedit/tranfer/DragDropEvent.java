package com.runningmanstudios.visualedit.tranfer;

public class DragDropEvent {
    private final Object source;

    public DragDropEvent(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }
}
