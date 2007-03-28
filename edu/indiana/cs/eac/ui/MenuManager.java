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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import ec.display.Console;
import edu.indiana.cs.eac.testing.ui.WindowMenu;

public class MenuManager
{
	
	private InterfaceManager ui;
	
	private JMenuBar menu;
	
	private JMenu fileMenu;
	private JMenuItem newMenu;
	
	


	// constructor
	public MenuManager(InterfaceManager ui)
	{
		// cache the InterfaceManager
		this.ui = ui;

//		fileMenu = new JMenu("jEAC");
//		JMenuItem newMenu = new JMenuItem("New");
//		JScrollPane scrollPane = new JScrollPane();
//
//		menuBar.add(fileMenu);
//
//		JMenuItem ecj = new JMenuItem("ECJ");
//		ecj.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) {
////				desktop.add(new Console(new String[0]));
//				
//				Console c = new Console(new String[0]);
//				c.setVisible(true);
//				
//			}});
//		
//		JMenuItem jeac = new JMenuItem("jEAC");
//		jeac.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) {
////				desktop.add(new Console(new String[0]));
//				desktop.add(new JEAC());
//			}});
//		
//		JMenu tools = new JMenu("Tools");
//		tools.add(ecj);
//		tools.add(jeac);
//		
//		
//		menuBar.add(tools);
//
//		menuBar.add(new WindowMenu(desktop));
//		fileMenu.add(newMenu);
//
//		menuBar.add(new JMenu("Help"));	
	}
	
	/**
	 * 
	 * @return
	 */
	public JMenuBar getMenu()
	{
		return menu;
	}
}
