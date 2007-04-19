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

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import net.infonode.docking.*;
import net.infonode.docking.theme.*;
import net.infonode.docking.util.*;
import net.infonode.util.*;

import java.util.*;
import java.awt.event.*;
import java.awt.*;

import edu.indiana.cs.eac.hardware.*;
import edu.indiana.cs.ga.snakeEvolver.*;
import edu.indiana.cs.testing.ui.*;
import edu.indiana.cs.testing.ui.NewUI.*;


import ec.display.*;


//based on http://www.javaworld.com/javaworld/jw-05-2001/jw-0525-mdi.html


/**
 * Multi-document interface (MDI) manager.
 * 
 * <p>This class implements an MDI manager.  
 * 
 * @author   Ryan R. Varick
 * @since    2.0.0
 *
 */
public class InterfaceManager
{
	/* defaults (do not alter directly; use the API instead) */
	private boolean useNativeLAF = true;
	
	private Color desktopColor = Color.GRAY.brighter();
	
	private static final String APPLICATION_TITLE = "jEAC - Cross-platform EAC development environment";
	
	/** Initial width of the UI. */
	public static int INITIAL_SIZE_X = 800;
	
	/** Initial height of the UI. */
	public static int INITIAL_SIZE_Y = 600;
	
	
	private static final String DESKTOP_TITLE = "Workspace";
	
//	/** Initial x-coordinate of the UI. */
//	public static int INITIAL_LOCATION_X = 50;
//	
//	/** Initial y-coordinate of the UI. */
//	public static int INITIAL_LOCATION_Y = 50;

	
	/* UI components (do not alter) */
	
	private JFrame mainWindow;
	private MDIDesktopPane desktop;
	
	
	private Device[][] validDevices;




	public MDIDesktopPane getDesktop()
	{
		return desktop;
	}
	public JFrame getWindow()
	{
		return mainWindow;
	}
	
	
	
	
	private static InterfaceManager instance;
	private InterfaceManager()
	{
		// best applied before Swing components are added
		setLookAndFeel();

		// add views to view map (see InfoNode documentation)
		int view = 1;
		ViewMap viewMap = new ViewMap();

		View desktopView = getDesktopView();
		desktopView.getCustomTitleBarComponents();
		
		viewMap.addView(view++, getDesktopView());

		View evolverView = new View("Evolver", null, new MDIDesktopPane());
		viewMap.addView(view++, evolverView);

//		View editorView = new View("LLA Editor", null, new MDIDesktopPane());
//		viewMap.addView(view++, editorView);

		View deviceManagerView = new View("Device Manager", null, getDevicePanel());
		viewMap.addView(view++, deviceManagerView);
		
		
		
		
		
		SplitWindow testWindow = new SplitWindow(true, 0.7f, desktopView, deviceManagerView);
		SplitWindow toolWindow = new SplitWindow(false, 0.3f, deviceManagerView, evolverView);
		SplitWindow mainWindow1 = new SplitWindow(true,  0.7f, desktopView, toolWindow);
		
		RootWindow rootWindow = DockingUtil.createRootWindow(viewMap, true);
		rootWindow.setWindow(testWindow);

		// theme
		DockingWindowsTheme theme = new ShapedGradientDockingTheme();
		rootWindow.getRootWindowProperties().addSuperObject(theme.getRootWindowProperties());
		
		// turn off tab window controls (too much clutter)
		rootWindow.getRootWindowProperties().getTabWindowProperties().getCloseButtonProperties().setVisible(false);
		rootWindow.getRootWindowProperties().getTabWindowProperties().getDockButtonProperties().setVisible(false);
		rootWindow.getRootWindowProperties().getTabWindowProperties().getMaximizeButtonProperties().setVisible(false);
		rootWindow.getRootWindowProperties().getTabWindowProperties().getMinimizeButtonProperties().setVisible(false);
		rootWindow.getRootWindowProperties().getTabWindowProperties().getUndockButtonProperties().setVisible(false);

		// add menu bar
		rootWindow.getWindowBar(Direction.RIGHT).setEnabled(true);
		
		
		
		
		
		
		
		
		// build the UI skeleton 
		mainWindow = new JFrame();
	
//		TODO: Add statusbar
		JLabel sb = new JLabel(" Status: Disconnected");
//		getContentPane().add(new StatusBarManager(), BorderLayout.SOUTH);
		
//		// finalize
		mainWindow.setLayout(new BorderLayout());
	    mainWindow.add(rootWindow, BorderLayout.CENTER);
	    mainWindow.add(sb, BorderLayout.SOUTH);


		// hook up the menu manager
		MenuManager menu = MenuManager.getInstance();
		mainWindow.setJMenuBar(menu.getMenu());
		
		// specify UI appearance and behavior
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setResizable(true);
		mainWindow.setSize(new Dimension(INITIAL_SIZE_X, INITIAL_SIZE_Y));
		mainWindow.setTitle(APPLICATION_TITLE);

		// center UI on screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width - mainWindow.getWidth()) / 2;
		int y = (screenSize.height - mainWindow.getHeight()) / 2;
		mainWindow.setLocation(new Point(x, y));
		
		// show the world our beautiful creation
		mainWindow.setVisible(true);

		
	}
	public static final InterfaceManager getInstance()
	{
		if(instance == null) { instance = new InterfaceManager(); }
		return instance;
	}
	
	
	
