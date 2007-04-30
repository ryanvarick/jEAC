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
import edu.indiana.cs.eac.hardware.*;

/**
 * Base class for jEAC.
 * 
 * <p>Much of the functionality that used to be in this class is now located
 * elsewhere.  One of the primary goals for this release was to better organize
 * the code into core components; namely the UI, timing, and hardware.  As such,
 * <code>JEAC</code> has been heavily refactored.  Major components are now 
 * arranged under <i>Manager</i> classes, which act as the top-level interface
 * for other components.
 * 
 * @author    Ryan R. Varick
 * @since     1.0.0
 * 
 * @version   2.0.0
 *
 */
public final class JEAC
{

	/**
	 * Starts jEAC.  
	 * 
	 * @param args   Command line arguments are ignored.
	 * 
	 * @author       Ryan R. Varick
	 * @since        1.0.0
	 * 
	 * @version      2.0.0
	 * 
	 */
	public static void main(String[] args)
	{
		HardwareManager hm = HardwareManager.getInstance();
		TimingManager tm = TimingManager.getInstance();

		InterfaceManager ui = new InterfaceManager(hm, tm);
		ui.init();
		ui.show();
	}
}
