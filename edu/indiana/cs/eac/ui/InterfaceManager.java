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

//based on http://www.javaworld.com/javaworld/jw-05-2001/jw-0525-mdi.html

package edu.indiana.cs.eac.ui;

import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

import edu.indiana.cs.eac.testing.ui.*;
import ec.display.*;

/**
 * Multi-document interface (MDI) manager.
 * 
 * <p>This class implements an MDI manager.  
 * 
 * @author Ryan R. Varick
 * @since  1.3.0
 *
 */
public class InterfaceManager extends JFrame
{
	/* defaults (do not alter directly; use the API instead) */
	private boolean useNativeLAF = true;
	
	private Color desktopColor = Color.GRAY.brighter();
	
	private static final String APPLICATION_TITLE = "jEAC - An integrated cross-platform EAC development environment";
	
	/** Initial width of the UI. */
	public static int INITIAL_SIZE_X = 640;
	
	/** Initial height of the UI. */
	public static int INITIAL_SIZE_Y = 480;
	
//	/** Initial x-coordinate of the UI. */
//	public static int INITIAL_LOCATION_X = 50;
//	
//	/** Initial y-coordinate of the UI. */
//	public static int INITIAL_LOCATION_Y = 50;

	
	/* UI components (do not alter) */
	private MDIDesktopPane desktop;

	private JMenuBar menuBar;

	
	
	public InterfaceManager()
	{
		if(useNativeLAF)
		{
			try
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} 
			catch(Exception e)
			{
				System.err.println("Unable to load native look-and-feel.");
			}
		}

		// build the UI skeleton 
		desktop = new MDIDesktopPane();
		desktop.setBackground(desktopColor);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().add(desktop);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		// hook up the menu manager
		MenuManager menu = new MenuManager(this);
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
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		InterfaceManager jeac = new InterfaceManager();
	}


}