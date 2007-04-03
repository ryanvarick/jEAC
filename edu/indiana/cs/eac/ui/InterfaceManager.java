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

import java.util.*;
import java.awt.event.*;
import java.awt.*;

import edu.indiana.cs.eac.hardware.*;
import edu.indiana.cs.ga.snakeEvolver.*;
import edu.indiana.cs.testing.ui.*;


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
		
	}
	public static final InterfaceManager getInstance()
	{
		if(instance == null) { instance = new InterfaceManager(); }
		return instance;
	}
	
	
	
	
	
	
	/**
	 * Needs to be here because the Constructor will overflow with the 
	 * complex call hierarchy.
	 *
	 */
	public void init()
	{
		// attempts to change the look-and-feel must be processed before
		//  Swing components are instantiated
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

		// build the UI skeleton 
		mainWindow = new JFrame();
		desktop = new MDIDesktopPane();
		desktop.setBackground(desktopColor);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().add(desktop);
		mainWindow.getContentPane().setLayout(new BorderLayout());
		mainWindow.getContentPane().add(scrollPane, BorderLayout.CENTER);
	
//		TODO: Add statusbar
//		getContentPane().add(new StatusBarManager(), BorderLayout.SOUTH);

		// hook up the menu manager
		MenuManager menu = MenuManager.getInstance();
//		MenuManager menu = (MenuManager)MenuManager.getInstance(MenuManager.class);
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

		// this how we register with the MDI manager
//		newMenu.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) {
//				desktop.add(new TextFrame());
//			}
//		});
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
}