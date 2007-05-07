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
import java.util.*;
import javax.swing.*;

import net.infonode.docking.*;
import net.infonode.docking.theme.*;
import net.infonode.docking.title.*;
import net.infonode.docking.util.*;
import net.infonode.util.*;

import edu.indiana.cs.eac.*;
import edu.indiana.cs.eac.hardware.*;



/**
 * Multi-document interface (MDI) manager.
 * 
 * <p>This class implements an MDI manager.
 * 
 * @author   Ryan R. Varick
 * @since    2.0.0
 *
 */
public class InterfaceManager implements Manager
{
	/* defaults */
	private static boolean USE_NATIVE_LAF = true;
	private DockingWindowsTheme theme = new ShapedGradientDockingTheme();
	
	// TODO: externalize strings
	private static final String APPLICATION_TITLE = "jEAC - Cross-platform EAC development environment";
	private static final String WORKSPACE_TITLE   = "Workspace";

	private static final int INITIAL_WIDTH  = 800;
	private static final int INITIAL_HEIGHT = 600;

	private static final Color BACKGROUND_COLOR = new Color(0xBABDB6);     // Tango aluminum gray
//	private static final Color DESKTOP_COLOR = Color.GRAY.brighter();

	/* defaults (do not alter directly; use the API instead) */

	/* UI components (do not alter) */
	private JFrame frame;
	private MDIDesktopPane workspace;
	
	/* device cache */
	private HardwareManager hm;
	private Device[][] validDevices;
	private HashMap<String, Device> deviceList = new HashMap<String, Device>();
	
	private DevicePanelManager dm;
	private TimingManager tm;
	private MenuManager mm;

	
	
	/**
	 * Returns a new <code>InterfaceManager</code> object.
	 * 
	 * <p>Note that the constructor does not initialize or show the interface.
	 * These tasks are handled by <code>init()</code> and <code>show()</code>,
	 * respectively.  
	 * 
	 * @param hm   <code>HardwareManager</code> to use.
	 * @param tm   <code>TimingManager</code> to use.
	 * 
	 * @author     Ryan R. Varick
	 * @since      2.0.0
	 *
	 */
	public InterfaceManager(HardwareManager hm, TimingManager tm)
	{
		this.hm = hm;
		this.tm = tm;
	}
	
	/**
	 * Builds the workspace area.
	 * 
	 * <p>jEAC uses two kinds of UI containers:  dockable windows and free-
	 * floating windows, which are contained within the <i>workspace</i> area.
	 * The workspace behaves as a standard multi-document interface (MDI).
	 * 
	 * <p><i>Note that the MDI is based on code outlined in a JavaWorld article,
	 * <a href=http://www.javaworld.com/javaworld/jw-05-2001/jw-0525-mdi.html>
	 * Conquer Swing deficiencies in MDI development</a>.</i>
	 * 
	 * @return   MDI desktop component.
	 * 
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 * 
	 */
	private View getWorkspaceView()
	{
		// create the workspace, which is a field because we will need to reference
		//  it later when we start adding windows
		workspace = new MDIDesktopPane();
		workspace.setBackground(BACKGROUND_COLOR);
		
		// add the workspace to a scrollpane
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().add(workspace);
		
		// package the new, scrollable workspace in an InfoNode view
		View workspace = new View(WORKSPACE_TITLE, null, scrollPane);
		
		// turn off default functions to reduce clutter
		workspace.getWindowProperties().setCloseEnabled(false);
		workspace.getWindowProperties().setUndockEnabled(false);
		workspace.getWindowProperties().setMaximizeEnabled(false);
		workspace.getWindowProperties().setMinimizeEnabled(false);
		workspace.getWindowProperties().setDragEnabled(false);
		
		// HACK: without a leading space, the title is truncated (WTF?)
		workspace.getViewProperties().setTitle(" " + WORKSPACE_TITLE);
		
		return workspace;
	}
	
	/**
	 * Returns an array of validated devices, organized by driver class.
	 * 
	 * @return   <code>array[][]</code> of validated devices.
	 * 
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 * 
	 */
	public Device[][] getDevices()
	{
		if(validDevices == null)
		{
			validDevices = validateDevices();
		}
		return validDevices;
	}
		
	/**
	 * Builds the overall UI structure.
	 * 
	 * <p>For a more robust UI, we are using a mix of InfoNode dockable windows
	 * and standard Swing components.  This method defines the overall UI
	 * structure but does <i>NOT</i> populate specific components (such as the
	 * device list).
	 * 
	 * @return   InfoNode component containing the overall UI.
	 * 
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 * 
	 */
	private RootWindow getRootWindow()
	{
		// allocate views (similar to JPanel)
		int view = 1;
		ViewMap viewMap = new ViewMap();

		View workspaceView = getWorkspaceView();
		viewMap.addView(view++, workspaceView);

		View evolverView = new View("Evolver", null, new JLabel("Evolver goes here."));
		viewMap.addView(view++, evolverView);

		View editorView = new View("LLA Editor", null, new MDIDesktopPane());
		viewMap.addView(view++, editorView);

		dm = new DevicePanelManager(this);
		View deviceManagerView = new View("Device Manager", null, dm.getDevicePanel());
		deviceManagerView.getWindowProperties().setMinimizeEnabled(false);
		deviceManagerView.getWindowProperties().setCloseEnabled(false);
		deviceManagerView.getWindowProperties().setUndockEnabled(false);
		deviceManagerView.getWindowProperties().setRestoreEnabled(false);
		deviceManagerView.getWindowProperties().setMaximizeEnabled(false);
		
		viewMap.addView(view++, deviceManagerView);
		
		// allocate main window (similiar to JFrame)
		//  NOTE: RootWindow *must* be allocated before other InfoNode windows
		RootWindow rootWindow = InterfaceManager.createMinimalRootWindow(viewMap);

		// apply InfoNode theme (separate from Swing LAF)
		rootWindow.getRootWindowProperties().addSuperObject(theme.getRootWindowProperties());		

		// specify InfoNode tab layout (similar to JTabbedPane)
		rootWindow.getWindowBar(Direction.DOWN).setEnabled(true);

		// specify view layout (similar to JSplitPane)
		SplitWindow rightWindow = new SplitWindow(false, 0.75f, workspaceView, evolverView);
		SplitWindow mainWindow  = new SplitWindow(true, 0.25f, deviceManagerView, rightWindow);

		rootWindow.setWindow(mainWindow);

		return rootWindow;
	}
	
	
	


