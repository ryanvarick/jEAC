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
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import net.infonode.docking.*;
import net.infonode.docking.theme.*;
import net.infonode.docking.util.*;
import net.infonode.util.*;
import edu.indiana.cs.eac.hardware.*;




/**
 * Multi-document interface (MDI) manager.
 * 
 * <p>This class implements an MDI manager.
 * based on http://www.javaworld.com/javaworld/jw-05-2001/jw-0525-mdi.html  
 * 
 * @author   Ryan R. Varick
 * @since    2.0.0
 *
 */
public class InterfaceManager
{
	/* defaults (do not alter directly; use the API instead) */
	private boolean useNativeLAF = true;
	private DockingWindowsTheme theme = new ShapedGradientDockingTheme();
	
	// TODO: externalize strings
	private static final String APPLICATION_TITLE = "jEAC - Cross-platform EAC development environment";
	private static final String DESKTOP_TITLE     = "Workspace";
	private static final String DEVICE_MGR_TITLE  = "Device Manager";
	private static final String LLA_EDITOR_TITLE  = "LLA Editor";
	private static final String EVOLVER_TITLE     = "Evolver";

	private static final int INITIAL_WIDTH  = 800;
	private static final int INITIAL_HEIGHT = 600;

	private static final Color DESKTOP_COLOR = Color.GRAY.brighter();
		
	/* UI components (do not alter) */
	private JFrame frame;
	private MDIDesktopPane desktop;
	private JTree deviceTree;
	
	/* device cache */
	private Device[][] verifiedDevices;
	private HashMap<String, Device> deviceList = new HashMap<String, Device>();

	
	
	/**
	 *
	 */
	public InterfaceManager()
	{
		// always set first
		setLookAndFeel();
		
		frame = new JFrame();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setResizable(true);
		frame.setTitle(APPLICATION_TITLE);
		
		// start maximized, when supported
		frame.setSize(new Dimension(INITIAL_WIDTH, INITIAL_HEIGHT));
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		// center on screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width - frame.getWidth()) / 2;
		int y = (screenSize.height - frame.getHeight()) / 2;
		frame.setLocation(new Point(x, y));
		
		// add components
		MenuManager menu = MenuManager.getInstance();
		frame.setJMenuBar(menu.getMenu());
		
		frame.add(getRootWindow(), BorderLayout.CENTER);
		
		JLabel sb = new JLabel(" Status: Disconnected");
		frame.add(sb, BorderLayout.SOUTH);
	}
	
	/**
	 * Builds the desktop.
	 * 
	 * <p>jEAC uses two kinds of UI containers:  dockable windows and free-
	 * floating windows.  Free-floating windows are contained within the desktop 
	 * area, which behaves like a standard multi-document interface (MDI).
	 * 
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 * 
	 * @return   MDI desktop component.
	 * 
	 */
	private View getDesktopView()
	{
		// create (and cache) the desktop; we will need it later
		desktop = new MDIDesktopPane();
		desktop.setBackground(DESKTOP_COLOR);
		
		// create a scroll pane to contain the desktop
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().add(desktop);
		
		// create an InfoNode view to contain the new, scrollable desktop
		View scrollableDesktop = new View(DESKTOP_TITLE, null, scrollPane);
		
		// make the desktop more-or-less immutable
		scrollableDesktop.getWindowProperties().setCloseEnabled(false);
		scrollableDesktop.getWindowProperties().setUndockEnabled(false);
		scrollableDesktop.getWindowProperties().setMaximizeEnabled(false);
		scrollableDesktop.getWindowProperties().setMinimizeEnabled(false);
		scrollableDesktop.getWindowProperties().setDragEnabled(false);
		
		return scrollableDesktop;
	}
	
