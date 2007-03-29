/**
 * LLAInspectorFrame - A window into the wonderful world of LLAs.
 * 
 * @version 1.0.0
 * 
 * Copyright (C) 2006.  All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 */

package edu.indiana.cs.eac.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import edu.indiana.cs.eac.driver.*;


public class LLAInspectorFrame extends JFrame 
{   
    private Device driver;
    
    // HACK: Hook JEAC's ActionListener so we can tell it to update the UI on close
    private ActionListener callback;
    public static final int INSPECTOR_CLOSED = 100;
    
    //shared UI components
    private JTextField[] inValueLabels, outValueLabels;
    
    
   
    /**
     * Constructor - Instantiate a new LLAPanel.
     * 
     * @param Device driver - active EAC to use
     * 
     */
    public LLAInspectorFrame(Device driver, ActionListener callback)
    {
    	this.driver   = driver;
    	this.callback = callback;
    	
    	this.configureWindow();
    }
    
    
    
    
    
    
    
    /**
     * Configures the LLA reporting Window.
     *
     */
    private void configureWindow()
    {
    	JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("LLA Values (" + driver.getCurrentUnit() + ")"));
		panel.setLayout(new GridBagLayout());
		
		// set up constraints
		GridBagConstraints constraints = new GridBagConstraints();
		int gbc_row = 0;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.gridx   = 0;
		constraints.gridy   = gbc_row++;
		constraints.anchor  = GridBagConstraints.FIRST_LINE_START;

		// add labels
	   	JLabel blankLabel    = new JLabel();
	   	JLabel inputLabel    = new JLabel("IN");
	   	JLabel functionLabel = new JLabel("LLA");
	   	JLabel outputLabel   = new JLabel("OUT");
	   	
	   	panel.add(blankLabel, constraints);
	   	constraints.gridx = 1;
	   	
	   	panel.add(inputLabel, constraints);
	   	constraints.gridx = 2;

//	   	panel.add(functionLabel, constraints);
//	   	constraints.gridx = 3;

	   	panel.add(outputLabel, constraints);
	   	constraints.gridx = 0;
	   	constraints.gridy = gbc_row++;

	   	inValueLabels  = new JTextField[driver.getNumLLAs()];
	   	outValueLabels = new JTextField[driver.getNumLLAs()];
	   	for (int i = 0; i < driver.getNumLLAs(); i++)
	    {
	    	// channels are 0-based, make the label 1-based
	    	JLabel llaLabel   = new JLabel("LLA " + (i + 1) + ":  ");
	    	inValueLabels[i]  = new JTextField(6);
	    	outValueLabels[i] = new JTextField(6);
	    	inValueLabels[i].setEditable(false);
	    	outValueLabels[i].setEditable(false);

	    	// add components
	    	panel.add(llaLabel, constraints);
		   	constraints.gridx = 1;
	    	
	    	panel.add(inValueLabels[i], constraints);
		   	constraints.gridx = 2;

//	    	panel.add(new JLabel(new ImageIcon(JEAC.getImage("lla_20.gif"))), constraints);
//		   	constraints.gridx = 3;

		   	panel.add(outValueLabels[i], constraints);
		   	constraints.gridx = 0;
		   	constraints.gridy = gbc_row++;
	    }
    	getContentPane().add(panel);    	

	    // basic window configuration
		addWindowListener(new ExitListener());
		setIconImage(JEAC.getApplicationIcon());
		setLocationRelativeTo(super.getContentPane());
		setResizable(false);
		setSize(new Dimension(getPreferredSize().width + 50, getPreferredSize().height + 50));
		setTitle("LLA Inspector");
		setVisible(true);
    }
    
    /**
     * Refresh the inspector UI.
     * 
     * @param String[] inValues - new LLA input values
     * @param String[] outValues - new LLA output values
     *
     */
    public void refreshDisplay(String[] llaInValues, String[] llaOutValues)
    {
    	for (int i = 0; i < driver.getNumLLAs(); i++)
    	{
    		inValueLabels[i].setText(llaInValues[i]);
    		outValueLabels[i].setText(llaOutValues[i]);
    	}
    }
    
    
    
    /* ===============[ LISTENERS ]=============== */
    
    /**
     * Listen for the exit event.
     *
     */
    private class ExitListener extends WindowAdapter
	{
		public void windowClosing(WindowEvent e)
		{
			dispose();
			callback.actionPerformed(new ActionEvent(this, INSPECTOR_CLOSED, null));
		}
	}
}
