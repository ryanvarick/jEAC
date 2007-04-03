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

package edu.indiana.cs.eac.hardware;

import java.util.*;

import edu.indiana.cs.eac.*;

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
public class HardwareManager
{
	/**
	 * List of active driver classes.
	 * 
	 * <p>This is how driver classes (and, by extension, hardware devices) are
	 * added to jEAC.  Drivers are implemented as singleton <code>Device</code>
	 * factories.  Thus to tell jEAC to look for a new class of device, simply
	 * add its driver class to the array.
	 * 
	 */
	private static Driver[] drivers = new Driver[]
	{
		Driver.getInstance(NullDriver.class),
		Driver.getInstance(RandomDriver.class),
//		Driver.getInstance(NullDriver3.class),
//		Driver.getInstance(NetEACDriver.class),
		Driver.getInstance(USBuEACDriver.class)
	};
	
	
	
	



	private HardwareManager()
	{
		
	}
	public static final HardwareManager getInstance()
	{
		return new HardwareManager();
	}
	
	
	public int getDeviceCount()
	{
		Device[][] devices = getKnownDevices();
		
		int cnt = 0;
		for(int i = 0; i < devices.length; i++)
		{
			cnt += devices[i].length;
		}
		
		System.out.println("Returning device count: " + cnt);
		return cnt;
	}
	

	/**
	 * 
	 * 
	 * @return
	 * 
	 */
	public Device[][] getKnownDevices()
	{
		Device[][] devices = new Device[drivers.length][];
		
		for(int i = 0; i < drivers.length; i++)
		{
			devices[i] = drivers[i].getDevices();
		}
		
		return devices;
	}	

//	public Vector<Vector<Device>> getKnownDevices2()
//	{
//		Device[][] devices = new Device[drivers.length][];
//		
//		for(int i = 0; i < drivers.length; i++)
//		{
//			devices[i] = drivers[i].getDevices();
//		}
//		
//		return devices;
//	}	

}