	private View getDesktopView()
	{
		// create (and cache) the desktop
		desktop = new MDIDesktopPane();
		desktop.setBackground(desktopColor);
		
		// create a scrollpane to contain the desktop
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().add(desktop);
		
		// create a view to contain the new, scrollable desktop
		View scrollableDesktop = new View(DESKTOP_TITLE, null, scrollPane);
		
		// customize the view (restrict actions)
		scrollableDesktop.getWindowProperties().setCloseEnabled(false);
		scrollableDesktop.getWindowProperties().setUndockEnabled(false);
		scrollableDesktop.getWindowProperties().setMaximizeEnabled(false);
		scrollableDesktop.getWindowProperties().setMinimizeEnabled(false);
		scrollableDesktop.getWindowProperties().setDragEnabled(false);
		
		return scrollableDesktop;
	}
	
	
	/**
	 * Starts the interface.
	 * 
	 * needs to be separate from the constructor
	 *
	 */
	public void run()
	{

	}
	
	
	/**
	 * 
	 * @return   Returns a validated list of drivers, organized by class.
	 */
	public Device[][] getValidDevices()
	{
		if(validDevices == null)
		{
			loadDrivers();
		}
		return validDevices;
	}
	
//	public Vector<Vector<Device>> getValidDevices2()
//	{
////		// grab the raw list of devices
////		HardwareManager hm = HardwareManager.getInstance();
////		Device[][] knownDevices = hm.getKnownDevices2();
////
////		// prepare the loading frame
////		LoadingFrame lf = new LoadingFrame(hm.getDeviceCount());
////		
////		
////		return new Vector();
//	}
	
