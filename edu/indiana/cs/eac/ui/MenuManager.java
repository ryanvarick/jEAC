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
import edu.indiana.cs.eac.ui.deprecated.*;
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
public class MenuManager implements Manager
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

		/* file menu */
		JMenu fileMenu = new FileMenu();
		menu.add(fileMenu);

		/* view menu */
		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic('V');
		
		JCheckBoxMenuItem cpItem = new JCheckBoxMenuItem("Device Controller");
		cpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.ALT_MASK));
		cpItem.setIcon(JEACUtilities.getImageIcon("icon-controller.png"));
		cpItem.setMnemonic('C');
		viewMenu.add(cpItem);
		
		viewMenu.add(new JSeparator());
		
		JCheckBoxMenuItem dataItem = new JCheckBoxMenuItem("Data");
//		evolverMenuItem.addActionListener(new EvolverListener());
		dataItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, KeyEvent.ALT_MASK));
		dataItem.setMnemonic('1');
		viewMenu.add(dataItem);

		JCheckBoxMenuItem gradient2DItem = new JCheckBoxMenuItem("2D Visualization");
//		gradient2DMenuItem.addActionListener(new NewGradientModeListener());
		gradient2DItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, KeyEvent.ALT_MASK));
		gradient2DItem.setMnemonic('2');
		gradient2DItem.setSelected(false);
		viewMenu.add(gradient2DItem);

		JCheckBoxMenuItem gradient3DItem = new JCheckBoxMenuItem("3D Visualization");
//		gradient3DMenuItem.addActionListener(new NewGradientModeListener());
		gradient3DItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, KeyEvent.ALT_MASK));
		gradient3DItem.setMnemonic('3');
		gradient3DItem.setSelected(true);
		viewMenu.add(gradient3DItem);

		viewMenu.add(new JSeparator());

		JCheckBoxMenuItem llaInspectorItem = new JCheckBoxMenuItem("LLA Inspector");
//		llaViewerMenuItem.addActionListener(new LLAViewerListener());
		llaInspectorItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.ALT_MASK));
		llaInspectorItem.setIcon(JEACUtilities.getImageIcon("icon-inspector.png"));
		llaInspectorItem.setMnemonic('L');
		llaInspectorItem.setState(false);
		viewMenu.add(llaInspectorItem);

		menu.add(viewMenu);
		
		/* tools menu */
