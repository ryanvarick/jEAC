package edu.indiana.cs.testing.ui;

import java.io.*;
import javax.swing.*;
import java.awt.*;

public class TextFrame2 extends JInternalFrame {
    private JTextArea textArea = new JTextArea();
    private JScrollPane scrollPane = new JScrollPane();

    public TextFrame2(File file) {
        setSize(200,300);
        setTitle(file.getName());
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        setResizable(true);
        String yo = "";
        
        // very inefficient!!
        try
        {
        	FileReader r = new FileReader(file);
        	BufferedReader b = new BufferedReader(r);
        	
        	String l;
        	while((l = b.readLine()) != null)
        	{	
        		yo = yo + l + "\n";
        		System.out.println(l);
        	}
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        
        

        System.out.println("Setting text to:  " + yo);
        
        textArea.setText(yo);
        textArea.setCaretPosition(0); 
        
        scrollPane.getViewport().add(textArea);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollPane,BorderLayout.CENTER);
        
        
        
        
      
    }
}