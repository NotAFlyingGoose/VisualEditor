package com.runningmanstudios.visualedit;

import com.runningmanstudios.visualedit.components.DragDropPanel;
import com.runningmanstudios.visualedit.tranfer.IDragDropOrigin;
import com.runningmanstudios.visualedit.tranfer.TransferPackage;
import sl.shapes.RoundPolygon;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.Serializable;
import java.util.ArrayList;

public class CodeDisplay extends DragDropPanel {
    java.util.List<Codon> codons = new ArrayList<>();

    public CodeDisplay() {

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setFont(new Font(g2d.getFont().getName(), Font.BOLD, 14));

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
}