//		JMenu toolMenu = new JMenu("Tools");
//		
//		toolMenu.add(new JSeparator());
//				
//		JMenuItem deviceManagerMenuItem = new JMenuItem("Device Manager");
//		toolMenu.add(deviceManagerMenuItem);
//		
//		JMenuItem evolverMenuItem2 = new JMenuItem("Evolver");
//		toolMenu.add(evolverMenuItem2);
//		
//		JMenuItem llaEditorMenuItem = new JMenuItem("LLA Editor");
//		toolMenu.add(llaEditorMenuItem);
//		
//		menu.add(toolMenu);
		
		/* window menu */
		JMenu windowMenu = new WindowMenu();
		windowMenu.setText("Window");
		windowMenu.setMnemonic('W');
		menu.add(windowMenu);
		
		/* help menu */
		menu.add(getHelpMenu());
	
	}
	
	/**
	 * 
	 * @return
	 */
	public JMenuBar getMenu()
	{
		return menu;
	}
	
	
	
	
	/**
	 * Builds the Help menu.
	 * 
	 * @return   Help menu.
	 * 
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 * 
	 */
	private JMenu getHelpMenu()
	{
		JMenu menu = new JMenu("Help");
		menu.setMnemonic('H');
		
		JMenuItem help_f1 = new JMenuItem("Using jEAC", 'U');
		help_f1.addActionListener(new LauncherListener(JEACUtilities.JEAC_HELP_URL));
		help_f1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		help_f1.setIcon(JEACUtilities.getImageIcon("icon-help.png"));
		menu.add(help_f1);
		
		menu.add(new JSeparator());
		
		JMenuItem releaseNotes = new JMenuItem("Release Notes", 'R');
		releaseNotes.addActionListener(new LauncherListener(JEACUtilities.JEAC_RELEASENOTES_URL));
		menu.add(releaseNotes);

		JMenuItem projectHomepage = new JMenuItem("Project Homepage", 'P');
		projectHomepage.addActionListener(new LauncherListener(JEACUtilities.JEAC_HOMEPAGE_URL));
		menu.add(projectHomepage);
		
		menu.add(new JSeparator());

		JMenuItem about = new JMenuItem("About jEAC", 'A');
		about.addActionListener(new AboutMenuItemListener());
		menu.add(about);
		
		return menu;
	}
	

	
	


	private class ConsoleListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			FileDialog fd = new FileDialog(im.getWindow());
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
//			int result = fileChooser.showOpenDialog(im.getDesktop());
			
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
			
			im.getDesktop().add(new TextFrame2(file));
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
	private class FileMenu extends DynamicMenu
	{
		private JMenuItem connectItem, loadItem;
		private JMenuItem saveItem, saveAsItem;
		private JMenuItem resetItem, disconnectItem;
		private JMenuItem exitItem;
		
		public FileMenu()
		{
			setText("File");
			setMnemonic('F');
		}
		

		
		
		protected void buildMenu()
		{
			
//			connectItem = new JMenuItem("Connect to device", 'C');
//			connectItem.addActionListener(new ConnectionListener());
//			connectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.ALT_MASK));
//			connectItem.setIcon(new BlankIcon(16, 16));
//			add(connectItem);
			
			loadItem = new JMenuItem("Load configuration...", 'O');
//			loadItem.addActionListener(new LoadListener());
			loadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
//			loadItem.setIcon(JEACUtilities.getImageIcon("icon-open.png"));
			add(loadItem);
			
			add(new JSeparator());
			
			saveItem = new JMenuItem("Save configuratoin", 'S');
//			saveItem.addActionListener(new FileIOListener());
			saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
			saveItem.setIcon(JEACUtilities.getImageIcon("icon-save.png"));
			add(saveItem);
			
			saveAsItem = new JMenuItem("Save configuration as...", 'A');
//			saveAsItem.addActionListener(new FileIOListener());
//			saveAsItem.setIcon(new BlankIcon(16, 16));
			add(saveAsItem);
			
//			add(new JSeparator());
			
//			resetItem = new JMenuItem("Reset", 'R');
//			resetItem.addActionListener(new ResetListener());
//			resetItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK));
//			resetItem.setIcon(new BlankIcon(16, 16));
//			add(resetItem);
			
//			disconnectItem = new JMenuItem("Disconnect", 'D');
//			disconnectItem.addActionListener(new DisconnectListener());
//			disconnectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.ALT_MASK));
//			disconnectItem.setIcon(new BlankIcon(16, 16));
//			add(disconnectItem);
			
			add(new JSeparator());
			
			exitItem = new JMenuItem("Exit", 'X');
			exitItem.addActionListener(new ExitListener());
//			exitItem.setIcon(new BlankIcon(16, 16));
			add(exitItem);
		}
		
//		protected void buildMenu()
//		{			
//			InterfaceManager ui = im;
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
	private class WindowMenu extends DynamicMenu
	{
		private JMenuItem cascadeMenuItem, tileMenuItem;
		/* Sets up the children menus depending on the current desktop state */
	    protected void buildMenu()
	    {
	    	// FIXME: Testing only--remove before release!
	    	JMenuItem consoleMenuItem = new JMenuItem("MDI Tester...");
			consoleMenuItem.addActionListener(new ConsoleListener());
			add(consoleMenuItem);
			add(new JSeparator());
	    	
	        // cascade menu
	        cascadeMenuItem = new JMenuItem("Cascade");
	        cascadeMenuItem.addActionListener(new ActionListener()
	        {
	            public void actionPerformed(ActionEvent ae)
	            {
	                im.getDesktop().cascadeFrames();
	            }
	        });

	        // tile menu
	        tileMenuItem = new JMenuItem("Tile");
	        tileMenuItem.addActionListener(new ActionListener()
	        {
	            public void actionPerformed(ActionEvent ae)
	            {
	                im.getDesktop().tileFrames();
	            }
	        });
	    	
	    	ExtendedJCheckBoxMenuItem menu;
	        JInternalFrame[] array = im.getDesktop().getAllFrames();

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




	public void update()
	{
		// TODO Auto-generated method stub
		
	}

}
