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

package edu.indiana.cs.eac.driver;

import java.util.HashMap;

import edu.indiana.cs.eac.JEAC;

/**
 * Manages devices for use with jEAC (start here!).
 * 
 * <p>jEAC v2 introduces a distinction between <i>devices</i> and
 * <i>drivers</i>.  A device is the physical piece of hardware that jEAC
 * is designed to control, either directly or indirectly.  Drivers pertain
 * to specific classes of devices.  The <code>DeviceManager</code> is
 * responsible for managing driver classes and available devices.  In general,
 * jEAC will interact with the <code>DeviceManager</code>, except to issue
 * specific commands directly to the active device.
 * 
 * @author   Ryan R. Varick
 * @since    2.0.0
 *
 */
public class DeviceManager
{
	/** Singleton instance of the class. */
	private static DeviceManager INSTANCE;
	
	
	
	/* -------------------------[ Generic class methods ]------------------------- */
	
	/**
	 * Private contructor to prevent instantiation.
	 * 
	 * <p>This class follows the Singleton design pattern; it is not meant to
	 * be instantiated directly.  Instead, the instance should be retrieved via
	 * the <code>getInstance()</code> method.
	 * 
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 *
	 */
	private DeviceManager() { }
	
	/**
	 * Returns the single instance of the class.
	 * 
	 * <p>The idea is to prevent multiple instances of jEAC from running, in
	 * the same JVM anyway.  There shouldn't be a need for simultaneous instances.
	 * 
	 * @return   Class instance.
	 * 
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 * 
	 */
	public static DeviceManager getInstance()
	{
		if(INSTANCE == null)
		{ 
			INSTANCE = new DeviceManager();
		}
		return INSTANCE;
	}
	
	/**
	 * Prevents attempts to create multiple instances via cloning.
	 * 
	 * @throws   Don't copy that floppy!
	 * 
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 * 
	 */
	public Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}

	
	
	
	public int getDeviceCount()
	{
		return 0;
	}
	

	/**
	 * 
	 * 
	 * @return
	 * 
	 */
	public Device[][] getDeviceList()
	{
		Driver[] driverClasses = Driver.getDriverClasses();
		
		
		// iterate through each driver (pre Java 6 syntax) and stuff into a list
		for(Driver i : driverClasses)
		{
			i.testMe();
			
			Driver.getInstance(driverClasses[i]);
		}
//		
		NullDriver nullDriver = new NullDriver();
		Device[] virtualDrivers  = nullDriver.getDeviceList();
//		Device[] networkDrivers = EthernetDriver.getDeviceList();
//		Device[] localDrivers      = USBDriver.getDeviceList();
		Device[][] deviceList    = new Device[][]
			{
				virtualDrivers,
//				networkDrivers,
//				localDrivers
			};
		
		return deviceList;
	}
	

}
