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
import javax.swing.tree.*;

import net.infonode.docking.*;
import net.infonode.docking.theme.*;
import net.infonode.docking.util.*;
import net.infonode.util.*;

import edu.indiana.cs.eac.*;
import edu.indiana.cs.eac.hardware.*;
import edu.indiana.cs.eac.ui.listeners.*;




/**
 * Multi-document interface (MDI) manager.
 * 
 * <p>This class implements an MDI manager.
 * based on http://www.javaworld.com/javaworld/jw-05-2001/jw-0525-mdi.html  
 * 
 * @author   Ryan R. Varick
 * @since    2.0.0
 *
 */
public class InterfaceManager implements Manager
{
	/* defaults (do not alter directly; use the API instead) */
	private boolean useNativeLAF = true;
	private DockingWindowsTheme theme = new ShapedGradientDockingTheme();
	
	// TODO: externalize strings
	private static final String APPLICATION_TITLE = "jEAC - Cross-platform EAC development environment";
	private static final String DESKTOP_TITLE     = "Workspace";
	private static final String DEVICE_MGR_TITLE  = "Device Manager";
	private static final String LLA_EDITOR_TITLE  = "LLA Editor";
	private static final String EVOLVER_TITLE          = "Evolver";

	private static final int INITIAL_WIDTH  = 800;
	private static final int INITIAL_HEIGHT = 600;

	private static final Color DESKTOP_COLOR = Color.GRAY.brighter();
		
	/* UI components (do not alter) */
	private JFrame frame;
	private MDIDesktopPane desktop;
	
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
//	 * <p>The constructor prepares the user interface and initializes other
//	 * managers (e.g. <code>MenuManager</code>) as needed.  Note that
//	 * it <b>does not</b> finalize or show UI.  These tasks are handled by
//	 * <code>init()</code> and <code>show()</code>, respectively.   
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
	 * Builds the desktop.
	 * 
	 * <p>jEAC uses two kinds of UI containers:  dockable windows and free-
	 * floating windows.  Free-floating windows are contained within the desktop 
	 * area, which behaves like a standard multi-document interface (MDI).
	 * 
	 * @return   MDI desktop component.
	 * 
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 * 
	 */
	private View getDesktopView()
	{
		// create (and cache) the desktop; we will need it later
		desktop = new MDIDesktopPane();
		desktop.setBackground(DESKTOP_COLOR);
		
		// create a scroll pane to contain the desktop
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().add(desktop);
		
		// create an InfoNode view to contain the new, scrollable desktop
		View scrollableDesktop = new View(DESKTOP_TITLE, null, scrollPane);
		
		// make the desktop more-or-less immutable
		scrollableDesktop.getWindowProperties().setCloseEnabled(false);
		scrollableDesktop.getWindowProperties().setUndockEnabled(false);
		scrollableDesktop.getWindowProperties().setMaximizeEnabled(false);
		scrollableDesktop.getWindowProperties().setMinimizeEnabled(false);
		scrollableDesktop.getWindowProperties().setDragEnabled(false);
		
		return scrollableDesktop;
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

		View desktopView = getDesktopView();
//		desktopView.getCustomTitleBarComponents();
		viewMap.addView(view++, desktopView);

		View evolverView = new View("Evolver", null, new MDIDesktopPane());
		viewMap.addView(view++, evolverView);

		View editorView = new View("LLA Editor", null, new MDIDesktopPane());
		viewMap.addView(view++, editorView);

		dm = new DevicePanelManager(this);
		View deviceManagerView = new View("Device Manager", null, dm.getDevicePanel());
		viewMap.addView(view++, deviceManagerView);
		
		// allocate main window (similiar to JFrame)
		//  NOTE: RootWindow *must* be allocated before other InfoNode windows
		RootWindow rootWindow = DockingUtil.createRootWindow(viewMap, false);

		// reduce clutter by disabling functionality
		rootWindow.getRootWindowProperties().getTabWindowProperties().getMaximizeButtonProperties().setVisible(false);
		rootWindow.getRootWindowProperties().getTabWindowProperties().getMinimizeButtonProperties().setVisible(false);
		rootWindow.getRootWindowProperties().getTabWindowProperties().getDockButtonProperties().setVisible(false);
		rootWindow.getRootWindowProperties().getTabWindowProperties().getUndockButtonProperties().setVisible(false);
		rootWindow.getRootWindowProperties().getTabWindowProperties().getCloseButtonProperties().setVisible(false);

		// apply theme (specified in setLookAndFeel())
		rootWindow.getRootWindowProperties().addSuperObject(theme.getRootWindowProperties());		

		// specify InfoNode tab layout (similar to JTabbedPane)
		rootWindow.getWindowBar(Direction.RIGHT).setEnabled(true);

		// specify view layout (similar to JSplitPane)
		SplitWindow toolWindow = new SplitWindow(false, 0.5f, deviceManagerView, evolverView);
		SplitWindow mainWindow = new SplitWindow(true,  0.7f, desktopView, toolWindow);

		rootWindow.setWindow(mainWindow);

		return rootWindow;
	}
	
	
	



	public MDIDesktopPane getDesktop()
	{
		return desktop;
	}
	public JFrame getWindow()
	{
		return frame;
	}
	
	
	

	

	
	
	/**
	 * Initializes the UI.
	 * 
	 * <p>This method populates the UI components that were only partially
	 * initialized by the constructor.  Generally, this means that the UI
	 * components handled here take a noticable amount of time, or need to be
	 * handled outside the constructor for various reasons.
	 *
	 */
	public void init()
	{
		// always set first
		setLookAndFeel();
		
		frame = new JFrame();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setResizable(true);
		frame.setTitle(APPLICATION_TITLE);
		
		// start maximized where supported
		frame.setSize(new Dimension(INITIAL_WIDTH, INITIAL_HEIGHT));
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		// center on screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width - frame.getWidth()) / 2;
		int y = (screenSize.height - frame.getHeight()) / 2;
		frame.setLocation(new Point(x, y));
		
		// add main menu
		MenuManager menu = new MenuManager();
		menu.init();
		frame.setJMenuBar(menu.getMenu());
		
		// add root window, containing a combination of InfoNode and Swing components
		frame.add(getRootWindow(), BorderLayout.CENTER);
//		populateDeviceTree(deviceTree);
		
		// add status bar
		JLabel sb = new JLabel(" Status: Disconnected");
		frame.add(sb, BorderLayout.SOUTH);		
	}
	
	/**
	 * Sets the look-and-feel.
	 * 
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 * 
	 */
	private void setLookAndFeel()
	{
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
	}

	/**
	 * Starts the interface.
	 * 
	 * needs to be separate from the constructor
	 *
	 */
	public void show()
	{
		frame.setVisible(true);
//		frame.setEnabled(false);
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