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

package edu.indiana.cs.eac.ui;

import java.awt.*;

import javax.swing.*;
import javax.swing.tree.*;

import edu.indiana.cs.eac.hardware.*;
import edu.indiana.cs.eac.ui.listeners.*;

/**
 */
public class DevicePanelManager
{
	private static final String DEVICE_TREE_ROOT_TITLE = "Available devices";

	private InterfaceManager im;
	
	private JTree deviceTree;
	private JToolBar tools;

	
	public DevicePanelManager(InterfaceManager im)
	{
		this.im = im;
	}
	

	/**
	 * 
	 * 
	 * @return   Device panel component.
	 * 
	 *  TODO: finish device panel
	 * 
	 */
	public JPanel getDevicePanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		tools = new JToolBar();
		tools.setFloatable(false);
		  
		JButton c = new JButton();
		c.setText("Connect");
//		c.setToolTipText("Scans for new devices");
		tools.add(c);

//		tools.addSeparator();

		JButton d = new JButton();
		d.setText("Reset");
		d.setToolTipText("Scans for new devices");
		tools.add(d);

		tools.addSeparator();

		JButton b = new JButton();
		b.setText("Rescan");
		b.setToolTipText("Scans for new devices");
		tools.add(b);

		
		panel.add(tools, BorderLayout.SOUTH);
		
		deviceTree = new JTree();
		populateDeviceTree(deviceTree);
		JScrollPane deviceListPane = new JScrollPane(deviceTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JPanel devicePropertiesPanel = new JPanel();
		devicePropertiesPanel.add(new JLabel("Properties go here."));
		  
		JSplitPane devicePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, deviceListPane, devicePropertiesPanel);
		panel.add(devicePane);
		  
		return panel;  
	}
	
	/**
	 * Populates the valid device tree.
	 * 
	 * <p>This method will populate a <code>JTree</code> with the list of valid 
	 * devices, organized by driver class.  In general, this should be the tree
	 * placeholder initialized by the constructor.
	 * 
	 * <p>A separate method is used for two reasons.  First, validating the
	 * device list takes a few seconds.  It is easier to provide appropriate UI
	 * feedback if the population process is separated from program startup.
	 * Second, using a separate method affords an opportunity to probe for new
	 * devices periodically.
	 * 
	 * <p><i>Note that this method produces side effects in that the given
	 * <code>JTree</code> will be manipulated directly.</i>
	 * 
	 * <p>This is due, in part, to my tenuous grasp of Java design decision.  As
	 * I understand, Java is pass-by-value, although it sometimes appears to be
	 * pass-by-reference when it comes to objects because object *references*
	 * are passed by value.  Thus we have to use side-effects to manipulate an
	 * already instantiated Swing component (the <code>JTree</code>).  If we use
	 * <code>new</code>, we will clober the object reference.  See
	 * <a href="http://javadude.com/articles/passbyvalue.htm">this article</a>
	 * for a better explanation.
	 * 
	 * <p>There is probably a better (safer?) way to do this.
	 * 
	 * @param tree   <code>JTree</code> to populate.
	 * 
	 * @author       Ryan R. Varick
	 * @since        2.0.0 
	 * 
	 * @deprecated Move to <code>DevicePanelManager</code>
	 * 
	 */
	private void populateDeviceTree(JTree tree)
	{
		// label the root
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(DEVICE_TREE_ROOT_TITLE);
		
		// build the hierarchy
		Device[][] devices = im.getDevices();
		for(int i = 0; i < devices.length; i++)
		{
			// get the driver class label from the first device
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(devices[i][0].getDriverName() + "s");
			
			// add the individual devices
			for(int j = 0; j < devices[i].length; j++)
			{
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(devices[i][j]);
				node.add(child);
			}
			
			root.add(node);
		}
		
		// make a tree out of it
		TreeModel t = new DefaultTreeModel(root);
		tree.setModel(t);
		
		// make each row visible
		for(int i = 0; i < tree.getRowCount(); i++)
		{
			tree.expandRow(i);
		}
		
		// add selection listener and right-click listener
		tree.addTreeSelectionListener(new DeviceTreeListener(this));
		tree.addMouseListener(new DeviceTreeMouseListener(this));
	}
	
	
	public void updateSelectedDevice(Device d)
	{
		if(d == null)
		{
			// 1. disable menu bar
			tools.setEnabled(false);
//			tools.setVisible(false);
			
			// 2. disable properties panel
			
			System.out.println("Turning shit off.");
			return;
		}
		
		tools.setEnabled(false);
//		tools.setVisible(true);
		
		System.out.println("Activating new device");
		
	}
	
	public JTree getTree()
	{
		return deviceTree;
	}
	
}
