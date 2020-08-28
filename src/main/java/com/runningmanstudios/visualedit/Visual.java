package com.runningmanstudios.visualedit;

import com.runningmanstudios.visualedit.components.DragDropList;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.Serializable;

public class Visual extends JFrame implements Serializable {

    public Visual() {
        super("Visual Tests");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));
        setSize(800, 600);
        setLayout(null);


        JSplitPane sections = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        addComponentListener(new ComponentAdapter()
        {
            public void componentResized(ComponentEvent evt) {
                Component c = (Component)evt.getSource();
                sections.setBounds(0, 0, getWidth(), getHeight());
            }
        });

        TitledBorder codonsTitle = new TitledBorder("Codons");
        Codon[] availableCodons = new Codon[] {
                new Codon("start"),
                new Codon("print")
                        .addInput(new CodonInput(CodonInput.STRING, "data")),
                new Codon("math")
                        .addInput(new CodonInput(CodonInput.NUMBER, "value1"))
                        .addInput(new CodonInput(CodonInput.DROPDOWN, "operation"))
                        .addInput(new CodonInput(CodonInput.NUMBER, "value2")),
                new Codon("concatenate")
                        .addInput(new CodonInput(CodonInput.STRING, "value1"))
                        .addInput(new CodonInput(CodonInput.STRING, "value2")),
                new Codon("boolean condition")
                        .addInput(new CodonInput(CodonInput.BOOLEAN, "value1"))
                        .addInput(new CodonInput(CodonInput.DROPDOWN, "operation"))
                        .addInput(new CodonInput(CodonInput.BOOLEAN, "value2")),
                new BigBoiCodon("if", new Color(155, 55, 155))
                        .addInput(new CodonInput(CodonInput.BOOLEAN, "condition")),
                new Codon("stop")};
        DragDropList<Codon> codons = new DragDropList<>();
        codons.setCAN_DROP_ON_SELF(false);
        codons.setOPEN_TO_OTHERS(false);
        codons.setREMOVE_CLONE(false);
        codons.addAll(availableCodons);
        codons.setBorder(codonsTitle);
        codons.setFont(new Font(codons.getFont().getName(), codons.getFont().getStyle(), 20));
        JScrollPane codonsScroll = new JScrollPane(codons);

        TitledBorder runnerTitle = new TitledBorder("Code");
        CodeDisplay runner = new CodeDisplay();
        runner.setBorder(runnerTitle);
        JScrollPane runnerScroll = new JScrollPane(runner);

        sections.setDividerLocation((int)((3.0/4.0) * getWidth()));
        sections.setLeftComponent(runnerScroll);
        sections.setRightComponent(codonsScroll);

        add(sections);

        setVisible(true);
    }

    public String askForText(String title, String text) {
        String s = JOptionPane.showInputDialog(
                this,
                text,
                title,
                JOptionPane.PLAIN_MESSAGE);
        if (s != null) {
            if (s.length() > 0) {
                return s;
            }

            //custom title, error icon
            JOptionPane.showMessageDialog(this,
                    "You must provide text",
                    title,
                    JOptionPane.ERROR_MESSAGE);
        }
        return "";
    }

    public static void main(String[] args) {
        new Visual();
    }
}
