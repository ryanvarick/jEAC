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

package edu.indiana.cs.eac;

import java.util.*;

import edu.indiana.cs.eac.ui.*;
import edu.indiana.cs.eac.driver.*;

/**
 * Base class for jEAC.
 * 
 * <p>jEAC has been substantially redesigned for version 2.  Much of the 
 * functionality that used to be in this class has been moved to other
 * subordinate classes.  One of the primary goals for this release was to
 * "untangle" the class dependencies.  
 *
 * <p>jEAC was substantially redesigned for v2.0.  Much of the functionality
 * that used to be in this class has been moved to other subordinate classes.
 * One of the major goals for this release was to "untangle" the code and 
 * strengthen the boundaries between core components.
 * 
 * <p>For v2.0, these components (interface, timing, and device drivers) are 
 * organized under <i>Managers</i>, which support and host the various tools
 * and utilities.  <code>JEAC</code> is responsible for overseeing each of 
 * the component managers.
 * 
 * @author   Ryan R. Varick
 * @since    1.0.0
 *
 */
public final class JEAC
{

	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	
//	
//	/** Singleton instance of the class. */
//	private static JEAC INSTANCE;
//	
//	
//	
//	/* -------------------------[ Generic class methods ]------------------------- */
//	
//	/**
//	 * Private contructor to prevent instantiation.
//	 * 
//	 * <p>This class follows the Singleton design pattern; it is not meant to
//	 * be instantiated directly.  Instead, the instance should be retrieved via
//	 * the <code>getInstance()</code> method.
//	 * 
//	 * @author   Ryan R. Varick
//	 * @since    2.0.0
//	 *
//	 */
//	private JEAC() { }
//	
//	/**
//	 * Returns the single instance of the class.
//	 * 
//	 * <p>The idea is to prevent multiple instances of jEAC from running, in
//	 * the same JVM anyway.  There shouldn't be a need for simultaneous instances.
//	 * 
//	 * @return   Class instance.
//	 * 
//	 * @author   Ryan R. Varick
//	 * @since    2.0.0
//	 * 
//	 */
//	public static JEAC getInstance()
//	{
//		if(INSTANCE == null)
//		{ 
//			INSTANCE = new JEAC();
//		}
//		return INSTANCE;
//	}
//	
//	/**
//	 * Prevents attempts to create multiple instances via cloning.
//	 * 
//	 * @throws   Don't copy that floppy!
//	 * 
//	 * @author   Ryan R. Varick
//	 * @since    2.0.0
//	 * 
//	 */
//	public Object clone() throws CloneNotSupportedException
//	{
//		throw new CloneNotSupportedException();
//	}
//
	
	
	
	
	
	
	
	
	/**
	 * Responsible for getting the good Mr. jEAC up and running.  
	 * 
	 * @param args   Command line arguments are ignored.
	 * 
	 * @author       Ryan R. Varick
	 * @since        1.0.0
	 * 
	 */
	public static void main(String[] args)
	{
//		try
//		{
			InterfaceManager ui = InterfaceManager.getInstance();
			ui.init();
			
			DeviceManager dm = DeviceManager.getInstance();
//			ThreadManager thread = new ThreadManager();
		
//			ui.loadDrivers(deviceManager);
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
	}

}
