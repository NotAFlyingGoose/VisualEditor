package com.runningmanstudios.visualedit;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JPanel {
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.white);
        g.setFont(new Font(g.getFont().getName(), Font.BOLD, 20));
        g.drawString("cool i guess", 10, 50);
        g.dispose();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setPreferredSize(new Dimension(300, 280));

        Main ch = new Main();
        frame.getContentPane().add(ch);
        frame.setUndecorated(true);

        MoveMouseListener mml = new MoveMouseListener(ch);
        ch.addMouseListener(mml);
        ch.addMouseMotionListener(mml);

        frame.pack();
        frame.setVisible(true);
    }
}

class MoveMouseListener implements MouseListener, MouseMotionListener {
    JComponent target;
    Point start_drag;
    Point start_loc;

    public MoveMouseListener(JComponent target) {
        this.target = target;
    }

    public static JFrame getFrame(Container target) {
        if (target instanceof JFrame) {
            return (JFrame) target;
        }
        return getFrame(target.getParent());
    }

    Point getScreenLocation(MouseEvent e) {
        Point cursor = e.getPoint();
        Point target_location = this.target.getLocationOnScreen();
        return new Point((int) (target_location.getX() + cursor.getX()),
                (int) (target_location.getY() + cursor.getY()));
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        this.start_drag = this.getScreenLocation(e);
        this.start_loc = this.getFrame(this.target).getLocation();
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        Point current = this.getScreenLocation(e);
        Point offset = new Point((int) current.getX() - (int) start_drag.getX(),
                (int) current.getY() - (int) start_drag.getY());
        JFrame frame = this.getFrame(target);
        Point new_location = new Point(
                (int) (this.start_loc.getX() + offset.getX()), (int) (this.start_loc
                .getY() + offset.getY()));
        frame.setLocation(new_location);
    }

    public void mouseMoved(MouseEvent e) {
    }
}
