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

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import ec.display.Console;
import edu.indiana.cs.eac.*;
//import edu.indiana.cs.eac.ui.listeners.*;
import edu.indiana.cs.eac.gradient.*;
import edu.indiana.cs.eac.driver.*;
import edu.indiana.cs.eac.testing.ui.*;

/**
 * Menu event manager.
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
	
	private InterfaceManager ui;
	
	private JMenuBar menu;
	
	private JMenu connectMenu, runMenu;
	private ButtonGroup driverButtonGroup;
	private ButtonModel hack_driverDeselectionModel;
	private JMenuItem loadMenuItem, saveMenuItem, saveAsMenuItem;
	private JMenuItem disconnectMenuItem;
	private JMenuItem resetMenuItem;
//	private JMenuItem evolverMenuItem;
	private JCheckBoxMenuItem gradient2DMenuItem, gradient3DMenuItem, ledMenuItem;
	private JCheckBoxMenuItem evolverMenuItem, llaViewerMenuItem;

	private Console evolverFrame;
	private LLAInspectorFrame llaFrame;

	private Gradient3D gradient3DPanel;
	private Gradient2D gradient2DPanel;
	private NodeMap    nodemap;
	private StatusBarManager  statusbar;
	


	// constructor
	public MenuManager()
	{
		// cache the InterfaceManager--we need to know where to send menu events
		ui = InterfaceManager.getInstance();
		
		menu = new JMenuBar();
		
		
		
		/* ---------------[ JEAC menu ]--------------- */
		
		JMenu jeacMenu = new JMenu("jEAC");
		jeacMenu.setMnemonic('J');


		// jeac > load
		loadMenuItem = new JMenuItem("Load configuration...", 'L');
//		loadMenuItem.addActionListener(new FileIOListener(this));
		loadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
		loadMenuItem.setIcon(new ImageIcon(JEAC_Reference.getImage("icon_open.gif")));
		loadMenuItem.setEnabled(false);
		jeacMenu.add(loadMenuItem);
		
		jeacMenu.add(new JSeparator());

		
		// jeac > save
		saveMenuItem = new JMenuItem("Save configuration", 'S');
//		saveMenuItem.addActionListener(new FileIOListener(this));
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
		saveMenuItem.setIcon(new ImageIcon(JEAC_Reference.getImage("icon_save.gif")));
		saveMenuItem.setEnabled(false);
		jeacMenu.add(saveMenuItem);
		
		// jeac > save as
		saveAsMenuItem = new JMenuItem("Save configuration as...", 'A');
		saveAsMenuItem.setIcon(new BlankIcon(16, 16));
//		saveAsMenuItem.addActionListener(new FileIOListener(this));
		saveAsMenuItem.setEnabled(false);
		jeacMenu.add(saveAsMenuItem);
		
		jeacMenu.add(new JSeparator());

		// jeac > exit
		JMenuItem exitMenuItem = new JMenuItem("Exit", 'X');
//		exitMenuItem.addActionListener(new ExitListener());
		exitMenuItem.setIcon(new BlankIcon(16, 16));
		exitMenuItem.setEnabled(false);
		jeacMenu.add(exitMenuItem);

		menu.add(jeacMenu);



		/* DEVICE MENU */
		JMenu deviceMenu = new JMenu("Device");
		deviceMenu.setMnemonic('D');

		// device > connect 
		connectMenu = new ConnectMenuManager();
		connectMenu.setText("Connect to EAC");
		connectMenu.setIcon(new BlankIcon(16, 16));
		deviceMenu.add(connectMenu);

		deviceMenu.add(new JSeparator());
		
		ledMenuItem = new JCheckBoxMenuItem("uEAC LEDs");
//		ledMenuItem.addActionListener(new LEDListener());
		ledMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.ALT_MASK));
		ledMenuItem.setMnemonic('L');
		ledMenuItem.setEnabled(false);
		deviceMenu.add(ledMenuItem);

		// device > reset
		resetMenuItem = new JMenuItem("Reset");
