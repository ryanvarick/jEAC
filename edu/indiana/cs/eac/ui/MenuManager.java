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
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import ec.display.Console;
import edu.indiana.cs.eac.*;
//import edu.indiana.cs.eac.ui.listeners.*;
import edu.indiana.cs.eac.gradient.*;
import edu.indiana.cs.eac.hardware.*;
import edu.indiana.cs.eac.ui.listeners.*;
import edu.indiana.cs.testing.ui.*;

/**
 * Menu layout and event manager.
 * 
 * <p>This class defines the layout and behavior of the jEAC menubar.  It 
 * contains a number of listeners to handle menu events.  If you're trying to
 * hook or modify a menu event, this is the class you're looking for.
 * 
 * <p>The constructor caches the <code>InterfaceManager</code>, so that listeners 
 * can alter the rest of the interface.  Generally, menu events that alter the
 * interface only originate here.  The event will likely be handled by a method
 * in <code>InterfaceManager</code>. 
 * 
 * @author   Ryan R. Varick
 * @since    2.0.0
 *
 */
public class MenuManager
{
	private JMenuBar menu;
	private InterfaceManager im;
	
	private MenuManager()
	{
		
	}
	public MenuManager(InterfaceManager im)
	{
		this.im = im;
	}
	


	// constructor
	public void init()
	{
		menu = new JMenuBar();

		/* DEVICE MENU */
		JMenu deviceMenu = new JEACMenuManager();
		deviceMenu.setText("jEAC");
		deviceMenu.setMnemonic('J');

//		JMenu connectMenu = 
//		connectMenu.setMnemonic('C');
//		deviceMenu.add(connectMenu);
//		
//		JMenuItem loadMenuItem = new JMenuItem("Load configuration...", 'L');
////		loadMenuItem.addActionListener(new FileIOListener(this));
//		loadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
//		loadMenuItem.setIcon(new ImageIcon(JEAC_Reference.getImage("icon_open.gif")));
//		loadMenuItem.setEnabled(false);
//		deviceMenu.add(loadMenuItem);
//		
//		deviceMenu.add(new JSeparator());
//
//		JMenuItem saveMenuItem = new JMenuItem("Save configuration", 'S');
////		saveMenuItem.addActionListener(new FileIOListener(this));
//		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
//		saveMenuItem.setIcon(new ImageIcon(JEAC_Reference.getImage("icon_save.gif")));
//		saveMenuItem.setEnabled(false);
//		deviceMenu.add(saveMenuItem);
//		
//		JMenuItem saveAsMenuItem = new JMenuItem("Save configuration as...", 'A');
//		saveAsMenuItem.setIcon(new BlankIcon(16, 16));
////		saveAsMenuItem.addActionListener(new FileIOListener(this));
//		saveAsMenuItem.setEnabled(false);
//		deviceMenu.add(saveAsMenuItem);
//		
//		deviceMenu.add(new JSeparator());
//		
////		ledMenuItem = new JCheckBoxMenuItem("uEAC LEDs");
//////		ledMenuItem.addActionListener(new LEDListener());
////		ledMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.ALT_MASK));
////		ledMenuItem.setMnemonic('L');
////		ledMenuItem.setEnabled(false);
////		deviceMenu.add(ledMenuItem);
//
//		JMenuItem resetMenuItem = new JMenuItem("Reset");
////		resetMenuItem.addActionListener(new TestListener());
//		resetMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK));
//		resetMenuItem.setIcon(new ImageIcon(JEAC_Reference.getImage("icon_reset.png")));
//		resetMenuItem.setMnemonic('R');
//		resetMenuItem.setEnabled(false);
//		deviceMenu.add(resetMenuItem);
//
//		JMenuItem disconnectMenuItem = new JMenuItem("Disconnect", 'D');
////		disconnectMenuItem.addActionListener(new DisconnectListener());
//		disconnectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_MASK));
//		disconnectMenuItem.setIcon(new ImageIcon(JEAC_Reference.getImage("icon_disconnect.png")));
//		deviceMenu.add(disconnectMenuItem);
//
//		deviceMenu.add(new JSeparator());
//		
//		JMenuItem exitMenuItem = new JMenuItem("Exit", 'X');
//		exitMenuItem.addActionListener(new ExitListener());
//		exitMenuItem.setIcon(new BlankIcon(16, 16));
//		exitMenuItem.setEnabled(true);
//		deviceMenu.add(exitMenuItem);


		menu.add(deviceMenu);
		
		
		
		/* ---------------[ Tools ]--------------- */
		
		JMenu viewMenu = new JMenu("Data");
		viewMenu.setMnemonic('A');
		
		// tools > evolver
		JCheckBoxMenuItem evolverMenuItem = new JCheckBoxMenuItem("Raw Data");
//		evolverMenuItem.addActionListener(new EvolverListener());
		evolverMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_MASK));
		evolverMenuItem.setIcon(new BlankIcon(16, 16));
		evolverMenuItem.setMnemonic('E');
		viewMenu.add(evolverMenuItem);
		
		viewMenu.add(new JSeparator());

		JCheckBoxMenuItem gradient2DMenuItem = new JCheckBoxMenuItem("2D Gradient");
