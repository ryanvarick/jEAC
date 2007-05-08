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

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import javax.swing.*;

import edu.indiana.cs.eac.*;
import edu.indiana.cs.testing.ui.*;
import edu.indiana.cs.eac.ui.listeners.*;



/**
 * Handles menu-related events.
 * 
 * <p>This class defines the layout and behavior of the <i>jEAC</i> main menu.
 * It acts as a <code>Manager</code>; however, it does not implement the
 * <code>update()</code> method since dynamic components are built automatically
 * on demand in response to menu events.  In other words, this class does not
 * care about on-screen UI events until a menu event is recieved.  Even then,
 * only a portion of the overall menu structure is generated.
 * 
 * <p>Note that this does <b>not</b> mean menu events are <i>processed</i> here.
 * This class respects the "no inner listener" rule.  That is, event handling
 * is handled by classes in the <code>ui.listeners</code> package.
 * 
 * <p>To sum up:  for menu structure and generation, look here.  For event
 * processing, look in the aforementioned <code>listeners</code> package.
 * 
 * @author   Ryan R. Varick
 * @since    2.0.0
 *
 */
public class MenuManager implements Manager
{
	private InterfaceManager im;
	private JMenuBar menu;
	
	
	
	/* -------------------------[ Generic class methods ]------------------------- */

	/**
	 * Private constructor.
	 * 
	 * <p>The default constructor is hidden, since this class requires an
	 * <code>InterfaceManager</code> callback reference.
	 * 
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 * 
	 */
	private MenuManager()
	{
		/* Do nothing. */
	}
	
	/**
	 * Returns a new <code>InterfaceManager</code> instance.
	 * 
	 * @param im   Current <code>InterfaceManager</code> instance.
	 * 
	 * @author     Ryan R. Varick
	 * @since      2.0.0
	 * 
	 * TODO: Register with InterfaceManager.
	 * 
	 */
	public MenuManager(InterfaceManager im)
	{
		this.im = im;
	}
	
	// interface method: Manager
	public void init()
	{
		// even though we ignore update() events, we'll register anyway
		im.registerManager(this);
		
		// initialize the main menu
		menu = new JMenuBar();
		menu.add(getFileMenu());
		menu.add(getViewMenu());
		menu.add(getWindowMenu());
		menu.add(getHelpMenu());
	}
	
	// interface method: Manager
	public void update()
	{
		/* Do nothing (see class declaration). */ 
	}
	
	
	
	/* -------------------------[ MenuManager methods ]------------------------- */
	
	private JMenu getFileMenu()
	{
		return new FileMenu();
	}

	private JMenu getViewMenu()
	{
		return new ViewMenu();
	}
	
	private JMenu getWindowMenu()
	{
		return new WindowMenu();
	}

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
		about.addActionListener(new AboutMenuItemListener(im));
		menu.add(about);
		
		return menu;
	}
	
	
	
	/* -------------------------[ Get/set methods ]------------------------- */

	/**
	 * Returns the menu.
	 * 
	 * @return   The menu.
	 * 
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 * 
	 */
	public JMenuBar getMenu()
	{
		return menu;
	}

	
	
	
	
	
	
	
	/* =========================[ Inner classes ]========================= */

	/** @deprecated This is just a test method. */
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
	
	

		
	// TODO: add listeners
	private class FileMenu extends DynamicMenu
	{
		public FileMenu()
		{
			setText("File");
			setMnemonic('F');
		}		
		
		protected void buildMenu()
		{
			boolean isEnabled = im.isSelectedDeviceActive();
			
			JMenuItem load = new JMenuItem("Load configuration...", 'O');
			load.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
			load.setEnabled(isEnabled);
			add(load);
			
			add(new JSeparator());
			
			JMenuItem save = new JMenuItem("Save configuration", 'S');
			save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
			save.setEnabled(isEnabled);
			save.setIcon(JEACUtilities.getImageIcon("icon-save.png"));
			add(save);
			
			JMenuItem saveAs = new JMenuItem("Save configuration as...", 'A');
			saveAs.setEnabled(isEnabled);
			add(saveAs);
			
			add(new JSeparator());
			
			JMenuItem exit = new JMenuItem("Exit", 'X');
			exit.addActionListener(new ExitListener());
			add(exit);
		}
		
	}
	
	// TODO: checkbox toggling
	// TODO: add listeners
	private class ViewMenu extends DynamicMenu
	{
		public ViewMenu()
		{
			setText("View");
			setMnemonic('V');
		}
		
		protected void buildMenu()
		{
			boolean isEnabled = im.isSelectedDeviceActive();

			JCheckBoxMenuItem controller = new JCheckBoxMenuItem("Device Controller");
			controller.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.ALT_MASK));
			controller.setEnabled(isEnabled);
			controller.setIcon(JEACUtilities.getImageIcon("icon-controller.png"));
			controller.setMnemonic('C');
			add(controller);
			
			add(new JSeparator());
			
			JCheckBoxMenuItem data = new JCheckBoxMenuItem("Data");
//			evolverMenuItem.addActionListener(new EvolverListener());
			data.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, KeyEvent.ALT_MASK));
			data.setEnabled(isEnabled);
			data.setMnemonic('1');
			add(data);

			JCheckBoxMenuItem gradient2D = new JCheckBoxMenuItem("2D Visualization");
//			gradient2DMenuItem.addActionListener(new NewGradientModeListener());
			gradient2D.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, KeyEvent.ALT_MASK));
			gradient2D.setEnabled(isEnabled);
			gradient2D.setMnemonic('2');
			gradient2D.setSelected(false);
			add(gradient2D);

			JCheckBoxMenuItem gradient3D = new JCheckBoxMenuItem("3D Visualization");
//			gradient3DMenuItem.addActionListener(new NewGradientModeListener());
			gradient3D.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, KeyEvent.ALT_MASK));
			gradient3D.setEnabled(isEnabled);
			gradient3D.setMnemonic('3');
			gradient3D.setSelected(true);
			add(gradient3D);

			add(new JSeparator());

			JCheckBoxMenuItem llaInspector = new JCheckBoxMenuItem("LLA Inspector");
//			llaViewerMenuItem.addActionListener(new LLAViewerListener());
			llaInspector.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.ALT_MASK));
			llaInspector.setEnabled(isEnabled);
			llaInspector.setIcon(JEACUtilities.getImageIcon("icon-inspector.png"));
			llaInspector.setMnemonic('L');
			llaInspector.setState(false);
			add(llaInspector);
		}
		
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
		
		public WindowMenu()
		{
			setText("Window");
			setMnemonic('W');
		}
		
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
	
}