	// TODO: Clean up getters, eliminate unnecessary functions
	public DevicePanelManager getDevicePanelManager() { return this.dm; }
	public MDIDesktopPane getDesktop()
	{
		return workspace;
	}
	public JFrame getWindow()
	{
		return frame;
	}
	public Device getActiveDevice()
	{
		return dm.getActiveDevice();
	}
	
	
	

	

	
	
	/**
	 * Initializes the UI.
	 * 
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 * 
	 */
	public void init()
	{
		// apply Swing look-and-feel first
		if(USE_NATIVE_LAF)
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
		
		frame = new JFrame();
		
		// start maximized where supported
		frame.setSize(new Dimension(INITIAL_WIDTH, INITIAL_HEIGHT));
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		// center on screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width - frame.getWidth()) / 2;
		int y = (screenSize.height - frame.getHeight()) / 2;
		frame.setLocation(new Point(x, y));
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setResizable(true);
		frame.setTitle(APPLICATION_TITLE);
		
		// add: main menu
		MenuManager menu = new MenuManager(this);
		menu.init();
		frame.setJMenuBar(menu.getMenu());
		
		// add: content area, which is a mix of InfoNode and Swing components
		frame.add(getRootWindow(), BorderLayout.CENTER);
		
		// add: status bar
		//  TODO: sketch out status bar
		JLabel sb = new JLabel(" Status: Disconnected");
		frame.add(sb, BorderLayout.SOUTH);		
	}
	
	
	
	
	
	
	
	
	
	
	
	public void update()
	{
		//updates everything
		
		dm.update();
//		mm.update();
//		sb.update();
		
	}
	
	
	
	
	
	
	/**
	 * Starts the interface.
	 *
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 *
	 */
	public void show()
	{
		frame.setVisible(true);
	}

	/**
	 * Validates the list of devices provided by the <code>HardwareManager</code>.
	 * 
	 * <p>The <code>HardwareManager</code> maintains a list of potential EAC
	 * devices; however, most of these devices are invalid for one reason or
	 * another.  It usually takes a few seconds to check each device.  While it
	 * would be trivial to run the validation process at the hardware level, we
	 * could not provide UI feedback without crossing the interface/hardware
	 * abstraction barrier.  The interface is already hardware-aware; thus,
	 * validation is performed here instead.
	 * 
	 * <p>NOTE: If no valid devices are found (unlikely), a runtime exception
	 * will be thrown.
	 * 
	 * @return   Validated <code>array[][]</code> of devices.
	 * 
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 * 
	 */
	private Device[][] validateDevices()
	{
		// grab the list of devices from the HardwareManager 
		Device[][] devicesToValidate = hm.getKnownDevices();
		LoadingFrame lf = new LoadingFrame(hm.getDeviceCount());
		
		// verify each device in each driver class
		Vector<Device[]> drivers = new Vector<Device[]>();      // outer accumulator (valid driver classes)
		for(int i = 0; i < devicesToValidate.length; i++)
		{
			Vector<Device> validatedDevices = new Vector<Device>();      // inner accumulator (valid devices)
			for(int j = 0; j < devicesToValidate[i].length; j++)
			{
				Device d = devicesToValidate[i][j];
				if(d.isValid())
				{
					validatedDevices.add(d);
					deviceList.put(d.getDeviceName(), d);
				}
				lf.increment();
			}
			
			// only add the driver class if it contains one or more valid devices
			if(validatedDevices.size() > 0)
			{
				Device[] validatedDriverClass = new Device[validatedDevices.size()];
				validatedDriverClass = validatedDevices.toArray(validatedDriverClass);
				drivers.add(validatedDriverClass);
			}
		}
		
		lf.close();

		Device[][] validated = new Device[drivers.size()][];
		validated = drivers.toArray(validated);
		
		return validated;
	}
	
	/**
	 * @deprecated Move to <code>JeacUtilities</code>
	 */
	public static RootWindow createMinimalRootWindow(AbstractViewMap v)
	{
		RootWindow rootWindow = DockingUtil.createRootWindow(v, false);

		// reduce clutter by disabling functionality
		rootWindow.getRootWindowProperties().getTabWindowProperties().getMaximizeButtonProperties().setVisible(false);
		rootWindow.getRootWindowProperties().getTabWindowProperties().getMinimizeButtonProperties().setVisible(false);
		rootWindow.getRootWindowProperties().getTabWindowProperties().getDockButtonProperties().setVisible(false);
		rootWindow.getRootWindowProperties().getTabWindowProperties().getUndockButtonProperties().setVisible(false);
		rootWindow.getRootWindowProperties().getTabWindowProperties().getCloseButtonProperties().setVisible(false);

		return rootWindow;
	}

}