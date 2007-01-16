package edu.indiana.cs.eac.testing.ui;

import javax.swing.*;
import java.awt.*;

public class TextFrame extends JInternalFrame {
    private JTextArea textArea = new JTextArea();
    private JScrollPane scrollPane = new JScrollPane();

    public TextFrame() {
        setSize(200,300);
        setTitle("Edit Text");
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        setResizable(true);
        scrollPane.getViewport().add(textArea);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollPane,BorderLayout.CENTER);
    }
}