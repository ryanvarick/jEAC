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
	
	// TODO: Implement as a singleton class using a hash map to 
	// try to define a static (final) getInstance() method to power our
	// Driver factories... try anyway.
	// TODO: Singleton in general -- don't forget synchronization!

	/**
	 * 
	 *
	 */
	public DeviceManager()
	{
		
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
