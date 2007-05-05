package edu.indiana.cs.eac.hardware;

import java.util.HashMap;

public abstract class Driver
{
	protected Driver()
	{
//		System.out.println("Trying to create a " + classname.getName());
	}

	
	private static HashMap map = new HashMap();
	
	public static final synchronized Driver getInstance(Class classname)
	{
		System.out.println("In Driver, trying to return " + classname.getName());
		Driver singleton = (Driver)map.get(classname);

		if(singleton != null)
		{
			return singleton;
		}
		
		try
		{
			singleton = (Driver)Class.forName(classname.getName()).newInstance();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		map.put(classname, singleton);
		return singleton;
	}
	
	
//	private static final String[] driverClasses = new String[] 
//		{
//			"NullDriver", 
//			"NetEACDriver", 
//			"USBuEACDriver"
//		};
//	
	
	
	public abstract Device[] getDevices();
	
	
	// Make this into Singletons...
	
	// 1. revert this to an interface
	// 2. move this code into DeviceManager
	//    (we cannot force subtypes to be Singletons)
//	public static Driver[] getDrivers()
//	{
//		return new Driver[] { new NullDriver(), new NullDriver() };
//	}
	
//	public abstract void testMe();
	
	/**
	 * 
	 * @return
	 * 
	 */
//	public Driver getInstance();

	
	
	/**
	 * Returns a <code>Device</code> from an identifier.
	 * 
	 * <p>This is a factory method that returns an instantiation of the driver.
	 * It is the preferred way of getting an actual, usable <code>Device</code>
	 * object out of a driver.  Generally, the identifier refers to the system
	 * identifier (ie COM5) rather than the print name (ie "uEAC on COM5").
	 * 
	 * @param identifier   Internal identifier (generally the system identifier).
	 * @return             An instantiated <code>Device</code>.
	 * 
	 * @author             Ryan R. Varick
	 * @since              2.0.0
	 * 
	 */
//	public abstract Device returnDeviceFromIdentifier(String identifier);
//
//	public abstract Device[] getDeviceList();
//	
}
