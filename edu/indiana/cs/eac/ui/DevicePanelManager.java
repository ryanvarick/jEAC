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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;

import net.infonode.docking.*;
import net.infonode.docking.util.*;

import edu.indiana.cs.eac.hardware.*;
import edu.indiana.cs.eac.ui.listeners.*;



/**
 * TODO: javadoc description
 * 
 * @author   Ryan R. Varick
 * @since    2.0.0
 *
 */
public class DevicePanelManager
{
	private static final String DEVICE_TREE_ROOT_TITLE = "Available devices";

	private InterfaceManager im;
	
	private JTree deviceTree;
	private JToolBar toolbar;
	
	private JButton connectionButton, resetButton, rescanButton;
	private JButton[] buttonList;
	
	private Device activeDevice;
	
	
	
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
	public JComponent getDevicePanel()
	{
		// initialize the toolbar, with no selected device
		toolbar = getToolBar();
		updateSelectedDevice(null);
				
		deviceTree = new JTree();
		populateDeviceTree(deviceTree);
		JScrollPane deviceListPane = new JScrollPane(deviceTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.add(deviceListPane, BorderLayout.CENTER);
		topPanel.add(toolbar, BorderLayout.NORTH);
				
		JPanel propertiesPanel = new JPanel();
		propertiesPanel.add(new JLabel("No devices currently connected."));
		
		
		
		View tp = new View("Available Devices", null, topPanel);
		tp.getViewProperties().setAlwaysShowTitle(false);
		ViewMap tpv = new ViewMap();
		tpv.addView(0, tp);
		
//		RootWindow tpw = DockingUtil.createRootWindow(tpv, false);
		RootWindow tpw = InterfaceManager.createMinimalRootWindow(tpv);
		
		View pp = new View("COM4", null, propertiesPanel);
		View pp2 = new View("eac2.cs.indiana.edu", null, propertiesPanel);

		ViewMap v = new ViewMap();
		v.addView(0, pp);
		v.addView(1, pp2);
		
//		RootWindow tw = DockingUtil.createRootWindow(v, false);
		RootWindow tw = InterfaceManager.createMinimalRootWindow(v);
		
//		TabWindow tw = new TabWindow();
//		tw.addTab(pp);
//		tw.addTab(pp2);
		
		SplitWindow panelContainer = new SplitWindow(false, 0.4f, tpw, tw);
//		SplitWindow panelContainer = new SplitWindow(false, 0.4f, pp, pp2);
		
		v.addView(2, new View("Blah", null, new JLabel("Nothing here!")));
		
//		JSplitPane panelContainer = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, topPanel, propertiesPanel);
//		JSplitPane panelContainer = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, tp, tw);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(panelContainer, BorderLayout.CENTER);
		  
		return panelContainer;
	}
	
	private JToolBar getToolBar()
	{
		JToolBar bar = new JToolBar();
		bar.setFloatable(false);
		bar.setRollover(true);
//		bar.setLayout(new BorderLayout());
		
		connectionButton = new JButton("Connect");
		connectionButton.addActionListener(new CxnListener());
		bar.add(connectionButton);
				
		resetButton  = new JButton("Reset");
//		resetButton.addActionListener(new ConnectionButtonListener(this, resetButton));
		resetButton.addActionListener(new ResetListener());
		bar.add(resetButton);
		
		bar.addSeparator();
		
		rescanButton = new JButton("Rescan");
//		rescanButton.addActionListener(new ConnectionButtonListener(this, rescanButton));
		bar.add(rescanButton);
		
		
		// register components
		buttonList = new JButton[]
		{
			connectionButton,
			resetButton,
			rescanButton,  // FIXME: leave in for now for proper Swing layout
		};

		return bar;
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
	
	public Device getActiveDevice()
	{
		return this.activeDevice;
	}
	public void updateSelectedDevice(Device d)
	{
		this.activeDevice = d;
		
		if(d == null)
		{
			// 1. disable menu bar
			toolbar.setEnabled(false);
//			tools.setVisible(false);
			
			// 2. disable properties panel
			
			System.out.println("Turning shit off.");
			
//			toggleButton.setEnabled(false);
			for(int i = 0; i < buttonList.length; i++)
			{
				buttonList[i].setEnabled(false);
			}
			
			return;
		}
		
//		for(int i = 0; i < buttonList.length; i++)
//		{
//			buttonList[i].setEnabled(true);
//		}
		
		if(d.isConnected())
		{
			connectionButton.setText("Disconnect");
			connectionButton.setEnabled(true);
			
			resetButton.setEnabled(true);
		}
		else
		{
			connectionButton.setText("Connect");
			connectionButton.setEnabled(true);
			
			resetButton.setEnabled(false);
		}
		

		
//		tools.setVisible(true);
		
		System.out.println("Activating new device");
		connectionButton.setEnabled(true);
		
	}
	
	public JTree getTree()
	{
		return deviceTree;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	@SuppressWarnings("unused")
	private class CxnListener implements ActionListener
	{

		public void actionPerformed(ActionEvent arg0)
		{
			if(activeDevice.isConnected())
			{
				activeDevice.disconnect();
				updateSelectedDevice(null);
			}
			else
			{
				try
				{
					activeDevice.connect();
					updateSelectedDevice(activeDevice);
				}
				catch(Exception e)
				{
					
				}
			}
			
		}
		
	}
	
	
	private class ResetListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			JOptionPane.showConfirmDialog(null, "Resetting this device will clear all connections and cannot be undone.  Are you sure you want to proceed?", "Confirm reset", JOptionPane.YES_NO_OPTION);
		}
	}
	
	
	
	
	
	
	
	
	
	
}