//		gradient2DMenuItem.addActionListener(new NewGradientModeListener());
		gradient2DMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, KeyEvent.ALT_MASK));
		gradient2DMenuItem.setMnemonic('2');
		gradient2DMenuItem.setSelected(false);
		viewMenu.add(gradient2DMenuItem);

		JCheckBoxMenuItem gradient3DMenuItem = new JCheckBoxMenuItem("3D Gradient");
//		gradient3DMenuItem.addActionListener(new NewGradientModeListener());
		gradient3DMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, KeyEvent.ALT_MASK));
		gradient3DMenuItem.setMnemonic('3');
		gradient3DMenuItem.setSelected(true);
		viewMenu.add(gradient3DMenuItem);

		viewMenu.add(new JSeparator());
		
		// tools > lla inspector
		JCheckBoxMenuItem llaViewerMenuItem = new JCheckBoxMenuItem("LLA Inspector");
//		llaViewerMenuItem.addActionListener(new LLAViewerListener());
		llaViewerMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_MASK));
		llaViewerMenuItem.setIcon(new ImageIcon(JEAC_Reference.getImage("icon_inspector.gif")));
		llaViewerMenuItem.setMnemonic('L');
		llaViewerMenuItem.setState(false);
		viewMenu.add(llaViewerMenuItem);
		

		menu.add(viewMenu);
		
		
		
		/* --- tools */ 
		
		JMenu toolMenu = new JMenu("Tools");
		
		JMenuItem consoleMenuItem = new JMenuItem("Window Manager Test...");
		consoleMenuItem.addActionListener(new ConsoleListener());
		toolMenu.add(consoleMenuItem);
		
		toolMenu.add(new JSeparator());
		

		
		JMenuItem deviceManagerMenuItem = new JMenuItem("Device Manager");
		toolMenu.add(deviceManagerMenuItem);
		
		JMenuItem evolverMenuItem2 = new JMenuItem("Evolver");
		toolMenu.add(evolverMenuItem2);
		
		JMenuItem llaEditorMenuItem = new JMenuItem("LLA Editor");
		toolMenu.add(llaEditorMenuItem);
		
		menu.add(toolMenu);
		
		
		

				
	
		// FIXME: Incorporate into the code better
		JMenu windowMenu = new WindowMenuManager();
		windowMenu.setText("Window");
		menu.add(windowMenu);
		
		
		/* ---------------[ Help  ]--------------- */
		
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');
		
		JMenuItem releaseNotesMenuItem = new JMenuItem("Release Notes", 'R');
		// listener
		helpMenu.add(releaseNotesMenuItem);
		
		helpMenu.add(new JSeparator());

		JMenuItem aboutMenuItem = new JMenuItem("About", 'A');
		aboutMenuItem.addActionListener(new AboutMenuItemListener());

		helpMenu.add(aboutMenuItem);
		menu.add(helpMenu);
	
	}
	
	/**
	 * 
	 * @return
	 */
	public JMenuBar getMenu()
	{
		return menu;
	}
	
	
	
	
	
	
	

	
	


	private class ConsoleListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			FileDialog fd = new FileDialog(InterfaceManager.getInstance().getWindow());
			fd.setTitle("Open");
			fd.setDirectory(System.getProperty("user.dir"));
			fd.setMode(FileDialog.LOAD);
			fd.show();
			
			String f = fd.getDirectory() + fd.getFile();
			if(f == null) return;
			
			File file = new File(f);
			System.out.println("Trying to open:  " + f);
			
			// pop up the file dialog
//			JFileChooser fileChooser = new JFileChooser();
//			fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
//			fileChooser.changeToParentDirectory();
//			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
////			fileChooser.setFileFilter(new FileFilter());
//			int result = fileChooser.showOpenDialog(InterfaceManager.getInstance().getDesktop());
			
			// handle cancel; otherwise, load up the file
