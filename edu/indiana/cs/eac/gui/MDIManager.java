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

package edu.indiana.cs.eac.gui;

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
public class MDIManager extends JFrame
{
	private MDIDesktopPane desktop;
	private JScrollPane scrollPane;

	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem newMenu;

	public MDIManager()
	{

		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch(Exception e)
		{
			System.err.println("Unable to load native look-and-feel.");
		}

		desktop = new MDIDesktopPane();
		desktop.setBackground(Color.GRAY.brighter());

		menuBar = new JMenuBar();


		fileMenu = new JMenu("jEAC");
		JMenuItem newMenu = new JMenuItem("New");
		JScrollPane scrollPane = new JScrollPane();

		menuBar.add(fileMenu);

		JMenuItem ecj = new JMenuItem("ECJ");
		ecj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
//				desktop.add(new Console(new String[0]));
				
				Console c = new Console(new String[0]);
				c.setVisible(true);
				
			}});
		
		JMenuItem jeac = new JMenuItem("jEAC");
		jeac.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
//				desktop.add(new Console(new String[0]));
				desktop.add(new JEAC());
			}});
		
		JMenu tools = new JMenu("Tools");
		tools.add(ecj);
		tools.add(jeac);
		
		
		menuBar.add(tools);

		menuBar.add(new WindowMenu(desktop));
		fileMenu.add(newMenu);

		menuBar.add(new JMenu("Help"));

		setJMenuBar(menuBar);
		this.setLocation(new Point(50,50));
		setTitle("jEAC - An integrated EAC development suite");
		scrollPane.getViewport().add(desktop);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// this how we register with the MDI manager
		newMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				desktop.add(new TextFrame());
			}
		});

	}



}