//		resetMenuItem.addActionListener(new TestListener());
		resetMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK));
		resetMenuItem.setIcon(new ImageIcon(JEAC_Reference.getImage("icon_reset.png")));
		resetMenuItem.setMnemonic('R');
		resetMenuItem.setEnabled(false);
		deviceMenu.add(resetMenuItem);

		deviceMenu.add(new JSeparator());

		// device > disconnect
		disconnectMenuItem = new JMenuItem("Disconnect", 'D');
//		disconnectMenuItem.addActionListener(new DisconnectListener());
		disconnectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_MASK));
		disconnectMenuItem.setIcon(new ImageIcon(JEAC_Reference.getImage("icon_disconnect.png")));
		deviceMenu.add(disconnectMenuItem);
		

		menu.add(deviceMenu);

		

		
		/* ---------------[ Tools ]--------------- */
		
		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic('V');
		
		JMenuItem consoleMenuItem = new JMenuItem("Console");
		consoleMenuItem.addActionListener(new ConsoleListener());
		viewMenu.add(consoleMenuItem);
		

		// tools > visualization
		JMenu visualizationMenu = new JMenu("Visualization");
		visualizationMenu.setIcon(new BlankIcon(16, 16));
		visualizationMenu.setMnemonic('G');

		gradient2DMenuItem = new JCheckBoxMenuItem("2D Gradient");
//		gradient2DMenuItem.addActionListener(new NewGradientModeListener());
		gradient2DMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, KeyEvent.ALT_MASK));
		gradient2DMenuItem.setMnemonic('2');
		gradient2DMenuItem.setSelected(false);

		gradient3DMenuItem = new JCheckBoxMenuItem("3D Gradient");
//		gradient3DMenuItem.addActionListener(new NewGradientModeListener());
		gradient3DMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, KeyEvent.ALT_MASK));
		gradient3DMenuItem.setMnemonic('3');
		gradient3DMenuItem.setSelected(true);
		
		
	
		visualizationMenu.add(gradient2DMenuItem);
		visualizationMenu.add(gradient3DMenuItem);
		visualizationMenu.add(new JSeparator());
		
		// tools > lla inspector
		llaViewerMenuItem = new JCheckBoxMenuItem("LLA Inspector");
//		llaViewerMenuItem.addActionListener(new LLAViewerListener());
		llaViewerMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_MASK));
		llaViewerMenuItem.setIcon(new ImageIcon(JEAC_Reference.getImage("icon_inspector.gif")));
		llaViewerMenuItem.setMnemonic('L');
		llaViewerMenuItem.setState(false);
		
		// tools > evolver
		evolverMenuItem = new JCheckBoxMenuItem("Evolver (experimental!)");
//		evolverMenuItem.addActionListener(new EvolverListener());
		evolverMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_MASK));
		evolverMenuItem.setIcon(new BlankIcon(16, 16));
		evolverMenuItem.setMnemonic('E');

		// tools > reset
//		resetMenuItem = new JMenuItem("Spawn a new frame");
//		resetMenuItem.addActionListener(new TestListener());
//		resetMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK));
//		resetMenuItem.setIcon(new ImageIcon(JEAC_Reference.getImage("icon_reset.png")));
//		resetMenuItem.setMnemonic('R');
		

		
		// build the tools menu
		viewMenu.add(visualizationMenu);
		viewMenu.add(new JSeparator());
		viewMenu.add(llaViewerMenuItem);
		viewMenu.add(evolverMenuItem);
		viewMenu.add(new JSeparator());
//		toolMenu.add(resetMenuItem);

		menu.add(viewMenu);
		

				
	
		// FIXME: Incorporate into the code better
		JMenu windowMenu = new WindowMenuManager();
		windowMenu.setText("Window");
		menu.add(windowMenu);
		
		
		/* ---------------[ Help  ]--------------- */
		
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');

		JMenuItem about = new JMenuItem("About", 'A');