//			if (result == JFileChooser.CANCEL_OPTION) return;
//			File fileName = fileChooser.getSelectedFile();
			
			// try to load the file
//			try 
//			{ 
//				loadConfiguration(new FileInputStream(fileName));
//			}
//			catch(IOException e)
//			{
////				System.err.println("Exception: " + e.getMessage());
//				e.printStackTrace();
//				JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//				return;
//			}
			
			InterfaceManager.getInstance().getDesktop().add(new TextFrame2(file));
		}
	}
	
	

		
	/**
	 * Manages the list of available devices.
	 * 
	 * <p>Managing the list of devices is a tricky task, and was one of the
	 * most hack-ish parts of jEACv1.  
	 *
	 * @author Varick
	 *  
	 *  TODO: externilize inner class (???)
	 *
	 */
	private class JEACMenuManager extends DynamicMenuManager
	{
		protected void buildMenu()
		{
			JMenuItem cxn = new JMenuItem();
			
			
			Device d = im.getActiveDevice();
			
			if(d == null)
			{
				cxn.setText("Connect to device");
				cxn.setEnabled(false);
			}
			else
			{
				cxn.setText("Connect to " + d.getDeviceName());
				cxn.setEnabled(true);
//				addActionListener(...);
			}
			
			add(cxn);
			
		}	
//		protected void buildMenu()
//		{			
//			InterfaceManager ui = InterfaceManager.getInstance();
//			
//			Device[][] devices = ui.getDevices();
//			int keyCounter = 0;
//			for(int i = 0; i < devices.length; i++)
//			{
//				for(int j = 0; j < devices[i].length; j++)
//				{
//					Device d = devices[i][j];
//					System.out.println("adding device:  " + d.getTitle());
//					
//					// create the menu item (and store the Device reference)
//					ExtendedJCheckBoxMenuItem menuItem = new ExtendedJCheckBoxMenuItem(d);
//					menuItem.addActionListener(new ConnectMenuEventListener());
//					menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0 + keyCounter++, KeyEvent.CTRL_MASK));
//					menuItem.setText(d.getTitle());
//					
//					add(menuItem);
//				}
//				add(new JSeparator());
//			}
//		
//			add(new JMenuItem("Rescan"));
//			// TODO: Add listener
//
//		}	
	}
	
	
	
	
	/**
	 * Menu component that handles the functionality expected of a standard
	 * "Windows" menu for MDI applications.
	 * 
	 *  TODO:  Integrate code better
	 */
	private class WindowMenuManager extends DynamicMenuManager
	{
		private JMenuItem cascadeMenuItem, tileMenuItem;

		/* Sets up the children menus depending on the current desktop state */
	    protected void buildMenu()
	    {
	        // cascade menu
	        cascadeMenuItem = new JMenuItem("Cascade");
	        cascadeMenuItem.addActionListener(new ActionListener()
	        {
	            public void actionPerformed(ActionEvent ae)
	            {
	                InterfaceManager.getInstance().getDesktop().cascadeFrames();
	            }
	        });

	        // tile menu
	        tileMenuItem = new JMenuItem("Tile");
	        tileMenuItem.addActionListener(new ActionListener()
	        {
	            public void actionPerformed(ActionEvent ae)
	            {
	                InterfaceManager.getInstance().getDesktop().tileFrames();
	            }
	        });
	    	
	    	ExtendedJCheckBoxMenuItem menu;
	        JInternalFrame[] array = InterfaceManager.getInstance().getDesktop().getAllFrames();

	        add(cascadeMenuItem);
	        add(tileMenuItem);
	        if (array.length > 0) addSeparator();
	        cascadeMenuItem.setEnabled(array.length > 0);
	        tileMenuItem.setEnabled(array.length > 0);

	        for (int i = 0; i < array.length; i++) {
	            menu = new ExtendedJCheckBoxMenuItem(array[i]);
	            menu.setText(array[i].getTitle());
	            menu.setState(i == 0);
	            menu.addActionListener(new ActionListener() {
	                public void actionPerformed(ActionEvent ae) {
	                    JInternalFrame frame = (JInternalFrame)((ExtendedJCheckBoxMenuItem)ae.getSource()).getReference();
	                    frame.moveToFront();
	                    try {
	                        frame.setSelected(true);
	                    } catch (PropertyVetoException e) {
	                        e.printStackTrace();
	                    }
	                }
	            });
	            menu.setIcon(array[i].getFrameIcon());
	            add(menu);
	        }
	    }

	}

}