	/**
	 * Builds the overall UI structure.
	 * 
	 * <p>For a more robust UI, we are using a mix of InfoNode dockable windows
	 * and standard Swing components.  This method defines the overall UI
	 * structure but does <i>NOT</i> populate specific components (such as the
	 * device list).
	 * 
	 * @return   InfoNode component containing the overall UI.
	 * 
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 * 
	 */
	private RootWindow getRootWindow()
	{
		// allocate views (similar to JPanel)
		int view = 1;
		ViewMap viewMap = new ViewMap();

		View desktopView = getDesktopView();
//		desktopView.getCustomTitleBarComponents();
		viewMap.addView(view++, desktopView);

		View evolverView = new View("Evolver", null, new MDIDesktopPane());
		viewMap.addView(view++, evolverView);

		View editorView = new View("LLA Editor", null, new MDIDesktopPane());
		viewMap.addView(view++, editorView);

		View deviceManagerView = new View("Device Manager", null, getDevicePanel());
		viewMap.addView(view++, deviceManagerView);
		
		// allocate main window (similiar to JFrame)
		//  NOTE: RootWindow *must* be allocated before other InfoNode windows
		RootWindow rootWindow = DockingUtil.createRootWindow(viewMap, false);

		// reduce clutter by disabling functionality
		rootWindow.getRootWindowProperties().getTabWindowProperties().getMaximizeButtonProperties().setVisible(false);
		rootWindow.getRootWindowProperties().getTabWindowProperties().getMinimizeButtonProperties().setVisible(false);
		rootWindow.getRootWindowProperties().getTabWindowProperties().getDockButtonProperties().setVisible(false);
		rootWindow.getRootWindowProperties().getTabWindowProperties().getUndockButtonProperties().setVisible(false);
		rootWindow.getRootWindowProperties().getTabWindowProperties().getCloseButtonProperties().setVisible(false);

		// apply theme (specified in setLookAndFeel())
		rootWindow.getRootWindowProperties().addSuperObject(theme.getRootWindowProperties());		

		// specify InfoNode tab layout (similar to JTabbedPane)
		rootWindow.getWindowBar(Direction.RIGHT).setEnabled(true);

		// specify view layout (similar to JSplitPane)
		SplitWindow toolWindow = new SplitWindow(false, 0.5f, deviceManagerView, evolverView);
		SplitWindow mainWindow = new SplitWindow(true,  0.7f, desktopView, toolWindow);

		rootWindow.setWindow(mainWindow);

		return rootWindow;
	}
	
	
	
	/**
	 * 
	 *
	 */
	public void init()
	{
		deviceTree = getDeviceTree();
	}
	
	/**
	 * Starts the interface.
	 * 
	 * needs to be separate from the constructor
	 *
	 */
	public void show()
	{
		frame.setVisible(true);
//		frame.setEnabled(false);
	}
	
	
	
	/**
	 * 
	 * @return   Returns a validated list of drivers, organized by class.
	 * 
	 */
	public Device[][] getValidDevices()
	{
		if(verifiedDevices == null)
		{
			verifyDevices();
		}
		return verifiedDevices;
	}
	
	/**
	 * Validates the list of devices provided by the <code>HardwareManager</code>.
	 * 
	 * <p>The <code>HardwareManager</code> maintains a list of potential EAC
	 * devices; however, most of these devices are invalid for one reason or
	 * another.  Verifying the list of devices usually takes a few seconds.
	 * While it would be trivial to run the verification process at the hardware
	 * level, we could not provide UI feedback without crossing the 
	 * interface/hardware abstraction barrier.  Since the interface is already
	 * hardware-aware, verification is done here at the interface level instead.
	 * 
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 * 
	 */
	
	// TODO: validate vs. verify

