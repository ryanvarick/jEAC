/**
 * JEAC.java - 3D real-time interaction with the extended analog computer.
 * 
 * @version 1.0.0
 * 
 * @author Drew Kipfer
 * @author Ryan R. Varick
 * 
 * Copyright (C) 2006.  All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 * 
 */

package edu.indiana.cs.eac;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;
import java.util.Timer;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import edu.indiana.cs.eac.driver.*;
import edu.indiana.cs.eac.gradient.*;
import edu.indiana.cs.eac.exceptions.*;

public class JEAC extends JFrame 
{
	private static final String JEAC_WINDOW_TITLE = "jEAC - Real-time 2D/3D EAC Interaction";
	private static final String VERSION = "1.1.0";
	private static final String BUILD   = "200";

	private static final int MIN_WINDOW_WIDTH  = 640;
	private static final int MIN_WINDOW_HEIGHT = 480;
	
	private static final int MIN_GRAPH_WIDTH       = 400;
	private static final int MIN_GRAPH_HEIGHT      = 400;
	private static final int GRAPH_SIZE_MULTIPLIER = 40;

	// driver controls
	private boolean driverConnected;
	private HAL driver;
	private Timer updateTimer;
	
	// driver list
	private String offlineDrivers[];
	private String ethernetDrivers[];
	private String usbDrivers[];
	private String driverList[][];
	
	private static int driver_flag = 0;
	public static final int NULL_DRIVERS = driver_flag++;
	public static final int EAC_DRIVERS  = driver_flag++;
	public static final int UEAC_DRIVERS = driver_flag++;
	
	// file I/O
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private File savestate;
	
	// timer control
	private static final int SECONDS = 1;
	private static final long TIMER_REFRESH_RATE = SECONDS * 1000;

	// shared UI components (see: reconfigureWindow)
	private JPanel contentPanel;
	
	private JMenu connectMenu;
	private ButtonGroup driverButtonGroup;
	private ButtonModel hack_driverDeselectionModel;
	private JMenuItem loadMenuItem, saveMenuItem, saveAsMenuItem;
	private JMenuItem disconnectMenuItem;
	private JMenuItem resetMenuItem;
	private JCheckBoxMenuItem gradient2DMenuItem, gradient3DMenuItem, ledMenuItem;
	private JCheckBoxMenuItem llaViewerMenuItem;

	private LLAInspectorFrame llaFrame;

	private Gradient3D gradient3DPanel;
	private Gradient2D gradient2DPanel;
	private NodeMap    nodemap;
	private StatusBar  statusbar;

	
	
	/**
	 * Constructor - initializes the overall UI.
	 * 
	 */
	public JEAC() 
	{		
		// populate the driver list
		offlineDrivers  = NullDriver.getDeviceList();
		ethernetDrivers = EthernetDriver.getDeviceList();
		usbDrivers      = USBDriver.getDeviceList();
		driverList      = new String[][]
			{
				offlineDrivers,
				ethernetDrivers,
				usbDrivers
			};
		
		driverConnected = false;
		
		// platform-native look-and-feel
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch(Exception e)
		{
			System.err.println("Unable to load native look and feel.");
		}
				
		// basic layout frame (GridBagLayout, ugh)
		Container container = getContentPane();
		container.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		
		// main menu
		setJMenuBar(getMainMenu());
		reconfigureMenu();

		// main content area (blank until a driver connects)
		constraints.gridx   = 0;
		constraints.gridy   = 0;
		constraints.anchor  = GridBagConstraints.FIRST_LINE_START;
		constraints.fill    = GridBagConstraints.BOTH;
		
		contentPanel = new JPanel();
		container.add(contentPanel, constraints);
		
		// status bar
		constraints.gridy  = 1;
		constraints.anchor = GridBagConstraints.LAST_LINE_START;
		constraints.fill   = GridBagConstraints.HORIZONTAL;		

		statusbar = new StatusBar();
		statusbar.setStatus(StatusBar.DISCONNECTED);
		container.add(statusbar, constraints);
				
		// other basic window configuration
		container.setBackground(getJMenuBar().getBackground());
		addWindowListener(new ExitListener());
		setIconImage(JEAC.getApplicationIcon());

		// size and location
		setSize(getWindowSize());
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width - getWidth()) / 2;
		int y = (screenSize.height - getHeight()) / 2;
		setLocation(x, y);

