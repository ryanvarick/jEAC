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

import edu.indiana.cs.eac.ui.*;
import edu.indiana.cs.eac.driver.*;

/**
 * Entry point for jEAC.
 * 
 * <p>This class was gutted for version 2.0.  It now simply invokes the various
 * Managers required to oversee program sub-components.  One of the major goals
 * for this version was to strengthen the boundaries between parts of the program. 
 * 
 * @author   Ryan R. Varick
 * @since    1.0.0
 *
 */
public class JEAC
{
	
	public static void main(String[] args)
	{
		InterfaceManager ui = new InterfaceManager();
//		DriverManager driver = new DriverManager();
//		ThreadManager thread = new ThreadManager();
		
//		driver.buildDriverList(ui.getLoadingFrame());
	}

}
