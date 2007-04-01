/**
 * NodeMap.java - Self-contained JPanel that packages EAC controls.
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
import java.io.*;
import java.util.*;
import javax.swing.*;

import edu.indiana.cs.eac.exceptions.*;
import edu.indiana.cs.eac.hardware.*;

public class NodeMap extends JPanel implements ActionListener, Serializable
{
	// button spacing
	private static final int SPACING_X = 0;
	private static final int SPACING_Y = 0;
	
	// button size
	private static final int SIZE_X = 40;
	private static final int SIZE_Y = 30;
	
	private int numNodes;
	private JToggleButton buttonArray[];
	private JEACNode nodes[];
	private ControlFrame temp;
	private Vector controlFrameContainer;
	public Device driver;
	
	
	
	/**
	 * Constructor - builds the nodemap control JPanel.
	 * 
	 * @param driver - The active EAC driver.
	 * 
	 */
	public NodeMap(Device driver)
	{
		// cache the driver
		this.driver = driver;

		// setup the UI
		setBorder(BorderFactory.createTitledBorder("Connection Manager"));		
		setLayout(new GridLayout(driver.getNumCols(), driver.getNumRows(), SPACING_Y, SPACING_X));
		
		// set up some default values
		this.numNodes    = driver.getNumRows() * driver.getNumCols();
		this.buttonArray = new JToggleButton[numNodes];
		this.nodes       = driver.getJEACNodes();
		this.controlFrameContainer = new Vector();
		
		// add buttons to the panel
		for(int i = 0; i < numNodes; i++)
		{
			int[] index = getIndex(i);
			
			String buttonTitle = index[0] + "x" + index[1];
			
			buttonArray[i] = new JToggleButton(buttonTitle);
			buttonArray[i].setFont(new Font("Arial", Font.PLAIN, 9));
			
			// margins and size
			buttonArray[i].setMargin(new Insets(0, 0, 0, 0));
			buttonArray[i].setPreferredSize(new Dimension(SIZE_X, SIZE_Y));
				
			// set text
			buttonArray[i].setHorizontalTextPosition(AbstractButton.LEADING);
			buttonArray[i].setIcon(new BlankIcon(9, 9));
			
			buttonArray[i].addActionListener(this);
			this.add(buttonArray[i]);
		}
	}
	
	
	
	
	
	
	/* --------------------[ UPDATE METHODS ]-------------------- */ 
	
	/**
	 * Updates a button on the NodeMap, usually in response to changes invoked 
	 *   by the ControlPanel window.
	 * 
	 * @param JToggleButton button - nodemap button to update
	 * @param String type - new node type (expected: JEACNode constant)
	 * 
	 */
	public void updateNodeMapButton(JToggleButton button, String type)
	{
		int[] index = getIndex(button);
		
		if(type == JEACNode.SOURCE)
		{
			button.setIcon(new ImageIcon(JEAC.getImage("icon_source.gif")));
			button.getModel().setSelected(true);
		}
		else if(type == JEACNode.SINK)
		{
			button.setIcon(new ImageIcon(JEAC.getImage("icon_sink.gif")));			
			button.getModel().setSelected(true);
		}
		else if(type == JEACNode.LLA)
		{
			button.setIcon(new ImageIcon(JEAC.getImage("icon_lla.gif")));
			button.getModel().setSelected(true);
		}
		else if(type == JEACNode.OFF)
		{
			button.setIcon(new BlankIcon(9,9));
			button.getModel().setSelected(false);
		}
				
		button.setText(index[0] + "x" + index[1]);
	}
	
	/**
	 * Resets all of the nodemap buttons to OFF.
	 *
	 */
	public void reset()
	{
		// turn off all buttons
		for(int i = 0; i < buttonArray.length; i++)
		{
			updateNodeMapButton(buttonArray[i], JEACNode.OFF);
		}
		
		// dispose of all control frames
		clearCollection();
		
		// finally allocate a fresh set of nodes :-)
		for(int i = 0; i < nodes.length; i++)
		{
			nodes[i] = new JEACNode();
		}
	}
	
	
	
	
	
	
	
	/* --------------------[ UTILITY METHODS ]-------------------- */
	
	/**
	 * Given an index, returns its x-y coordinate.
	 * 
	 * @param int - index
	 * @return int[][] - x-y coordinates
	 * 
	 */
	private int[] getIndex(int index)
	{
		return new int[]
		    {
				((int) Math.floor(index / driver.getNumRows())) + 1,
				(index % driver.getNumRows()) + 1
		    };
	}
	
	/**
	 * Given a button on the nodemap, returns itx x-y coordinate.
	 * 
	 * @param JToggleButton - Button on the nodemap
	 * @return int[] - x-y coordinates
	 * 
	 */
	public int[] getIndex(JToggleButton button)
	{
		for (int i = 0; i < numNodes; i++)
		{
			if (button == buttonArray[i]) { return getIndex(i); }
		}
		return null;
	}
	
	/**
	 * Returns the JEACNode from the nodes array that corresponds with the button
	 *  pressed by the user.
	 * 
	 * @param JToggleButton button - button that was pressed
	 * @return JEACNode node - node on the nodemap
	 * 
	 * @throws NodeNotFoundException
	 * 
	 */
	private JEACNode getJEACNode(JToggleButton button) throws NodeNotFoundException {
		for (int i = 0; i < numNodes; i++) {
			if (button == buttonArray[i])
				return nodes[i];
		}
		
		throw new NodeNotFoundException("getJEACNode");
	}
	
	/**
	 * Resets the JToggleButton array based on the values stored in the the driver's
	 *  JEACNode array.  
	 *  
	 */
	public void reload()
	{
		JEACNode nodeArray[] = driver.getJEACNodes();
		for(int i = 0; i < numNodes; i++)
		{
			updateNodeMapButton(buttonArray[i], nodeArray[i].getType());
		}
	}
	
	
	
	
	
	
	
	/* --------------------[ BOOKKEEPING METHODS ]--------------------*/
	
	// These methods keep track of instantiated ControlFrames, so that they
	//  may be closed en-masse on driver disconnect.
	
	public void addObjectToCollection(Object o)
	{
		controlFrameContainer.add(o);
	}

	public void clearCollection()
	{
		Object[] collection = controlFrameContainer.toArray();
		for (int i = 0; i < collection.length; i++)
			((JFrame) collection[i]).dispose();
		controlFrameContainer.clear();
	}
	
	public void removeObjectFromCollection(ControlFrame frame)
	{
		controlFrameContainer.remove(frame);
	}

	
	
	
	
	
	
	/* --------------------[ LISTENERS ]-------------------- */
	
	/**
	 * Listens for clicks on nodemap buttons.
	 * 
	 */
	public void actionPerformed(ActionEvent e)
	{
		JToggleButton button = (JToggleButton) e.getSource();
		button.getModel().setSelected(! button.getModel().isSelected());
		
		try
		{
			JEACNode node = getJEACNode(button);
			int coordinates[] = getIndex(button);

			if (!node.isChanging())
			{
				temp = new ControlFrame(this, button, node, coordinates);
				controlFrameContainer.add(temp);
			}
		}
		catch (NodeNotFoundException f)
		{
			JOptionPane.showMessageDialog(null, "The node that corresponds with this button was not found.", "NODE NOT FOUND", JOptionPane.ERROR_MESSAGE);
		}
	}
}