		setResizable(false);
		setTitle(JEAC_WINDOW_TITLE);
		setVisible(true);
	}

	/**
	 * Responsible for getting the good Mr.jEAC off running. :-)
	 *
	 * This method instantiates a copy of jEAC, then initializes the window.
	 * From there, interaction is event-driven.  The menu will instantiate a driver,
	 * which in turn will add/remove components to the UI.
	 * 
	 * @param args - Command line arguments are ignored. 
	 * 
	 */
	public static void main(String args[])
	{		
		// NOTE: enable or disable uEAC debugging here
		USBDriver.setDebug(false);
	
		LoadingFrame lf = new LoadingFrame();
		JEAC jEAC = new JEAC();
		lf.dispose();
	}
	
	
	
	
	
	
	
	/* ===============[ USER INTERFACE ]=============== */
	
	/**
	 * Populates the main menu.
	 * 
	 * @return JMenuBar - the main menu
	 * 
	 */
	private JMenuBar getMainMenu()
	{
		JMenuBar mainMenu = new JMenuBar();
		
		/* ---------------[ Top-level definitions ]--------------- */
		
		JMenu jeacMenu = new JMenu("jEAC");
		jeacMenu.setMnemonic('J');
		
		JMenu toolMenu = new JMenu("Tools");
		toolMenu.setMnemonic('T');
		
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');
		
		/* ---------------[ JEAC menu ]--------------- */
		
		// jeac > connect menu
		connectMenu = populateDriverMenu();
		connectMenu.setIcon(new BlankIcon(16, 16));

		// jeac > load
		loadMenuItem = new JMenuItem("Load configuration...", 'L');
		loadMenuItem.addActionListener(new FileIOListener(this));
		loadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
		loadMenuItem.setIcon(new ImageIcon(JEAC.getImage("icon_open.gif")));
		
		// jeac > save
		saveMenuItem = new JMenuItem("Save configuration", 'S');
		saveMenuItem.addActionListener(new FileIOListener(this));
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
		saveMenuItem.setIcon(new ImageIcon(JEAC.getImage("icon_save.gif")));
		
		// jeac > save as
		saveAsMenuItem = new JMenuItem("Save configuration as...", 'A');
		saveAsMenuItem.setIcon(new BlankIcon(16, 16));
		saveAsMenuItem.addActionListener(new FileIOListener(this));
		
		// jeac > disconnect
		disconnectMenuItem = new JMenuItem("Disconnect from EAC", 'D');
		disconnectMenuItem.addActionListener(new DisconnectListener());
		disconnectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_MASK));
		disconnectMenuItem.setIcon(new ImageIcon(JEAC.getImage("icon_disconnect.png")));
		
		// jeac > exit
		JMenuItem exitMenuItem = new JMenuItem("Exit", 'X');
		exitMenuItem.addActionListener(new ExitListener());
		exitMenuItem.setIcon(new BlankIcon(16, 16));
		
		/* ---------------[ Tools ]--------------- */

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
		gradient3DMenuItem.addActionListener(new NewGradientModeListener());
		gradient3DMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, KeyEvent.ALT_MASK));
		gradient3DMenuItem.setMnemonic('3');
		gradient3DMenuItem.setSelected(true);
		
		ledMenuItem = new JCheckBoxMenuItem("uEAC LEDs");
		ledMenuItem.addActionListener(new LEDListener());
		ledMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.ALT_MASK));
		ledMenuItem.setMnemonic('L');
		ledMenuItem.setEnabled(false);
		
	
		visualizationMenu.add(gradient2DMenuItem);
		visualizationMenu.add(gradient3DMenuItem);
		visualizationMenu.add(new JSeparator());
		visualizationMenu.add(ledMenuItem);
		
		// tools > lla inspector
		llaViewerMenuItem = new JCheckBoxMenuItem("LLA Inspector");
		llaViewerMenuItem.addActionListener(new LLAViewerListener());
		llaViewerMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_MASK));
		llaViewerMenuItem.setIcon(new ImageIcon(JEAC.getImage("icon_inspector.gif")));
		llaViewerMenuItem.setMnemonic('L');
		llaViewerMenuItem.setState(false);

		// tools > reset
		resetMenuItem = new JMenuItem("Reset all connections");
		resetMenuItem.addActionListener(new ResetListener());
		resetMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK));
		resetMenuItem.setIcon(new ImageIcon(JEAC.getImage("icon_reset.png")));
		resetMenuItem.setMnemonic('R');
		
		/* ---------------[ Help  ]--------------- */
		
		// help > about
		JMenuItem about = new JMenuItem("About", 'A');
		about.addActionListener(new AboutListener());
				
		/* ---------------[ Finalize menus ]--------------- */
		
		// build the jeac menu
		jeacMenu.add(connectMenu);
		jeacMenu.add(loadMenuItem);
		jeacMenu.add(new JSeparator());
		jeacMenu.add(saveMenuItem);
		jeacMenu.add(saveAsMenuItem);
		jeacMenu.add(new JSeparator());
		jeacMenu.add(disconnectMenuItem);
		jeacMenu.add(new JSeparator());
		jeacMenu.add(exitMenuItem);
		
		// build the tools menu
		toolMenu.add(visualizationMenu);
		toolMenu.add(new JSeparator());
		toolMenu.add(llaViewerMenuItem);
		toolMenu.add(resetMenuItem);
		
		// build the help menu
		helpMenu.add(about);
		
		// build and return the main menu
		mainMenu.add(jeacMenu);
		mainMenu.add(toolMenu);
		mainMenu.add(helpMenu);
		return mainMenu;
	}
	
	/**
	 * Updates the menu based on the state of the active driver.
	 * 
	 */
	private void reconfigureMenu()
	{
		disconnectMenuItem.setEnabled(driverConnected);
		loadMenuItem.setEnabled(driverConnected);
		saveMenuItem.setEnabled(driverConnected);
		saveAsMenuItem.setEnabled(driverConnected);
		gradient2DMenuItem.setEnabled(driverConnected);
		gradient3DMenuItem.setEnabled(driverConnected);
		llaViewerMenuItem.setEnabled(driverConnected);
		llaViewerMenuItem.setSelected(false);
		resetMenuItem.setEnabled(driverConnected);
		
		// LED control is uEAC-specific
		if(driverConnected && driver.getClass() == USBDriver.class)
		{
			ledMenuItem.setEnabled(true);
			ledMenuItem.setSelected(((USBDriver)driver).getLEDStatus());
		}
		else
		{
			ledMenuItem.setEnabled(false);
		}
	}
	
	/**
	 * Clear the driver selection menu.  This shouldn't be necessary,
	 *  but alas, Swing sometimes sucks, so here we are.
	 *  
	 */
	private void resetDriverMenu()
	{
		driverButtonGroup.setSelected(hack_driverDeselectionModel, true);
	}
	
	/**
	 * Updates the UI based on the state of the active driver.
	 * 
	 */
	private void reconfigureWindow() 
	{
		// ignore plots/nodemap if there is no driver
		if(driverConnected)
		{
			// status bar
			statusbar.setStatus(StatusBar.PLOTTING);

			// initialize grid bag constraints
			GridBagConstraints constraints = new GridBagConstraints();
			contentPanel.setLayout(new GridBagLayout());
			
			int gbc_row = 0;
			constraints.weightx = 1.0;
			constraints.weighty = 1.0;
			constraints.gridx   = 0;
			constraints.gridy   = gbc_row++;
			constraints.anchor  = GridBagConstraints.FIRST_LINE_START;
			constraints.fill    = GridBagConstraints.BOTH;
			
			// add a 3D view?
			if(gradient3DMenuItem.isSelected())
			{
				gradient3DPanel = new Gradient3D(driver);
				gradient3DPanel.setBorder(BorderFactory.createTitledBorder("3D Gradient Visualization"));

				// HACK: remove the JMathTools toolbar
				gradient3DPanel.remove(0);
				
				// graph size
				int preferredX = driver.getNumRows() * GRAPH_SIZE_MULTIPLIER;
				int preferredY = driver.getNumCols() * GRAPH_SIZE_MULTIPLIER;
				
				if(preferredX < MIN_GRAPH_WIDTH)  preferredX = MIN_GRAPH_WIDTH;
				if(preferredY < MIN_GRAPH_HEIGHT) preferredY = MIN_GRAPH_HEIGHT;
				gradient3DPanel.setPreferredSize(new Dimension(preferredX, preferredY));
				
				contentPanel.add(gradient3DPanel, constraints);
				constraints.gridx = 1;
			}
			
			// add a 2D view?
			if(gradient2DMenuItem.isSelected())
			{
				gradient2DPanel = new Gradient2D(driver);
				gradient2DPanel.setBorder(BorderFactory.createTitledBorder("2D Gradient Visualization"));
				contentPanel.add(gradient2DPanel, constraints);
			}
			
			// add the nodemap
			constraints.gridx     = 0;
			constraints.gridy     = gbc_row++;
			constraints.gridwidth = 2;
			constraints.anchor    = GridBagConstraints.LAST_LINE_START;
			constraints.fill      = GridBagConstraints.HORIZONTAL;
			nodemap = driver.getNodeMap();
			contentPanel.add(nodemap, constraints);
			
			// (re)start the timer
			startInterfaceUpdateThread();
		}

		// driver disconnected - remove gradients
		else
		{
			clearGradientPanels();
		}
		
		// status bar
		if(driverConnected)
		{
			statusbar.setStatus(StatusBar.CONNECTED);
			statusbar.setConnectedTo(driver.getDeviceName());
		}
		else statusbar.setStatus(StatusBar.DISCONNECTED);
		
		// resize and revalidate
		this.pack();
		this.setSize(this.getWindowSize());
		this.validate();
	}
	
	/**
	 * Removes gradient panels from the content panel.
	 *
	 */
	private void clearGradientPanels()
	{
		if(gradient2DPanel != null) { contentPanel.remove(gradient2DPanel); }
		if(gradient3DPanel != null) { contentPanel.remove(gradient3DPanel); }
	}
	
	/**
	 * Populates the driver menu.
	 * 
	 * @return JMenu - JMenu containing a list of valid drivers
	 * 
	 */
	private JMenu populateDriverMenu()
	{
		JMenu driverMenu = new JMenu("Connect to EAC");
		driverMenu.setMnemonic('C');
		
		driverButtonGroup = new ButtonGroup();
		int keyAcceleratorCounter = 0;
		for(int i = 0; i < driverList.length; i++)
		{
			// declare radio buttons for each driver in the current driver list
			JRadioButtonMenuItem[] driverMenuItem = new JRadioButtonMenuItem[driverList[i].length];
			
			for(int j = 0; j < driverList[i].length; j++)
			{
				// create a radio button for the driver and add it to the current button group
				driverMenuItem[j] = new JRadioButtonMenuItem(driverList[i][j]);
				driverMenuItem[j].setAccelerator(KeyStroke.getKeyStroke(
						KeyEvent.VK_0 + keyAcceleratorCounter++, 
						KeyEvent.CTRL_MASK));
				driverButtonGroup.add(driverMenuItem[j]);
				
				// now add the entry to the menu, along with its listener
				driverMenu.add(driverMenuItem[j]);
				driverMenuItem[j].addActionListener(new NewDriverListener());
			}
			
			// append separators smartly (no trailing separator, no separator if the uEAC list is empty)
			int separatorsToOmit = 1;
			if(usbDrivers == null || usbDrivers.length == 0) separatorsToOmit++;
			if((i + separatorsToOmit) < driverList.length) { driverMenu.add(new JSeparator()); }
		}
		
		//
		// HACK: Add a hidden button so we can "turn off" the button on driver disconnect.
		//       Thank you for sucking, Swing! (see: http://www.javaworld.com/javaworld/javatips/jw-javatip142.html)
		//
		JRadioButtonMenuItem hackItem = new JRadioButtonMenuItem();
		driverButtonGroup.add(hackItem);
		hack_driverDeselectionModel = hackItem.getModel();
		
		return driverMenu;
	}
	

	
	
	
	
	
	/* ===============[ UTILITY METHODS ]=============== */
	
	/**
	 * Stops the active driver.  First the UI update thread is cancelled,
	 *  then the driver is disconnected.  Finally, the interface is updated.
	 *
	 */
	private void disconnectDriver()
	{
		stopInterfaceUpdateThread();

		driver.disconnect();
		driverConnected = false;

		driver    = null;
		savestate = null;

		clearGradientPanels();
		
		if(nodemap != null) 
		{
			nodemap.clearCollection();
			contentPanel.remove(nodemap);
		}
		if(llaFrame != null) 
		{
			llaFrame.dispose();
			llaFrame = null;
		}

		reconfigureMenu();
		reconfigureWindow();
	}
	
	/**
	 * Starts (or restarts) the thread that updates the UI.  Useful because
	 *  we only want to update components that are currently visible.  For example,
	 *  if the LLA Inspector isn't open, we don't want to waste communication 
	 *  bandwidth probing LLA values.
	 *
	 */
	private void startInterfaceUpdateThread()
	{
		// stop any existing threads
		if(updateTimer != null) { stopInterfaceUpdateThread(); }
		
		// (re)start the new thread
		UpdateTask task = new UpdateTask(driver, gradient2DPanel, gradient3DPanel, llaFrame);
		task.run();		// run the task immediately to update the gradient
		updateTimer = new Timer();
		updateTimer.schedule(task, TIMER_REFRESH_RATE, TIMER_REFRESH_RATE);
	}	
	
	/**
	 * Stops the thread that updates the UI. See <code>startInterfaceUpdateThread</start>
	 *  for the motivation behind these methods.
	 * 
	 */
	private void stopInterfaceUpdateThread()
	{
		if(updateTimer != null) { updateTimer.cancel(); }
	}

	/**
	 * Computes the default window size.
	 * 
	 * @return Dimension
	 * 
	 */
	private Dimension getWindowSize()
	{
		Dimension newSize;
		
		// when a driver is connected, set the window size to the sum of the components' 
		//  preferred sizes; otherwise, use a fixed size (this could be smarter)
		if(driverConnected)
		{
			newSize = getPreferredSize();
		}
		else
		{
			newSize = new Dimension(JEAC.MIN_WINDOW_WIDTH, JEAC.MIN_WINDOW_HEIGHT);
		}
		return newSize;
	}

	/**
	 * Performs necessary shutdown operations.
	 *  
	 */
	private void shutdown()
	{
		if(this.driverConnected == true)
		{
			driver.disconnect();
		}
		System.exit(0);
	}
	
	
	
	/* --------------------[ Static methods ]-------------------- */

	/**
	 * Static method to retrieve jEAC's default application icon.
	 * 
	 */
	public static Image getApplicationIcon()
	{
		return getImage("appicon.png");
	}
	
	/**
	 * Static method to load an image resource.
	 * 
	 * @param String image - name (plus extension) of the image to load
	 * 
	 * @return Image
	 *  
	 */
	public static Image getImage(String imageName)
	{
		java.net.URL imageURL = JEAC.class.getResource("images/" + imageName);
		
		ImageIcon icon = null;
		if (imageURL != null) {
		    icon = new ImageIcon(imageURL);
		}
		else
		{
			System.err.println("Cannot load image: " + imageName);
		}
		return icon.getImage();		
	}
	
	/**
	 * Static method to retrieve the jEAC version number.
	 * 
	 */
	public static String getVersion()
	{
		return VERSION;
	}
	
	/**
	 * Static method to retrieve the jEAC build number.
	 * 
	 */
	public static String getBuild()
	{
		return BUILD;
	}
	
	
	
	
	
	
	
	/* ====================[ LISTENERS ]===================== */
	
	/**
	 * Listens for the about menu item.
	 * 
	 */
	private class AboutListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			AboutFrame about = new AboutFrame();
		}
	}
	
	/**
	 * Listens for the user to request a driver disconnect.
	 * 
	 */
	private class DisconnectListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			disconnectDriver();
			
			// HACK: this has to be here -- we only want to reset the menu when
			//       a disconnect is specified explicitly.  To move this into 
			//		 reconfigureMenu() would reset the menu on driver change too.
			resetDriverMenu();
		}
	}

	/**
	 * Listen for exit events and insures clean shutdown.
	 *
	 */
	private class ExitListener extends WindowAdapter implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			shutdown();
		}
		public void windowClosing(WindowEvent e)
		{
			shutdown();
		}
	}
	
	/**
	 * Listens for configuration load/save events.
	 *
	 */
	private class FileIOListener implements ActionListener
	{
		private JEAC jeac;

		/**
		 * Constructor - Instantiates a new listener.
		 * 
		 * @param JEAC - reference to jEAC
		 * 
		 */
		public FileIOListener(JEAC jeac)
		{
			this.jeac = jeac;
		}
		
		/** 
		 * Responds to user interaction with the jEAC menu.
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			// HACK: these are hardcoded to the name of the menu items
			if (e.getActionCommand() == "Save configuration as...") {
				saveConfigurationFile();
			} else if (e.getActionCommand() == "Load configuration...") {
				openConfigurationFile();
			} else if (e.getActionCommand() == "Save configuration") {
				resaveConfigurationFile();
			}
		}
		
		/**
		 * Loads an existing configuration.
		 * 
		 */
		public void openConfigurationFile()
		{	
			// pop up the file dialog
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
			fileChooser.changeToParentDirectory();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setFileFilter(new FileFilter());
			int result = fileChooser.showOpenDialog(jeac);
			
			// handle cancel; otherwise, load up the file
			if (result == JFileChooser.CANCEL_OPTION) return;
			File fileName = fileChooser.getSelectedFile();
			
			// try to load the file
			try 
			{ 
				loadConfiguration(new FileInputStream(fileName));
			}
			catch(IOException e)
			{
//				System.err.println("Exception: " + e.getMessage());
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		/**
		 * Load a saved configuration (XML).
		 * 
		 * @param InputStream in - input stream specifying the XML data
		 * 
		 * @throws IOException - if something breaks, an exception will be thrown
		 * 
		 */
		private void loadConfiguration(InputStream in) throws IOException
		{
			// try to load the XML file
			Document doc = null;
			try
			{
				doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
			}
			catch(Exception e)
			{
				throw new IOException("The configuration file could not be opened for processing.");
			}

			// now try to read the XML file
			NodeList nodeList = doc.getChildNodes();
			if(nodeList.getLength() != 1) throw new IOException("The configuration file is not valid.");
			Element root = (Element)nodeList.item(0);

			// check the version
			if(!"1.0".equals(root.getAttribute("version"))) 
			{
				throw new IOException("The configuration file is not supported by this version of jEAC."); 
			}

			// check hardware flag
			String eacType = root.getAttribute("type");
			if(!driver.getClass().getName().equals(eacType))
			{
				throw new IOException("This configuration file is not valid for the current EAC.");
			}
			
			// everything is good so far, reset the driver
			try
			{
				statusbar.setStatus(StatusBar.RESETTING);
				driver.reload();
				statusbar.setStatus(StatusBar.LOADING);
			}
			catch(Exception e)
			{
				throw new IOException("The driver did not reset properly.");
			}
			
			NodeList rowList = root.getChildNodes();
			for(int i = 0; i < rowList.getLength(); i++)
			{
				// check that things are in sync
				if(!rowList.item(i).getClass().getName().equals(root.getClass().getName())) { continue; }
				
				Element row = (Element)rowList.item(i);
				if(!"row".equals(row.getTagName())) { continue; }
				
				int rowIndex = Integer.parseInt(row.getAttribute("index"));
				NodeList pinList = row.getChildNodes();
				for(int j = 0; j < pinList.getLength(); j++)
				{
					// check that things are in sync
					if(!pinList.item(j).getClass().getName().equals((root.getClass().getName()))) {	continue; }

					// isolate the pin and its location
					Element pin  = (Element)pinList.item(j);
					int pinIndex = Integer.parseInt(pin.getAttribute("index"));
					
					// isolate the pin's type and value
					Element rawType  = (Element)(pin.getChildNodes().item(0));
					String type      = rawType.getNodeName();
					CDATASection rawValue = (CDATASection)(rawType.getChildNodes().item(0));
					double value          = Double.parseDouble(rawValue.getNodeValue());
					
					// now create a proper JEACNode and tell the driver to update
					JEACNode node = driver.getJEACNodes()[rowIndex * driver.getNumCols() + pinIndex];
					driver.changeNode(type, value, node);
				}
			}
			
			// update the UI
//			gradient3DPanel.setLabel("TESTING");
			driver.getNodeMap().reload();
			statusbar.setStatus(StatusBar.CONNECTED);
		}
		
		/**
		 * Write a configuration file (XML).
		 * 
		 * @param OutputStream out - stream to write XML data to
		 * 
		 * @throws IOException
		 * 
		 */
		private void saveConfig(OutputStream out) throws IOException
		{
			try
			{
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.newDocument();
				
				Element root = doc.createElement("eac");
				root.setAttribute("version", "1.0");
				root.setAttribute("type", driver.getClass().getName());
				root.setAttribute("name", driver.getDeviceName());
				doc.appendChild(root);

				JEACNode[] nodes = driver.getJEACNodes();
				int rows = driver.getNumRows();
				int cols = driver.getNumCols();
				for(int i = 0; i < rows; i++)
				{
					boolean empty = true;
					Element row = doc.createElement("row");
					row.setAttribute("index", "" + i);
					for(int j = 0; j < cols; j++)
					{
						JEACNode me = nodes[i * rows + j];
						if(!me.getType().equals(JEACNode.OFF))
						{
							empty = false;
							Element pin = doc.createElement("pin");
							pin.setAttribute("index", "" + j);
							Element type = doc.createElement(me.getType());
							type.appendChild(doc.createCDATASection("" + me.getValue()));
							pin.appendChild(type);
							row.appendChild(pin);
						}
					}
					if(!empty)
					{
						root.appendChild(row);
					}
				}
				
				TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc), new StreamResult(out));
			}
			catch(Exception e)
			{ 
				throw new IOException("Unable to save the configuration file.");
			} 
			
		}
		/**
		 * Saves the state of the active EAC (Save as..., or first-time Save).
		 *
		 */		
		public void saveConfigurationFile()
		{
			// pop up the save dialog
			JFileChooser fileChooser = new JFileChooser();
			FileFilter filter = new FileFilter();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setFileFilter(filter);
			int result = fileChooser.showSaveDialog(jeac);
			
			// handle cancel; otherwise, start saving
			if (result == JFileChooser.CANCEL_OPTION) return;
			File fileName = fileChooser.getSelectedFile();
			
			// append the extension, if necessary (assuming a valid filename)
			if(!filter.accept(fileName)) { fileName = new File(fileName.toString() + filter.getExtension()); }
			
			try
			{
				saveConfig(new FileOutputStream(fileName));
				savestate = fileName;
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(null, "Could not save the configuration.", "Error", JOptionPane.ERROR_MESSAGE);				
				System.err.println("Error saving configuration:");
				e.printStackTrace();
			}
		}
		
		/**
		 * Saves the state of the active EAC to the current save file (subsequent Save).
		 *
		 */
		public void resaveConfigurationFile() {
			if (savestate != null) {
				try {
					saveConfig(new FileOutputStream(savestate));
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Could not save the configuration.", "Error", JOptionPane.ERROR_MESSAGE);				
					System.err.println("Error saving configuration:");
					e.printStackTrace();
				}
			} else saveConfigurationFile();				
		}
	}
	
	/**
	 * Listen for driver mode changes from the menu and attempt to connect to the new driver.
	 *   
	 */
	private class NewDriverListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			statusbar.setStatus(StatusBar.CONNECTING);
			
			// stop existing driver connections
			if(driverConnected) disconnectDriver();
			
			// figure out what was selected
			String new_driver = e.getActionCommand();
			
			for(int i = 0; i < driverList[NULL_DRIVERS].length; i++)
			{
				if(new_driver == driverList[NULL_DRIVERS][i]) { driver = new NullDriver(); }
			}
			for(int i = 0; i < driverList[EAC_DRIVERS].length; i++)
			{
				if(new_driver == driverList[EAC_DRIVERS][i]) { driver = new EthernetDriver(new_driver); }
			}
			for(int i = 0; i < driverList[UEAC_DRIVERS].length; i++)
			{
				if(new_driver == driverList[UEAC_DRIVERS][i])
				{
					driver = new USBDriver(new_driver);
				}
			}
						
			// attempt to connect to the driver
			try
			{
				driver.connect();
				driverConnected = true;
				
				statusbar.setStatus(StatusBar.RESETTING);
				driver.reset();				// reset the machine on connect (1476441)
			} 
			catch(ConnectionException ea)
			{
				JOptionPane.showMessageDialog(
						null, "Could not connect to the EAC.", "Connection Error", JOptionPane.ERROR_MESSAGE);
				driverConnected = false;
			}

			// reconfigure the UI
			reconfigureMenu();
			reconfigureWindow();
		}
	}
	
	/**
	 * Listen for new the user to request a new gradient mode.
	 *
	 */
	private class NewGradientModeListener implements ActionListener 
	{
		public void actionPerformed(ActionEvent e)
		{
			clearGradientPanels();
			reconfigureWindow();
		}
	}

	/**
	 * Listen for changes to the LED checkbox.
	 * 
	 */
	private class LEDListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			// since this is a uEAC-only command, we can safely cast here
			((USBDriver)driver).toggleLEDs();
		}
	}
	
	/**
	 * Listen for changes to the LLAViewer checkbox.
	 * 
	 */
	private class LLAViewerListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			// HACK: check for callbacks from the InspectorFrame
			if(e.getID() == LLAInspectorFrame.INSPECTOR_CLOSED)
			{
				// uncheck the inspector menu item
				reconfigureMenu();
				llaFrame = null;
				startInterfaceUpdateThread();
			}
			
			// check for menu checked (open the inspector)
			else if(llaViewerMenuItem.isSelected())
			{
				llaFrame = new LLAInspectorFrame(driver, this);
				startInterfaceUpdateThread();
			}
			
			// check for menu unchecked (close the inspector)
			else
			{
				llaFrame.dispose();
				llaFrame = null;
				startInterfaceUpdateThread();
			}
		}
	}
	
	/**
	 * Listens for the reset command.
	 * 
	 */
	private class ResetListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to reset all connections?", "Confirm...", JOptionPane.YES_NO_OPTION);
			if(choice == JOptionPane.YES_OPTION)
			{
				statusbar.setStatus(StatusBar.RESETTING);
				
				// stop the update thread, then stop the driver
				stopInterfaceUpdateThread();
				try { driver.reset(); }
				catch(Exception err) { }
				
				// reset the node map
				nodemap.reset();
		
				// finally, restart the update thread
				statusbar.setStatus(StatusBar.CONNECTED);
				JOptionPane.showMessageDialog(null, "Connections reset.");
				startInterfaceUpdateThread();
			}
			else
			{
				/* Do nothing. */
			}
		}
	}
}