//		about.addActionListener(new AboutListener());

		helpMenu.add(about);
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
	
	
	
	
	
	
	

	
	

	/* === NOTES!! */
	/*
	 * This would be the perfect place to use generics.
	 * 
	 *   Maybe I'll work on it when I get more practice...
	 * 
	 */
	private class ConsoleListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			InterfaceManager.getInstance().getDesktop().add(new TextFrame());
		}
	}
	
	
	
//	/**
//	 * Adds an object reference to a normal <code>JCheckBoxMenuItem</code>.
//	 * 
//	 * <p>For dynamically generated menus, it is often helpful to tie a menu
//	 * item to a particular object.  This class extends the standard
//	 * <code>JCheckBoxMenuItem</code> by adding a private <code>Object</code> 
//	 * field.  That way, when an <code>ActionEvent</code> is fired, it can be 
//	 * associated with an existing object.
//	 * 
//	 * <p>Note that the dereferencing process can be rather cumbersome.  A 
//	 * typical event handler may look something like the following:
//	 * 
//	 * <p><code>
//	 * public void actionPerformed(ActionEvent ae) { <br>
//	 *     Type obj = (Type)((ExtendedJCheckBoxMenuItem)ae.getSource()).getReference();<br>
//	 *     ...<br>
//	 * }</code>
//	 * 
//	 * <p>Generics could be used to reduce the number of explicit casts, but at
//	 * the expense older JVMs.
//	 * 
//	 * @author   Ryan R. Varick
//	 * @since    2.0.0
//	 * 
//	 */
//	private class ExtendedJCheckBoxMenuItem extends JCheckBoxMenuItem
//    {
//        private Object reference;
//
//        public ExtendedJCheckBoxMenuItem(Object reference)
//        {
//            this.reference = reference;
//        }
//
//        public Object getReference()
//        {
//            return reference;
//        }
//    }
	
	
	/**
	 * Manages the list of available devices.
	 * 
	 * <p>Managing the list of devices is a tricky task, and was one of the
	 * most hack-ish parts of jEACv1.  
	 *
	 * @author Varick
	 *
	 */
	private class ConnectMenuManager extends DynamicMenuManager
	{		
		protected void buildMenu()
		{
			InterfaceManager ui = InterfaceManager.getInstance();
//			
//			Device[][] deviceList = ui.getDeviceList();
//			int keyCounter = 0;
//			for(int i = 0; i < deviceList.length; i++)
//			{
//				for(int j = 0; j < deviceList[i].length; j++)
//				{
//					Device d = deviceList[i][j];
//					
//					// create the menu item (and store the Device reference)
//					ExtendedJCheckBoxMenuItem menuItem = new ExtendedJCheckBoxMenuItem(d);
//					menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0 + keyCounter++, KeyEvent.CTRL_MASK));
//					menuItem.setText(d.getTitle());
//
//					// add the listener
//					menuItem.addActionListener(new ActionListener()
//		            {
//		            	public void actionPerformed(ActionEvent ae)
//		            	{
//		            		Device d = (Device)((ExtendedJCheckBoxMenuItem)ae.getSource()).getReference();
//		            		System.out.println("Event fired:  Device=" + d.getTitle());
//		                }
//		            });
//					
//					add(menuItem);
//				}
//				add(new JSeparator());
//			}
		
			add(new JMenuItem("Rescan"));
			// TODO: Add listener

		}
		
		

		
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
//
//	    /* This JCheckBoxMenuItem descendant is used to track the child frame that corresponds
//	       to a give menu. */
//	    class ChildMenuItem extends JCheckBoxMenuItem
//	    {
//	        private JInternalFrame frame;
//
//	        public ChildMenuItem(JInternalFrame frame) {
//	            super(frame.getTitle());
//	            this.frame=frame;
//	        }
//
//	        public JInternalFrame getFrame() {
//	            return frame;
//	        }
//	    }
	}

}
