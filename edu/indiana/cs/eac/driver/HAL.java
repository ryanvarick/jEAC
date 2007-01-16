/**
 * HAL.java - Extended analog computer hardware abstraction layer.
 * 
 * @version 1.0.0
 * 
 * Copyright (C) 2006.  All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 * 
 */

package edu.indiana.cs.eac.driver;

import java.io.*;

import edu.indiana.cs.eac.*;
import edu.indiana.cs.eac.exceptions.*;

public interface HAL
{
	//
	// NOTE: The following method should be declared for all driver classes.
	//       You can't declare static methods in an interface, apparently. :-/
	//

	/**
     * Return the list of valid devices for this driver class.
     * 
     * @return String[] - list of devices
     */
//	public static String[] getDeviceList();
	
    

    
    
    
	
	/* ===============[ LAYER 1: CONNECT/DISCONNECT ]=============== */
	
	//
	// This layer defines the "edges" of the driver: connecting, 
	//  disconnecting, resetting, and reloading.  These are features we
	//  think are common to all EAC devices.
	//
	
	/**
	 * Tells the driver to connect to the current EAC.
	 * 
	 * @throws ConnectionException
	 * 
	 */
	public void connect() throws ConnectionException;
	
	/**
	 * Tells the driver to disconnect from the current EAC.
	 *
	 */
	public void disconnect();
	
	/**
	 * Tells the driver to re-instantiate the EAC based on the current
	 *  nodemap configuration (called after deserialization).
	 * 
	 * @throws ConnectionException
	 * 
	 */
	public void reload() throws ConnectionException;
	
	/**
	 * Tells the driver to remove all connections from the device.
	 * 
	 * NOTE: This is not the same as <code>reload()</code>, which is used to
	 *       load a previous configuration.  <code>reset()</code> is called
	 *       to restore the EAC to an unconfigured state.
	 *
	 */
	public void reset();
	
	
	
	
		
	
	
	/* ===============[ LAYER 2: HARDWARE CONTROL ]=============== */
	
	//
	// These methods define required methods for hardware control of the EAC.
	//  You'll notice this section is rather empty.  The HAL defines the 
	//  a basic connect/disconnect procedure (layer 1, above), and a number
	//  of UI methods.
	//
	// We tried to decouple the UI requirements from the details of hardware
	//  interaction so as to provide driver developers as much freedom as
	//  possible when working with new machines.  Take a look at the differences
	//  between EthernetDriver and USBDriver for example strategies.
	//

	/**
	 * Reads the voltage gradient from the EAC.
	 * 
	 * @returns double[][] - array of doubles representing the values of each pin
	 * 
	 */
	public double[][] getVoltageGradient() throws IOException;
		
	
	
	
	
	
	
	/* ===============[ LAYER 3: UI/REPORTING ]================= */
	
	//
	// These methods provide functionality useful for Java EAC frontends.
	//  If you want, you can roll EAC hardware control into these methods
	//  and massage the data into the specified format, or you can define
	//  your own hardware controls (layer 2, above) that feed these methods.
	//
	
	
	
	/* ---------------[ Basic Information ]--------------- */
	
	/**
	 * Returns a printable version of the EAC name (generally the OS-specific port name).
	 * 
	 * @return String - device name
	 * 
	 */
    public String getDeviceName();
	
	/**
	 * Reports the total number of connection rows on the machine.
	 * 
	 * @return int - number of rows
	 * 
	 */
	public int getNumRows();
	
	/**
	 * Reports the number of rows that report values.
	 * 
	 * @return int - number of rows
	 * 
	 */
	public int getNumReportingRows();
	
	/**
	 * Reports the total number of connection columns on the machine.
	 * 
	 * @return int - number of columns
	 * 
	 */
	public int getNumCols();
	
	/**
	 * Reports the number of columns that report values.
	 * 
	 * @return int - number of columns
	 * 
	 */
	public int getNumReportingCols();
	
	/**
	 * Reports the maximum current value accepted by the driver. <br><br>
	 * 	
     * NOTE: This must be an int; it is ultimately used to instantiate
	 *       JSliders, which do not like doubles (same for <code>getMinCurrent</code>).
	 * 
	 * @return int - maximum current
	 * 
	 */
	public int getMaxCurrent();
    
    /**
     * Reports the minimum current value accepted by the driver.
     * 
     * @return int - minimum current
     * 
     */
	public int getMinCurrent();
	
	/**
	 * Reports the current unit in use (generally mA or uA)
	 * 
	 * @return String - unit
	 * 
	 */
	public String getCurrentUnit();
	
	/**
	 * Reports the number of LLA functions recognized by the machine.
	 * 
	 * @return int - number of LLA functions
	 * 
	 */
	public int getNumLLAFunctions();
	
    /**
     * Reports the number of LLAs in use.
     * 
     * @return int - number of active LLAs
     * 
     */
    public int getNumLLAs();

    /**
     * Reports the input values for all active LLAs. <br><br>
     *
     * NOTE: This is somewhat UI-specific, what with returning a String an all.
     *       LLAs report either as int's or doubles, depending on the hardware,
     *       but the UI likes Strings, so that's what we use. <br><br>
     *       
     *       Each driver probably provides its own internal LLA reporting functions
     *       if you're interested in the raw values.
     * 
     * @return String[] - list of values
     *
     */
	public String[] getAllLLAInputValues();
	
	/**
	 * Reports the output values for all active LLAs.
	 * 
	 * @return String[] - list of values
	 * 
	 */
	public String[] getAllLLAOutputValues();
	
	/**
	 * Reports the number of connection types supported by the machine.
	 * 
	 * @return int - number of types
	 * 
	 */
	public int getNumConnectionTypes();

	
	
	/* ---------------[ jEAC-specific Interaction ]--------------- */
	
	//
	// These methods are specific to jEAC's particular approach to
	//  dealing with EACs.  They likely of no use outside of this application.
	//
	// TODO: (DEF) Move these methods into a separate interface.
	//

	/**
     * Returns the driver's array of JEAC nodes.
     * 
     * @return JEACNode[] - array of nodes
     * 
     */
	public JEACNode[] getJEACNodes();
	
	/**
	 * Returns the driver's NodeMap.
	 * 
	 * @return NodeMap
	 * 
	 */
    public NodeMap getNodeMap();

    /**
     * Changes the configuration of a connection based on user-interaction.<br><br>
     * 
     * <code>changeNode</code> acts as the gate-keeper between the user interface
     * and the driver.  Events from the interface are collected by <code>changeNode</code>
     * and passed to the driver for execution.  Upon successful completion of the driver
     * command, <code>changeNode</code> will return true.  It is expected that client-
     * side bookkeeping (e.g. the nodemap) will be updated only after <code>changeNode</code>
     * says things are okay.
     * 
     * @param String type - node type (one of the constants in JEACNode)
     * @param double value - node value
     * @param JEACNode node - node that is being changed
     * 
     * @return boolean - node successfully changed
     * 
     */
    public boolean changeNode(String type, double value, JEACNode node);
	
}
