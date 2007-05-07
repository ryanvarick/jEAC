/**
 * ControlPanel.java - Node controls.
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

package edu.indiana.cs.eac.ui.deprecated;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import edu.indiana.cs.eac.exceptions.*;
import edu.indiana.cs.eac.hardware.*;
import edu.indiana.cs.eac.ui.deprecated.*;

public class ControlFrame extends JFrame
{
	private static final int FRAME_WIDTH  = 300;
	private static final int FRAME_HEIGHT = 140;
	
	private int    NUM_CONNECTION_TYPES;
	private int    NUM_OF_LLA_FUNCTIONS;
	private int    MIN_CURRENT;
	private int    MAX_CURRENT;
	private String CURRENT_UNIT;
	
	private Device driver;
	private NodeMap nodemap;
	private int[] coords;
    
    // hack: used globally :-(
    private JRadioButton[] radioButtons; 
	private JSlider currentSlider;
	private JTextField currentTextBox;
	private JComboBox llaListBox;
	private JEACNode node;
	private JToggleButton button;
		
	
	
	/**
	 * Constructor - Caches the settings and sets up some globals, then calls configure.
	 * 
	 * @param NodeMap
	 * @param JToggleButton - current button
	 * @param JEACNode - cooresponding node
	 * @param int[] - x-y coordinates of the button
	 * 
	 */
	public ControlFrame(NodeMap nodemap, JToggleButton button, JEACNode node, int[] coordinates) 
	{
		// cache the configuration
		this.nodemap = nodemap;
		this.button  = button;
		this.coords  = coordinates;
		this.driver  = nodemap.driver;
		this.node    = node;
		this.node.setChanging(true);

		NUM_CONNECTION_TYPES = driver.getNumConnectionTypes();
		NUM_OF_LLA_FUNCTIONS = driver.getNumLLAFunctions();
		
		MIN_CURRENT  = driver.getMinCurrent();
		MAX_CURRENT  = driver.getMaxCurrent();
		CURRENT_UNIT = driver.getCurrentUnit();

		configureFrame();
	}
	
	/**
	 * Configure the frame.
	 *
	 */
	private void configureFrame()
	{
		// set up the basic layout (GridBagLayout, ugh)
		Container container = getContentPane();
		container.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.gridx   = 0;
		constraints.gridy   = 0;
		constraints.anchor  = GridBagConstraints.FIRST_LINE_START;
		constraints.fill    = GridBagConstraints.HORIZONTAL;

		// add the radio button JPanel to the frame
		container.add(buildRadioButtonPanel(), constraints);
		
		// configure for sources and sinks
		String title = "Configuring " + node.getType();
		if(node.isType(JEACNode.SOURCE) || node.isType(JEACNode.SINK))
		{
			constraints.gridy  = 1;
			container.add(buildDacPanel(), constraints);
		}
		
		// configure for LLAs
		else if (node.isType(JEACNode.LLA))
		{
			constraints.gridy  = 1; 
			container.add(buildLlaPanel(), constraints);
		}
		
		// configure for OFF
		else
		{
			title = "OFF";
		}

		// basic window configuration
		addWindowListener(new ExitlListener());
		setIconImage(JEAC.getApplicationIcon());
		setLocationRelativeTo(super.getContentPane());
		setResizable(false);
		setSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		setTitle("Pin (" + 
				new Integer(coords[0]).toString() + ", " + 
				new Integer(coords[1]).toString() + "):  " + title);
		setVisible(true);
	}

	/**
	 * Populate radio button panel.
	 * 
	 * @return JPanel - JPanel containing component types
	 *
	 */
	private JPanel buildRadioButtonPanel()
	{
		JPanel radioButtonPanel = new JPanel(new GridLayout(1, NUM_CONNECTION_TYPES));
		radioButtonPanel.setBorder(BorderFactory.createTitledBorder("Connection Type"));

		// add the radio buttons
		ButtonGroup radioButtonGroup = new ButtonGroup();
		this.radioButtons = new JRadioButton[NUM_CONNECTION_TYPES];
		for (int i = 0; i < radioButtons.length; i++)
		{
			radioButtons[i] = new JRadioButton();
			radioButtons[i].setMnemonic(KeyEvent.VK_1 + i);		// NEW: (1476434) - added for v1.0.4
			radioButtons[i].addActionListener(new RadioButtonListener());
			radioButtonGroup.add(radioButtons[i]);
			radioButtonPanel.add(radioButtons[i]);
		}
		
		radioButtons[0].setText(JEACNode.SOURCE);
		radioButtons[1].setText(JEACNode.SINK);
		radioButtons[2].setText(JEACNode.LLA);
		radioButtons[3].setText(JEACNode.OFF);
		
		// select the appropriate button according to the current state
		this.setSelectedRadioButton();
		
		return radioButtonPanel;
	}
	
	/**
	 * Returns controls to manipulate sources and sinks
	 * 
	 * @return Component - JPanel containing source and sink controls
	 * 
	 */
	private Component buildDacPanel()
	{
		// create a new JPanel with Flowlayout for the controls
		JPanel widgetPanel = new JPanel(new FlowLayout());
		widgetPanel.setBorder(BorderFactory.createTitledBorder("Update Current (" + CURRENT_UNIT + ")"));
		
		// read the node's current value
		double initialValue = node.getValue();

		// set up the slider
		currentSlider  = new JSlider(MIN_CURRENT, MAX_CURRENT);
		currentSlider.setMajorTickSpacing((int)Math.floor(MAX_CURRENT / 4));
		currentSlider.setMinorTickSpacing((int)Math.floor(MAX_CURRENT / 20));
		currentSlider.setPaintTicks(true);
		currentSlider.setSnapToTicks(false);
		currentSlider.setValue((int)initialValue);
		currentSlider.addChangeListener(new SliderChangeListener());
		
		// set up the text box
		currentTextBox = new JTextField(Integer.toString(currentSlider.getValue()));
		currentTextBox.setColumns(4);
		currentTextBox.addActionListener(new TextBoxChangeListener());
		
		widgetPanel.add(currentSlider);
		widgetPanel.add(currentTextBox);

		return widgetPanel;
	}
	
	/**
	 * Returns controls to manipulate an LLA.
	 * 
	 * @return Component - JPanel containing LLA controls
	 * 
	 */
	private Component buildLlaPanel()
	{
		JPanel widgetPanel = new JPanel();
		widgetPanel.setBorder(BorderFactory.createTitledBorder("Update LLA Function"));
		widgetPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		// allocate a large enough combo box for now
		String[] placeholder = new String[NUM_OF_LLA_FUNCTIONS];
		for(int i = 0; i < placeholder.length; i++) { placeholder[i] = new Integer(i).toString(); }
		
		llaListBox = new JComboBox(placeholder);
		llaListBox.setRenderer(new LLAListBoxRenderer());
		llaListBox.setSelectedIndex((int) node.getValue());			
		llaListBox.setMaximumRowCount(5);
		llaListBox.addActionListener(new LLAFunctionChangeListener());

		widgetPanel.add(llaListBox);

		return widgetPanel;
	}
	
	/**
	 * Reinitialize the frame based on user interaction.
	 *
	 */
	private void reconfigureControlPanel()
	{
		// update the nodemap
		nodemap.updateNodeMapButton(button, node.getType());
		
		// create and log the new frame
		ControlFrame temp = new ControlFrame(nodemap, button, node, coords);
		temp.setLocation(this.getLocationOnScreen());
		
		// remove the old frame
		nodemap.removeObjectFromCollection(this);
		nodemap.addObjectToCollection(temp);
		dispose();
	}
	
	/**
	 * I'm not sure what this is for.
	 *
	 */
	private void allowNewFrames() {
		node.setChanging(false);
		nodemap.removeObjectFromCollection(this);
	}
	
	/**
	 * Match the state of the node to the appropriate radio button.
	 *
	 */
	private void setSelectedRadioButton()
	{
		if(node.isType(JEACNode.SOURCE)) {
			radioButtons[0].setSelected(true);
		}
		if(node.isType(JEACNode.SINK)) {
			radioButtons[1].setSelected(true);
		}
		if(node.isType(JEACNode.LLA)) {
			radioButtons[2].setSelected(true);
		}
		if(node.isType(JEACNode.OFF)) {
			radioButtons[3].setSelected(true);
		}
	}
	

	
	/* =========================[ LISTENERS ]========================= */ 
		
	/**
	 * Handles ControlPanel close events.
	 *
	 */
	private class ExitlListener extends WindowAdapter
	{
		public void windowClosing(WindowEvent e)
		{
			allowNewFrames();
		}
	}
	
	/**
	 * Handles LLA function changes.
	 * 
	 */
	private class LLAFunctionChangeListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			int value = llaListBox.getSelectedIndex();

			if(nodemap.driver.changeNode(node.getType(), (double) value, node))
			{
				node.setValue(value);
			}
		}
	}

	/**
	 * Handles radio button interaction (node changes).
	 * 
	 */
	public class RadioButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			Object source = e.getSource();
			
			// figure out what was pressed and call the appropriate internal handler
			if(     source == radioButtons[0] && !(node.isType(JEACNode.SOURCE))) { handleOnState(JEACNode.SOURCE); }
			else if(source == radioButtons[1] && !(node.isType(JEACNode.SINK)))   { handleOnState(JEACNode.SINK); }
			else if(source == radioButtons[2] && !(node.isType(JEACNode.LLA)))    { handleOnState(JEACNode.LLA); }
			else if(source == radioButtons[3] && !(node.isType(JEACNode.OFF)))    { handleOff(); }
			else { /* User clicked the same radio button again, ignore. */ }
		}
		
		private void handleOnState(String type)
		{
			try
			{
				if(nodemap.driver.changeNode(type, node.getValue(), node))
				{
					node.setType(type);
					node.setValue(0);
					reconfigureControlPanel();
				}
				else
				{
					setSelectedRadioButton();
				}
			}
			catch (InvalidTypeException ea)
			{
				setSelectedRadioButton();
			}
		}
		
		private void handleOff()
		{
			try
			{
				if (nodemap.driver.changeNode(JEACNode.OFF, node.getValue(), node))
				{
					node.setType(JEACNode.OFF);
					reconfigureControlPanel();
				}
				else
				{
					setSelectedRadioButton();
				}
			}
			catch (InvalidTypeException ea) 
			{
				setSelectedRadioButton();
			}
		}
	}
	
	/**
	 * Handles slider events (from SRC/SNK nodes).
	 * 
	 */
	private class SliderChangeListener implements ChangeListener
	{
		public void stateChanged(ChangeEvent e)
		{
			// ignore events until the user is done moving the slider
			if(e.getSource() == currentSlider && !(currentSlider.getValueIsAdjusting()))
			{
				if(nodemap.driver.changeNode(node.getType(), currentSlider.getValue(), node))
				{
					node.setValue(currentSlider.getValue());
					currentTextBox.setText(Integer.toString(currentSlider.getValue()));
				}
				else
				{
					currentSlider.setValue((int)node.getValue());
				}
			}
		}
	}
	
	/**
	 * Listens for user changes in the slider text box.
	 * 
	 */
	private class TextBoxChangeListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			double newValue, rawInput;
			
			try
			{
				// read the text box
				rawInput = Double.parseDouble(currentTextBox.getText().trim());
				
				// sanity check the input
				if(rawInput > MAX_CURRENT) { newValue = MAX_CURRENT; }
				else if(rawInput < MIN_CURRENT) { newValue = MIN_CURRENT; }
				else newValue = rawInput;
			}
			catch(NumberFormatException f)
			{
				newValue = MIN_CURRENT;
			} 
	
			 // type stayed the same, value changed
			if(nodemap.driver.changeNode(node.getType(), newValue, node))
			{
				node.setValue(newValue);
				currentSlider.setValue((int)newValue);
				currentTextBox.setText(Integer.toString((int)newValue));
			}
			else
			{
				currentSlider.setValue((int)node.getValue());
				currentTextBox.setText(Integer.toString((int)node.getValue()));
			}
		}
	}
}