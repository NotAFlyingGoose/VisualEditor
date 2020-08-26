package com.runningmanstudios.visualedit;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Visual extends JFrame {

    public Visual() {
        super("Visual Tests");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));
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
                .addInput(new Input(Input.STRING, "data")),
                new Codon("stop")};
        DragDropList<Codon> codons = new DragDropList<>();
        codons.setCAN_DROP_ON_SELF(false);
        codons.setOPEN_TO_OTHERS(false);
        codons.setREMOVE_CLONE(false);
        codons.addAll(availableCodons);
        codons.setBorder(codonsTitle);

        TitledBorder runnerTitle = new TitledBorder("Code");
        DragDropList<Codon> runner = new DragDropList<>();
        runner.setBorder(runnerTitle);
        runner.addDragDropListener(new DragDropListener() {
            @Override
            public void onDropAction(DragDropEvent e) {
                Codon code = (Codon) e.getSource();
                while (code.hasNextInput()) {
                    Input input = code.getNextUnfilledInput();
                    System.out.println(input.getId());
                    String newName = askForText("Parameter", "Please enter a "+input.getId()+" for the parameter " + input.getTitle());
                    input.setValue(newName);
                }
                System.out.println("drop " + code.getName());
            }

            @Override
            public void onDragAction(DragDropEvent e) {
                System.out.println("drag " + e.getSource().toString());
            }
        });

        sections.setDividerLocation(getWidth()/2);
        sections.setLeftComponent(runner);
        sections.setRightComponent(codons);

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
