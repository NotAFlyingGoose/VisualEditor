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
import java.util.Collections;

public class CodeDisplay extends DragDropPanel implements MouseListener, MouseMotionListener {
    java.util.List<Codon> codons = new ArrayList<>();
    FontMetrics lastMetrics = null;
    Point start_drag;
    Point start_loc;
    Codon selected;

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

        java.util.List<Codon> reversedCodons = new ArrayList<>(codons);
        Collections.reverse(reversedCodons);
        for (Codon codon : reversedCodons) {
            int width = g2d.getFontMetrics().stringWidth(codon.getName())+10;
            int codonWidth = codon.getFullWidth(g2d.getFontMetrics());
            int codonHeight = g2d.getFontMetrics().getHeight() + 10;
            //if (codon instanceof BigBoiCodon) codonHeight*=((BigBoiCodon) codon).getInners().size()+2;
            Color codonColor = (codon.bright?darken(codon.color, -10):codon.color);
            RoundRectangle2D.Double codonShape = new RoundRectangle2D.Double(codon.x, codon.y, codonWidth, codonHeight, 10, 10);
            if (codon.bright) {
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fill(new RoundRectangle2D.Double(codon.x - 5, codon.y + 5, codonWidth, codonHeight, 10, 10));
            }
            g2d.setColor(codonColor);
            g2d.fill(codonShape);

            for (CodonInput input : codon.getInputs()) {
                int inputWidth = input.getFullWidth(g2d.getFontMetrics());
                g2d.setColor(input.color);
                g2d.fillRoundRect((int) codonShape.x+width, (int) codonShape.y+5, inputWidth, (int) codonShape.height-10, 10, 10);
                g2d.setColor(Color.white);
                g2d.drawString((input.isFilled()?input.getValue():input.getId()+" "+input.getTitle()).toString(), (int) codonShape.x+5+width, (int) codonShape.y+g2d.getFontMetrics().getHeight());
                width += inputWidth;
            }
            int codonX = (int) codonShape.x;
            int codonY = (int) codonShape.y;
            if (codon instanceof BigBoiCodon) {
                for (Codon inlineCodon : ((BigBoiCodon) codon).getInnerCodonList()) {
                    inlineCodon.bright = true;
                    codonY += getCodonRect(inlineCodon).getY();
                }
                codonY += codonShape.getHeight() + (codonShape.getHeight()/1.5);
                g2d.setColor(codonColor);
                RoundRectangle2D.Double sidebar = new RoundRectangle2D.Double(codonShape.x, codonShape.y - 10 + codonShape.height, 10, codonY - codonShape.y + 10, codonShape.arcwidth, codonShape.archeight);
                RoundRectangle2D.Double bottombar = new RoundRectangle2D.Double(codonX, codonY + codonShape.getHeight()/2, lastMetrics.stringWidth(codon.getName()) + 20, codonShape.getHeight()/2, codonShape.arcwidth, codonShape.archeight);
                g2d.fill(sidebar);
                g2d.fill(bottombar);
            }

            int triSize = codonHeight/3;
            //inner stick in big boi codons
            if (codon instanceof BigBoiCodon && ((BigBoiCodon) codon).getInnerCodon()==null) {
                g2d.setColor(codonColor);
                g2d.fill(createRoundTriangle(codonX + 15, (int) (codonShape.y + codonShape.height - 2), triSize, 1));
            }
            //bottom stick
            if (codon.getNextCode()==null) {
                g2d.setColor(codonColor);
                g2d.fill(createRoundTriangle(codonX + 5, (int) (codonY + codonShape.height - 2), triSize, 1));
            }
            //top hole
            if (codon.getLastCode()==null) {
                g2d.setColor(darken(codonColor, 20));
                g2d.fill(createRoundTriangle((int) codonShape.getX() + 5, (int) (codonShape.getY()), triSize, 1));
            }

            g2d.setColor(Color.white);
            g2d.drawString(codon.getName(), codon.x+5, codon.y + g2d.getFontMetrics().getHeight());
        }

