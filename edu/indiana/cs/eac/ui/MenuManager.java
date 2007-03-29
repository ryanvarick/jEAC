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
import edu.indiana.cs.eac.gradient.*;
import edu.indiana.cs.eac.testing.ui.MDIDesktopPane;

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
	public MenuManager(InterfaceManager ui)
	{
		// cache the InterfaceManager--we need to know where to send menu events
		this.ui = ui;
		
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
//		connectMenu = populateDriverMenu();
//		connectMenu.setIcon(new BlankIcon(16, 16));

		deviceMenu.add(new JSeparator());
		
		ledMenuItem = new JCheckBoxMenuItem("uEAC LEDs");
//		ledMenuItem.addActionListener(new LEDListener());
		ledMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.ALT_MASK));
		ledMenuItem.setMnemonic('L');
		ledMenuItem.setEnabled(false);
		deviceMenu.add(ledMenuItem);

		// device > reset
		resetMenuItem = new JMenuItem("Reset");
		resetMenuItem.addActionListener(new TestListener());
		resetMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK));
		resetMenuItem.setIcon(new ImageIcon(JEAC_Reference.getImage("icon_reset.png")));
		resetMenuItem.setMnemonic('R');
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
		
		JMenu toolMenu = new JMenu("Tools");
		toolMenu.setMnemonic('T');

		// tools > visualization
		JMenu visualizationMenu = new JMenu("Visualization");
		visualizationMenu.setIcon(new BlankIcon(16, 16));
		visualizationMenu.setMnemonic('G');

		gradient2DMenuItem = new JCheckBoxMenuItem("2D Gradient");
		gradient2DMenuItem.addActionListener(new NewGradientModeListener());
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
		toolMenu.add(visualizationMenu);
		toolMenu.add(new JSeparator());
		toolMenu.add(llaViewerMenuItem);
		toolMenu.add(evolverMenuItem);
		toolMenu.add(new JSeparator());
//		toolMenu.add(resetMenuItem);

		menu.add(toolMenu);
		
		/* ----- [Experimental] ----- */
		
		JMenu experimentalMenu = new JMenu("Experimental");
		experimentalMenu.setMnemonic('E');
				
	
		// FIXME: Incorporate into the code better
		menu.add(new WindowMenuManager(ui.getDesktop()));
		
		
		/* ---------------[ Help  ]--------------- */
		
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');

		// help > about
		JMenuItem about = new JMenuItem("About", 'A');
//		about.addActionListener(new AboutListener());

		// build the help menu
		helpMenu.add(about);
		menu.add(helpMenu);
				
		/* ---------------[ Finalize menus ]--------------- */
		
		
		
		
	}
	
	/**
	 * 
	 * @return
	 */
	public JMenuBar getMenu()
	{
		return menu;
	}
	
	
	
	
	
	
	
	
	private class NewGradientModeListener implements ActionListener 
	{
		public void actionPerformed(ActionEvent e)
		{
//			ui.toggle2D();
		}
	}
	
	private class TestListener implements ActionListener 
	{
		public void actionPerformed(ActionEvent e)
		{
			System.out.println("Menu event fired.");
			ui.testMethod();
//			ui.toggle2D();
		}
	}
	
	
	
	
	
	
	
	
	private class ConnectMenuManager extends JMenu
	{
		
	}	
	
	
	
	
	/**
	 * Menu component that handles the functionality expected of a standard
	 * "Windows" menu for MDI applications.
	 */
	private class WindowMenuManager extends JMenu
	{
	    private MDIDesktopPane desktop;
	    private JMenuItem cascade=new JMenuItem("Cascade");
	    private JMenuItem tile=new JMenuItem("Tile");

	    public WindowMenuManager(MDIDesktopPane desktop) {
	        this.desktop=desktop;
	        setText("Window");
	        cascade.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent ae) {
	                WindowMenuManager.this.desktop.cascadeFrames();
	            }
	        });
	        tile.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent ae) {
	                WindowMenuManager.this.desktop.tileFrames();
	            }
	        });
	        addMenuListener(new MenuListener() {
	            public void menuCanceled (MenuEvent e) {}

	            public void menuDeselected (MenuEvent e) {
	                removeAll();
	            }

	            public void menuSelected (MenuEvent e) {
	                buildChildMenus();
	            }
	        });
	    }

	    /* Sets up the children menus depending on the current desktop state */
	    private void buildChildMenus() {
	        int i;
	        ChildMenuItem menu;
	        JInternalFrame[] array = desktop.getAllFrames();

	        add(cascade);
	        add(tile);
	        if (array.length > 0) addSeparator();
	        cascade.setEnabled(array.length > 0);
	        tile.setEnabled(array.length > 0);

	        for (i = 0; i < array.length; i++) {
	            menu = new ChildMenuItem(array[i]);
	            menu.setState(i == 0);
	            menu.addActionListener(new ActionListener() {
	                public void actionPerformed(ActionEvent ae) {
	                    JInternalFrame frame = ((ChildMenuItem)ae.getSource()).getFrame();
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

	    /* This JCheckBoxMenuItem descendant is used to track the child frame that corresponds
	       to a give menu. */
	    class ChildMenuItem extends JCheckBoxMenuItem {
	        private JInternalFrame frame;

	        public ChildMenuItem(JInternalFrame frame) {
	            super(frame.getTitle());
	            this.frame=frame;
	        }

	        public JInternalFrame getFrame() {
	            return frame;
	        }
	    }
	}

}
