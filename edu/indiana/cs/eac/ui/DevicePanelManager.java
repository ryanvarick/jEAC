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

import edu.indiana.cs.eac.*;
import edu.indiana.cs.eac.hardware.*;
import edu.indiana.cs.eac.ui.deprecated.*;
import edu.indiana.cs.eac.ui.listeners.*;



/**
 * Responsible for device management and device-specific controls.
 * 
 * TODO: full javadoc here
 * 
 * @author   Ryan R. Varick
 * @since    2.0.0
 *
 */
public class DevicePanelManager implements Manager
{
	private static final String DEVICE_TREE_ROOT_TITLE = "Available devices";

	private InterfaceManager im;
	
	private JComponent devicePanel;
	private JTree deviceTree;
	private JToolBar deviceToolbar;
	
	private JButton connectionButton, resetButton, rescanButton;
	private JButton[] buttonList;
	
	private Device activeDevice;

	private JButton loadButton;

	private JButton saveButton;

	private TabWindow tabs;
	
	
	
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
		return devicePanel;
	}
	
	/**
	 * TODO: cleanup, comment
	 * @return
	 */
	private JToolBar getDeviceToolbar()
	{
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.setRollover(true);
		
		connectionButton = new JButton("Connect");
		connectionButton.addActionListener(new CxnListener());
		connectionButton.setIcon(JEACUtilities.getImageIcon("icon-connection.png"));
		toolbar.add(connectionButton);
		
		toolbar.addSeparator();
		
		loadButton = new JButton();
		loadButton.setIcon(JEACUtilities.getImageIcon("icon-open.png"));
		loadButton.setToolTipText("Load configuration");
//		bar.add(loadButton);
		
		saveButton = new JButton();
		saveButton.setIcon(JEACUtilities.getImageIcon("icon-save.png"));
		saveButton.setToolTipText("Save configuration");
//		bar.add(saveButton);
				
		resetButton  = new JButton("Reset");
//		resetButton.addActionListener(new ConnectionButtonListener(this, resetButton));
		resetButton.setIcon(JEACUtilities.getImageIcon("icon-reset.png"));
		resetButton.setToolTipText("Reset");
		resetButton.addActionListener(new ResetListener());
		toolbar.add(resetButton);
		
//		bar.addSeparator();
		
		
		
		// register components
		buttonList = new JButton[]
		{
			connectionButton,
			loadButton,
			saveButton,
			resetButton,
		};

		return toolbar;
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
	
	/**
	 * Message-passing method that forces things to reload.
	 *
	 */
	public void update()
	{
		/*
		 * This method should be used to update things.  It can be called
		 * interally or externally (like, say, in response to a menu event).
		 * Ideally, this will be called by InterfaceManager.update();
		 * 
		 */
		
		// 1. read selected device
		// 2. update toolbar
		// 3. update tree selection
		// 4. update view page
		
		
//		updateSelectedDevice(null);
		System.out.println("DPM::update(): Update fired!");
		
		JPanel propertiesPanel1 = new JPanel();
		propertiesPanel1.add(new JLabel("No devices currently connected. 1"));
		
		View pp0 = new View("COM4", null, propertiesPanel1);
		
		tabs.addTab(pp0);

	}
	
	/**
	 * Should this be setSelectedDevice?
	 * @param d
	 */
	public void setSelectedDevice(Device d)
	{
		this.activeDevice = d;
		
		if(d == null)
		{
			// 1. disable menu bar
			deviceToolbar.setEnabled(false);
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
			connectionButton.setIcon(new ImageIcon(JEAC_Reference.getImage("icon-disconnect.png")));
			connectionButton.setEnabled(true);
			
			resetButton.setEnabled(true);
		}
		else
		{
			connectionButton.setText("Connect");
			connectionButton.setIcon(new ImageIcon(JEAC_Reference.getImage("icon-connect.png")));
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
	// TODO: externalize
	private class CxnListener implements ActionListener
	{

		public void actionPerformed(ActionEvent arg0)
		{
			if(activeDevice.isConnected())
			{
				activeDevice.disconnect();
				setSelectedDevice(null);
			}
			else
			{
				try
				{
					activeDevice.connect();
					setSelectedDevice(activeDevice);
					
					// TESTING!
					im.update();

				}
				catch(Exception e)
				{
					
				}
			}
			
			
		}
		
	}
	
	// TODO: externalize	
	private class ResetListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			JOptionPane.showConfirmDialog(null, "Resetting this device will clear all connections and cannot be undone.  Are you sure you want to proceed?", "Confirm reset", JOptionPane.YES_NO_OPTION);
		}
	}

	public void init()
	{
		/* 1. build the device chooser */
		deviceToolbar = getDeviceToolbar();

		// FIXME: this depends on the toolbar being initialized; unwravel this
		setSelectedDevice(null);
				
		JPanel deviceChooserPanel = new JPanel();
		deviceChooserPanel.setLayout(new BorderLayout());
		deviceChooserPanel.add(deviceToolbar, BorderLayout.NORTH);

		// FIXME: figure out how this should work
		deviceTree = new JTree();
		populateDeviceTree(deviceTree);
		JScrollPane deviceListPane = new JScrollPane(deviceTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);	
		deviceChooserPanel.add(deviceListPane, BorderLayout.CENTER);
		
		JToolBar rescanToolbar = new JToolBar();
		rescanToolbar.setFloatable(false);
		rescanToolbar.setRollover(true);
		rescanToolbar.setLayout(new BorderLayout());

		JButton rescan = new JButton("Scan for new devices");
		rescanToolbar.add(rescan, BorderLayout.EAST);
		
		deviceChooserPanel.add(rescanToolbar, BorderLayout.SOUTH);
		

		
		/* 2. set up the control panel area */
		ViewMap viewMap = new ViewMap();
		int view_cnt = 0;

		View devices = new View(null, null, deviceChooserPanel);
		devices.getViewProperties().setAlwaysShowTitle(false);
		viewMap.addView(view_cnt++, devices);

		JPanel propertiesPanel0 = new JPanel();
		propertiesPanel0.add(new JLabel("No devices currently connected. 0"));
		JPanel propertiesPanel1 = new JPanel();
		propertiesPanel1.add(new JLabel("No devices currently connected. 1"));
		
		View pp0 = new View("COM4", null, propertiesPanel0);
		viewMap.addView(view_cnt++, pp0);
		
		View pp1 = new View("eac2.cs.indiana.edu", null, propertiesPanel1);
		viewMap.addView(view_cnt++, pp1);
		
		
		
		/* put it all together */
		RootWindow tpw = InterfaceManager.createMinimalRootWindow(viewMap);
		
		tabs = new TabWindow();
		tabs.getTabWindowProperties().getTabbedPanelProperties().setShadowEnabled(false);
		
		tabs.addTab(pp0);
		tabs.addTab(pp1);

		SplitWindow sw = new SplitWindow(false, 0.4f, devices, tabs);
		
		tpw.setWindow(sw);
		
		devicePanel = tpw;
		
		// TODO: determine why this will not resize
//		SplitWindow container = new SplitWindow(false, tp, tw);
//		SplitWindow container = new SplitWindow(false, 0.4f, tp, tw);
//		JSplitPane container = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, tp, tw);
		
		// TODO: add views programmatically
//		v.addView(2, new View("Blah", null, new JLabel("Nothing here!")));
	}
		
}
