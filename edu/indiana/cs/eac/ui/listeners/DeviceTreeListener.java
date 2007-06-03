/*
 * This file is part of jEAC (http://jeac.sf.net/).
 * 
 * Copyright (C) 2007.  All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 * 
 */

package edu.indiana.cs.eac.ui.listeners;

import javax.swing.event.*;
import javax.swing.tree.*;

import edu.indiana.cs.eac.hardware.*;
import edu.indiana.cs.eac.ui.*;



/**
 * 
 * 
 * @author   Ryan R. Varick
 * @since    2.0.0
 * 
 */
public class DeviceTreeListener implements TreeSelectionListener
{
	private DevicePanelManager dm;
	
	public DeviceTreeListener(DevicePanelManager dm)
	{
		this.dm = dm;
	}

	
	public void valueChanged(TreeSelectionEvent e)
	{
		DefaultMutableTreeNode node = null;
		
		// null ptr exception possibly thrown when right-clicking out of bounds
		// TODO: fire change listener on null ptr (mode: off)
		try
		{
			node = (DefaultMutableTreeNode)e.getNewLeadSelectionPath().getLastPathComponent();
		}
		catch(NullPointerException e2)
		{
			System.out.println("Null: ");
			return;
		}
		  
//		if(node == null)
//		{
//			System.out.println("Null: "); return;
//		}
		Object nodeInfo = node.getUserObject();
		  
		if(node.isLeaf())
		{
			Device d = (Device)nodeInfo; // now we rock
			
			dm.setSelectedDevice(d);
			// 1. changeMenuBar(d);
			// 2. updateDeviceControls(d);
			// new: updateSelectedDevice(d);
			
			System.out.println("Leaf: " + node.toString() + "; name: " + d.getDeviceName());
		}
		else
		{
			System.out.println("Branch: " + node.toString());
			
			dm.setSelectedDevice(null);
			// 1. disableMenuBar();
			// 2. disableDeviceControls();
			// new: updateSelectedDevice(null);
			
//			(DefaultMutableTreeNode)e.get
		}
	}
}