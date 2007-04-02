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
import edu.indiana.cs.eac.hardware.*;

/**
 * Base class for jEAC.
 * 
 * <p>Much of the functionality that used to be in this class
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
	private JEAC()
	{
		
	}
	public static final JEAC getInstance()
	{
		return new JEAC();
	}
	
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
		InterfaceManager ui = InterfaceManager.getInstance();
		ui.init();
		ui.loadDrivers();
			
//		DeviceManager dm = DeviceManager.getInstance();
//		ThreadManager thread = new ThreadManager();
	}

}