	/**
	 * ...
	 * 
	 * @param deviceManager   .
	 * 
	 * @author                Ryan R. Varick
	 * @since                 2.0.0
	 * 
	 */
	public void loadDrivers()
	{
		// grab the raw list of devices
		HardwareManager hm = HardwareManager.getInstance();
		Device[][] knownDevices = hm.getKnownDevices();

		// prepare the loading frame
		LoadingFrame lf = new LoadingFrame(hm.getDeviceCount());
		
		/*
		 * WARNING! Pain in the ass zone ahead! (I apologize to anyone that 
		 * has to maintain this code down the line.)
		 * 
		 * We need to verify the list of devices given by the HardwareManager.
		 * Why?  Because we want to provide UI feedback while validating.  While
		 * we could put this process on its own thread, we'll want to scan for 
		 * devices initially before finalizing the interface.  Our choices are
		 * to either make the hardware layer aware of UI (jEAC version 1) or 
		 * make the UI aware of the hardware layer.  This method represents the
		 * latter.
		 * 
		 * So what's the problem?  The HardwareManager returns a 2D array of 
		 * devices, some or many of which may be invalid.  Thus we will have to
		 * remove elements from the array.  We could use Vectors and generics, 
		 * but because of type erasure, we end up with a rather nasty cast
		 * situation.  What lies below is a wasteful, complicated mess of array
		 * copies.  Cleary this entire algorithm needs to be reworked.
		 * 
		 * FIXME: Convert entire call chain to Vector, or similar.
		 * 
		 */

		// outer loop: verify each driver class
		Device[][] driverHolder = new Device[0][];   // outer accumulator
		for(int i = 0; i < knownDevices.length; i++)
		{
			Device[] deviceHolder = new Device[0];   // inner accumulator
			lf.setTitle("Looking for EACs (" + (i + 1) + "/" + knownDevices.length + ")...");

			// inner loop: verify each device in the driver class
			for(int j = 0; j < knownDevices[i].length; j++)
			{				
				if(knownDevices[i][j].isValid())
				{
					// allocate a larger array and insert the device at the end
					Device[] newDeviceHolder = new Device[deviceHolder.length + 1];
					newDeviceHolder[deviceHolder.length] = knownDevices[i][j];
					
					// merge the old accumulator when the array is larger than zero
					if(deviceHolder.length > 0)
					{
						System.arraycopy(deviceHolder, 0, newDeviceHolder, 0, deviceHolder.length);
					}
					deviceHolder = newDeviceHolder;

					// increment the progress bar
					lf.increment();
				}
			}
			
			// add the driver class if valid devices were found
			if(deviceHolder.length > 0)
			{
				Device[][] newDriverHolder = new Device[driverHolder.length + 1][];
				newDriverHolder[driverHolder.length] = deviceHolder;
				
				if(driverHolder.length > 0)
				{
					System.arraycopy(driverHolder, 0, newDriverHolder, 0, driverHolder.length);
				}
				driverHolder = newDriverHolder;
			}
		}

		lf.close();
		validDevices = driverHolder;
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
	

	/**
	 * Sets the Swing look-and-feel.
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public JTree getTree()
	{
		    Object[] hierarchy =
		      { "Available Devices",
		    	new Object[] { "Offline Drivers",
		    		  "Static driver (inactive)",
		    		  "Random driver (active)"
		      },
		        new Object[] { "Network EACs",
	                       "eac1.cs.indiana.edu",
	                       "eac3.cs.indiana.edu",
	                       "eac4.cs.indiana.edu" },
	            new Object[] { "Local uEACs",
		    		       "COM5",
		    		       "COM13", }};
		    DefaultMutableTreeNode root = processHierarchy(hierarchy);
		    JTree tree = new JTree(root);
		    
		    for(int i = 0; i < tree.getRowCount(); i++)
		    {
		    	tree.expandRow(i);
		    }
		    
		    tree.addTreeSelectionListener(new DeviceTreeListener());
		    tree.addMouseListener(new DeviceTreeMouseListener());
		    
		    return tree;
	}
	
	  private DefaultMutableTreeNode processHierarchy(Object[] hierarchy) {
		    DefaultMutableTreeNode node =
		      new DefaultMutableTreeNode(hierarchy[0]);
		    DefaultMutableTreeNode child;
		    for(int i=1; i<hierarchy.length; i++) {
		      Object nodeSpecifier = hierarchy[i];
		      if (nodeSpecifier instanceof Object[])  // Ie node with children
		        child = processHierarchy((Object[])nodeSpecifier);
		      else
		        child = new DefaultMutableTreeNode(nodeSpecifier); // Ie Leaf
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
		  
		  JTree jt = getTree();
//		  panel.add(jt, BorderLayout.CENTER);


		  
		  JScrollPane deviceListPane = new JScrollPane(jt, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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
		    	  System.out.println("Leaf: " + node.toString());
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
	
}