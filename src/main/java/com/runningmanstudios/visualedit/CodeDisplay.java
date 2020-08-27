package com.runningmanstudios.visualedit;

import com.runningmanstudios.visualedit.components.DragDropPanel;
import com.runningmanstudios.visualedit.tranfer.IDragDropOrigin;
import com.runningmanstudios.visualedit.tranfer.TransferPackage;
import sl.shapes.RoundPolygon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.Serializable;
import java.util.ArrayList;

public class CodeDisplay extends DragDropPanel implements MouseListener, MouseMotionListener {
    java.util.List<Codon> codons = new ArrayList<>();
    FontMetrics lastMetrics = null;

    public CodeDisplay() {
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setFont(new Font(g2d.getFont().getName(), Font.BOLD, 14));
        this.lastMetrics = g2d.getFontMetrics();

        for (Codon codon : codons) {
            //System.out.println(codon);
            int width = g2d.getFontMetrics().stringWidth(codon.getName())+10;
            int codonWidth = codon.getFullWidth(g2d.getFontMetrics());
            int codonHeight = g2d.getFontMetrics().getHeight() + 10;
            g2d.setColor(codon.color);
            RoundRectangle2D.Double codonShape = new RoundRectangle2D.Double(codon.x, codon.y, codonWidth, codonHeight, 10, 10);
            g2d.fill(codonShape);

            for (CodonInput input : codon.getInputs()) {
                int inputWidth = input.getFullWidth(g2d.getFontMetrics());
                g2d.setColor(input.color);
                g2d.fillRoundRect((int) codonShape.x+width, (int) codonShape.y+5, inputWidth, (int) codonShape.height-10, 10, 10);
                g2d.setColor(Color.white);
                g2d.drawString((input.isFilled()?input.getValue():input.getId()+" "+input.getTitle()).toString(), (int) codonShape.x+5+width, (int) codonShape.y+g2d.getFontMetrics().getHeight());
                width += inputWidth;
            }
            int triSize = codonHeight/3;
            //bottom stick
            if (codon.getNextCode()==null) {
                g2d.setColor(codon.color);
                g2d.fill(createRoundTriangle((int) codonShape.x + 5, (int) (codonShape.y + codonShape.height - 2), triSize, 1));
            }
            //top hole
            if (codon.getLastCode()==null) {
                g2d.setColor(darken(codon.color, 20));
                g2d.fill(createRoundTriangle((int) codonShape.x + 5, (int) (codonShape.y), triSize, 1));
            }

            g2d.setColor(Color.white);
            g2d.drawString(codon.getName(), codon.x+5, codon.y + g2d.getFontMetrics().getHeight());
        }

        g.dispose();
    }

    public Color darken(Color c, int amt) {
        return new Color(Math.max(c.getRed()-amt, 0), Math.max(c.getGreen()-amt, 0), Math.max(c.getBlue()-amt, 0), c.getAlpha());
    }

    public RoundPolygon createRoundTriangle(int x, int y, int size, int arcWidth) {
        Polygon connector = new Polygon();
        connector.addPoint(x, y); // top right
        connector.addPoint(x + size, y); // top left
        connector.addPoint(x + (size/2), y + size); // middle
        return new RoundPolygon(connector, arcWidth);
    }

    @Override
    public void receive(Point mousePos, TransferPackage data, IDragDropOrigin originator) {
        assert data.getSource() instanceof Codon;
        Codon codon = (Codon) data.getSource();
        if (codon.getName().equals("start")) {
            for (Codon inCode : codons) {
                if (inCode.getName().equals("start")) {
                    JOptionPane.showMessageDialog(this,
                            "There is already a start codon.",
                            "Codon Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
        SwingUtilities.convertPointFromScreen(mousePos, this);
        codon.x = mousePos.x - 10;
        codon.y = mousePos.y - 10;
        codons.add(codon);
        repaint();
    }

    @Override
    public boolean canReceive(Point mousePos, TransferPackage data, TransferHandler.TransferSupport support) {
        repaint();
        return super.canReceive(mousePos, data, support);
    }

    @Override
    public Serializable request(Point mousePos) {
        repaint();
        return super.request(mousePos);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println("done");
        for (Codon codon : codons) {
            int width = lastMetrics.stringWidth(codon.getName()) + 10;
            int codonWidth = codon.getFullWidth(lastMetrics);
            int codonHeight = lastMetrics.getHeight() + 10;

            for (CodonInput input : codon.getInputs()) {
                width += input.getFullWidth(lastMetrics);
            }

            RoundRectangle2D.Double codonShape = new RoundRectangle2D.Double(codon.x, codon.y, width, codonHeight, 10, 10);

            for (Codon otherCodon : codons) {
                int oWidth = lastMetrics.stringWidth(codon.getName()) + 10;
                int oCodonWidth = otherCodon.getFullWidth(lastMetrics);
                int oCodonHeight = lastMetrics.getHeight() + 10;

                for (CodonInput input : codon.getInputs()) {
                    oWidth += input.getFullWidth(lastMetrics);
                }

                RoundRectangle2D.Double oCodonShape = new RoundRectangle2D.Double(otherCodon.x, otherCodon.y, oWidth, oCodonHeight, 10, 10);

                if (areClose(codonShape.x, oCodonShape.x, 10) && areClose(codonShape.y + codonShape.height, oCodonShape.y, 10)) {
                    System.out.println(codon + " is close to " + otherCodon);
                    codon.setNextCode(otherCodon);
                    otherCodon.setLastCode(codon);
                    otherCodon.x = codon.x;
                    otherCodon.y = (int) (codon.y + codonShape.height);
                }
            }
        }

        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        for (Codon codon : codons) {
            int width = lastMetrics.stringWidth(codon.getName()) + 10;
            int codonWidth = codon.getFullWidth(lastMetrics);
            int codonHeight = lastMetrics.getHeight() + 10;

            for (CodonInput input : codon.getInputs()) {
                width += input.getFullWidth(lastMetrics);
            }

            RoundRectangle2D.Double codonShape = new RoundRectangle2D.Double(codon.x, codon.y, width, codonHeight, 10, 10);

            if (codonShape.contains(e.getPoint())) {
                int newX = (int) (e.getX() - (codonShape.width/2));
                int newY = (int) (e.getY() - (codonShape.height/2));
                codon.x = newX;
                codon.y = newY;
                Codon next = codon.getNextCode();
                while (next != null) {

                }
                break;
            }
        }
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        repaint();
    }

    public boolean areClose(double v1, double v2, double t) {
        return (v2 > (v1-t) && v2 < (v1+t));
    }

}
