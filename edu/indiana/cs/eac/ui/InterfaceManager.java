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
public class InterfaceManager extends JFrame
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
	
	private MDIDesktopPane desktop;
	
	private Device[][] validDevices;




	public MDIDesktopPane getDesktop()
	{
		return desktop;
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
		desktop = new MDIDesktopPane();
		desktop.setBackground(desktopColor);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().add(desktop);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrollPane, BorderLayout.CENTER);
	
//		TODO: Add statusbar
//		getContentPane().add(new StatusBarManager(), BorderLayout.SOUTH);

		// hook up the menu manager
		MenuManager menu = MenuManager.getInstance();
//		MenuManager menu = (MenuManager)MenuManager.getInstance(MenuManager.class);
		setJMenuBar(menu.getMenu());
		
		// specify UI appearance and behavior
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);
		setSize(new Dimension(INITIAL_SIZE_X, INITIAL_SIZE_Y));
		setTitle(APPLICATION_TITLE);

		// center UI on screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width - getWidth()) / 2;
		int y = (screenSize.height - getHeight()) / 2;
		setLocation(new Point(x, y));
		
		// show the world our beautiful creation
		setVisible(true);

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
	
	/**
	 * ...
	 * 
	 * @param deviceManager   .
	 * 
	 * @author                Ryan R. Varick
	 * @since                 2.0.0
	 * 
	 */
	private void loadDrivers()
	{
		// grab the raw list of devices
		HardwareManager hm = HardwareManager.getInstance();
		Device[][] knownDevices = hm.getKnownDevices();

		// prepare the loading frame
		LoadingFrame lf = new LoadingFrame(hm.getDeviceCount());

		/*
		 * WARNING! Pain in the ass zone ahead!
		 * 
		 * We need to verify the devices returned by the hardware manager.
		 * Since it is possible likely that a number of devices (or entire
		 * driver classes) will fail to verify, we cannot allocate the 
		 * verifiedDevices array ahead of time.
		 * 
		 * We could use vectors here, but that involves a lot of superflous
		 * casting.  Generics don't help because of type erasure.  So instead
		 * we'll use a combination of counters and parameterized methods to
		 * build the valid devices array.  There's probably a much simpler way
		 * to do this. 
		 * 
		 */
//		Vector<Device[]> checkedDevices = new Vector<Device[]>();
//		int goodDriverCnt = 0;
//		
//		for(int i = 0; i < knownDevices.length; i++)
//		{
//			Vector<Device> goodDevices = new Vector<Device>();
//			int goodDeviceCnt = 0;
//			
//			for(int j = 0; j < knownDevices[i].length; j++)
//			{
//				
//				if(knownDevices[i][j].isValid())
//				{
//					goodDevices.add(knownDevices[i][j]);
//					goodDeviceCnt++;
//				}
//				lf.increment();
//			}
//			
//			Device[] dok = new Device[goodDeviceCnt];
//			dok = goodDevices.toArray(dok);
//			
//			checkedDevices.add(dok);
//		}
//		
//		lf.dispose();
//		validDevices = (Device[][])checkedDevices.toArray();
		
		validDevices = knownDevices;
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