	public void verifyDevices()
	{	
		// grab the list of unverified devices
		HardwareManager hm = HardwareManager.getInstance();
		Device[][] unverifiedDevices = hm.getKnownDevices();

		// prepare the loading frame
		LoadingFrame lf = new LoadingFrame(hm.getDeviceCount());
		
		// verify each device
		Vector<Device[]> drivers = new Vector<Device[]>();      // outer accumulator (drivers)
		for(int i = 0; i < unverifiedDevices.length; i++)
		{
			Vector<Device> devices = new Vector<Device>();      // inner accumulator (devices)
			for(int j = 0; j < unverifiedDevices[i].length; j++)
			{
				Device d = unverifiedDevices[i][j];
				
				if(d.isValid())
				{
					devices.add(d);
					deviceList.put(d.getDeviceName(), d);
				}
				lf.increment();
			}
			
			// only add the driver class if it contains valid devices
			if(devices.size() > 0)
			{
				Device[] verified = new Device[devices.size()];
				verified = devices.toArray(verified);
				drivers.add(verified);
			}
		}
		verifiedDevices = new Device[drivers.size()][];
		verifiedDevices = drivers.toArray(verifiedDevices);
		
		lf.close();
	}



	public MDIDesktopPane getDesktop()
	{
		return desktop;
	}
	public JFrame getWindow()
	{
		return frame;
	}
	
	
	

	

	/**
	 * Sets the look-and-feel.
	 * 
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 * 
	 */
	private void setLookAndFeel()
	{
		if(useNativeLAF)
		{
			try
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} 
			catch(Exception e)
			{
				System.err.println("Could not load platform-native look-and-feel.");
			}
		}
	}
	
	
	public JTree getDeviceTree()
	{
		Device[][] d = getValidDevices();
		
//		Object[] hierarchy =
//		{ 
//				"Available Devices",
//				new Object[]
//				{ 
//						"Offline Drivers",
//						"Static driver (inactive)",
//						"Random driver (active)"
//				},
//		        new Object[] 
//		        { 
//						"Network EACs",
//						"eac1.cs.indiana.edu",
//						"eac3.cs.indiana.edu",
//						"eac4.cs.indiana.edu"
//		        },
//	            new Object[]
//	            {
//						"Local uEACs",
//						"COM5",
//						"COM13",
//	            }
//		};
		
//		DefaultMutableTreeNode root = processHierarchy(hierarchy);
		DefaultMutableTreeNode root = processHierarchy(d);
		JTree tree = new JTree(root);
		    
		for(int i = 0; i < tree.getRowCount(); i++)
		{
			tree.expandRow(i);
		}
		    
		tree.addTreeSelectionListener(new DeviceTreeListener());
		tree.addMouseListener(new DeviceTreeMouseListener());
		    
		return tree;
	}
	
	/**
	 * 
	 * @param hierarchy
	 * @return
	 */
	private DefaultMutableTreeNode processHierarchy(Object[] hierarchy)
	{
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(hierarchy[0]);
	
		DefaultMutableTreeNode child;
		for(int i = 1; i < hierarchy.length; i++)
		{
			Object nodeSpecifier = hierarchy[i];
			if (nodeSpecifier instanceof Object[])  // Ie node with children
			{
				child = processHierarchy((Object[])nodeSpecifier);
			}
			else
			{
		        child = new DefaultMutableTreeNode(nodeSpecifier); // Ie Leaf
			}
			node.add(child);
		}
		
		return(node);
	}

	
	private JPanel getDevicePanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JToolBar tools = new JToolBar();
		tools.setFloatable(false);
		  
		JButton b = new JButton();
		b.setText("Rescan");
		b.setToolTipText("Scans for new devices");
		JButton c = new JButton();
		c.setText("Connect");
		c.setToolTipText("Scans for new devices");
		JButton d = new JButton();
		d.setText("Reset");
		d.setToolTipText("Scans for new devices");
		tools.add(b);
		tools.addSeparator();
		tools.add(c);
		tools.add(d);
		
		panel.add(tools, BorderLayout.NORTH);
		
		deviceTree = new JTree();