        g.dispose();
    }

    public static Color darken(Color c, int amt) {
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
        codon.x = mousePos.x;
        codon.y = mousePos.y;
        codons.add(0, codon);
        repaint();
    }

    @Override
    public boolean canReceive(Point mousePos, TransferPackage data, TransferHandler.TransferSupport support) {
        repaint();
        return true;
    }

    @Override
    public Serializable request(Point mousePos) {
        repaint();
        return null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.start_drag = e.getPoint();
        for (Codon codon : codons) {

            RoundRectangle2D codonShape = getCodonRect(codon);

            if (codonShape.contains(this.start_drag)) {
                this.start_loc = new Point((int) codonShape.getX(), (int) codonShape.getY());
                this.selected = codon;
                return;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.selected = null;
        for (Codon codon : codons) {
            codon.bright = false;

            RoundRectangle2D codonShape = getCodonRect(codon);

            for (Codon otherCodon : codons) {

                RoundRectangle2D oCodonShape = getCodonRect(otherCodon);
                int botSub = 0;
                if (codon instanceof BigBoiCodon) {
                    if (areClose(codonShape.getX() + 10, oCodonShape.getX(), 10) && areClose(codonShape.getY() + codonShape.getHeight() + botSub, oCodonShape.getY(), 10)) {
                        otherCodon.setLastCode(null);
                        ((BigBoiCodon) codon).setInner(otherCodon);
                        otherCodon.setLastCode(codon);
                        otherCodon.x = codon.x + 10;
                        otherCodon.y = (int) (codon.y + codonShape.getHeight() + botSub);
                        break;
                    }
                    for (Codon inlineCodon : ((BigBoiCodon) codon).getInnerCodonList()) {
                        botSub += getCodonRect(inlineCodon).getY();
                    }
                    botSub += codonShape.getHeight() + (codonShape.getHeight()/1.5);
                }

                if (areClose(codonShape.getX(), oCodonShape.getX(), 10) && areClose(codonShape.getY() + codonShape.getHeight() + botSub, oCodonShape.getY(), 10)) {
                    codon.setNextCode(otherCodon);
                    otherCodon.setLastCode(codon);
                    otherCodon.x = codon.x;
                    otherCodon.y = (int) (codon.y + codonShape.getHeight() + botSub);
                    break;
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
        Point offset = new Point(e.getX() - (int) start_drag.getX(),
                e.getY() - (int) start_drag.getY());
        Point new_location = new Point(
                (int) (this.start_loc.getX() + offset.getX()), (int) (this.start_loc
                .getY() + offset.getY()));
        selected.x = new_location.x;
        selected.y = new_location.y;

        if (selected.getLastCode() != null) {
            selected.getLastCode().setNextCode(null);
            selected.setLastCode(null);
        }
        selected.bright = true;
        RoundRectangle2D bounds = getCodonRect(selected);
        Codon next = selected.getNextCode();
        while (next != null) {
            next.x = new_location.x;
            next.y = new_location.y += bounds.getHeight();
            next.bright = true;
            bounds = getCodonRect(next);
            if (next instanceof BigBoiCodon) {
                int codonX = (int) bounds.getX();
                int codonY = (int) bounds.getY();
                for (Codon inlineCodon : ((BigBoiCodon) next).getInnerCodonList()) {
                    codonY += getCodonRect(inlineCodon).getY();
                }
                codonY += bounds.getHeight() + (bounds.getHeight()/1.5);
                RoundRectangle2D.Double sidebar = new RoundRectangle2D.Double(bounds.getX(), bounds.getY() - 10 + bounds.getHeight(), 10, codonY - bounds.getY() + 10, bounds.getArcWidth(), bounds.getArcHeight());
                RoundRectangle2D.Double bottombar = new RoundRectangle2D.Double(codonX, codonY + bounds.getHeight()/2, lastMetrics.stringWidth(next.getName()) + 20, bounds.getHeight()/2, bounds.getArcWidth(), bounds.getArcHeight());

                bounds = new RoundRectangle2D.Double(bounds.getX(), bounds.getY(), bottombar.width, bottombar.getHeight() + sidebar.getHeight(), 10, 10);
            }
            next = next.getNextCode();
        }
        repaint();
    }

    public RoundRectangle2D getCodonRect(Codon codon) {
        int codonWidth = lastMetrics.stringWidth(codon.getName()) + 10;
        int codonHeight = lastMetrics.getHeight() + 10;

        for (CodonInput input : codon.getInputs()) {
            codonWidth += input.getFullWidth(lastMetrics);
        }

        return new RoundRectangle2D.Double(codon.x, codon.y, codonWidth, codonHeight, 10, 10);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        repaint();
    }

    public boolean areClose(double v1, double v2, double t) {
        return (v2 > (v1-t) && v2 < (v1+t));
    }

}
