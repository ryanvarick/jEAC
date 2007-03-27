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

/**
 * A development environment for the extended analog computer.
 * 
 * <p>This is the top-level class for the jEAC user interface.  It simply
 * instantiates the MDI manager and handles other bootstrapping functions.
 * 
 * @author   Ryan R. Varick
 * @since    1.3.0
 * 
 */
public class JEAC2
{
	/** Initial size of the UI. */
	public static int INITIAL_SIZE_X = 640;
	
	/** Initial size of the UI. */
	public static int INITIAL_SIZE_Y = 480;


	
	/* -------------------------[ Generic class methods ]------------------------- */

	/**
	 * Entry-point for the MDI interface.
	 * 
	 * @param args   Command line arguments are ignored.
	 * 
	 * @author       Ryan R. Varick
	 * @since        1.3.0
	 * 
	 */
	public static void main(String[] args)
	{
		MDIManager app = new MDIManager();
		app.setSize(INITIAL_SIZE_X, INITIAL_SIZE_Y);
		app.setVisible(true);
	}
}