//		panel.add(jt, BorderLayout.CENTER);
		  
		JScrollPane deviceListPane = new JScrollPane(deviceTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JPanel devicePropertiesPanel = new JPanel();
		devicePropertiesPanel.add(new JLabel("Properties go here."));
		  
		JSplitPane devicePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, deviceListPane, devicePropertiesPanel);
		panel.add(devicePane);
		  
		return panel;  
	}
	
	private class DeviceTreeListener implements TreeSelectionListener
	{
		public void valueChanged(TreeSelectionEvent e)
		{
//		        DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.getNewLeadSelectionPath().getLastPathComponent();
			  
			if(node == null)
			{
				System.out.println("Null: "); return;
			}
			Object nodeInfo = node.getUserObject();
			  
			if (node.isLeaf())
			{
				Device d = (Device)nodeInfo; // now we rock
				
				
				System.out.println("Leaf: " + node.toString() + "; name: " + d.getDeviceName());
			}
			else
			{
				System.out.println("Branch: " + node.toString()); 
			}
		}
	}
	  
	private class DeviceTreeMouseListener extends MouseAdapter {
		public void mousePressed(MouseEvent e)
		{
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e)
		{
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e)
		{
			if(e.isPopupTrigger())
			{
				JPopupMenu p  = new JPopupMenu();
				JMenuItem jmi = new JMenuItem("Test");
				JMenuItem jmi2 = new JMenuItem("Test");
				JMenuItem jmi3 = new JMenuItem("Test");
				p.add(jmi);
				p.add(jmi2);
				p.add(jmi3);
				  
				p.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


//	public void testMethod()
//	{
//		Game game = new Game();
//		
//		game.setManualControlEnabled(true);
//		game.setUseMomentum(true);
//		
//		game.setGrowSnake(true);
//		game.setIgnoreSelfCollisions(true);
//		game.setIgnoreWallCollisions(true);
//		
//		// allocate generation information panel
//		JPanel generalPanel = new JPanel(new GridLayout(2, 2));
//		generalPanel.setBorder(BorderFactory.createTitledBorder("General information"));
//		generalPanel.add(new JLabel(" Food eaten:"));
////		generalPanel.add(foodEaten);
//		generalPanel.add(new JLabel(" Time remaining:"));
////		generalPanel.add(timeLeft);
//		
//		// allocate world information panel
//		JPanel worldPanel = new JPanel(new GridLayout(5, 2));
//		worldPanel.setBorder(BorderFactory.createTitledBorder("World information"));
//		worldPanel.add(new JLabel(" Snake (x,y):"));
////		worldPanel.add(snakeLocation);
//		worldPanel.add(new JLabel(" Food (x,y):"));
////		worldPanel.add(foodLocation);
//		worldPanel.add(new JLabel(" Absolute dt:"));
////		worldPanel.add(absoluteFoodDistance);
//		worldPanel.add(new JLabel(" Absolute direction:"));
////		worldPanel.add(snakeDirection);
//		worldPanel.add(new JLabel(" Relative direction (dx,dy):"));
////		worldPanel.add(relativeFoodDistance);
//
//		// allocate snake information panel
//		JPanel snakePanel = new JPanel(new GridLayout(3, 2));
//		snakePanel.setBorder(BorderFactory.createTitledBorder("Snake information"));
//		snakePanel.add(new JLabel(" Input vector:"));
//		snakePanel.add(new JLabel());
//		snakePanel.add(new JLabel(" Output vector:"));
//		snakePanel.add(new JLabel());
//		snakePanel.add(new JLabel(" Fitness score:"));
////		snakePanel.add(fitness);
//
//		// finalize the window
//		// TODO: Register the game frame with the MDI manager
//		JInternalFrame gameWindow = new JInternalFrame();
//		gameWindow.setLayout(new BoxLayout(gameWindow.getContentPane(), BoxLayout.Y_AXIS));
//		gameWindow.add(game);
//		gameWindow.add(generalPanel);
//		gameWindow.add(worldPanel);
//		gameWindow.add(snakePanel);
//		gameWindow.setResizable(false);
//        gameWindow.setIconifiable(true);
//        gameWindow.setClosable(true);
//
//		gameWindow.setTitle("Snaaaaake!");
//		gameWindow.pack();
//		gameWindow.setVisible(true);
//		desktop.add(gameWindow);
//		
//		game.start();
//	}

}