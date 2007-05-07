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

import java.net.*;
import javax.swing.*;



/**
 * Collection of utilities used by <i>jEAC</i>.
 * 
 * @author   Ryan R. Varick
 * @since    2.0.0
 *
 */
public class JEACUtilities
{
	/** Relative path to project-related resource files. */
	private static final String RESOURCE_PATH = "resources/";
	
	
	
	private JEACUtilities()
	{
		// prevent instantiation
	}
	
	
	
	/**
	 * Loads an image from the resources directory.
	 * 
	 * @param image   Name of the image to load, including file extension.
	 * @return        <code>ImageIcon</code> containing the requested image,
	 * 				  or <code>null</code> if the image could not be loaded.
	 * 
	 * @author        Ryan R. Varick
	 * @since         1.0.0
	 * 
	 * @version       1.1.0
	 *   
	 */
	public static ImageIcon getImageIcon(String image)
	{
		URL imageURL = JEAC.class.getResource(RESOURCE_PATH + image);
		
		ImageIcon icon = null;
		if (imageURL != null)
		{
		    icon = new ImageIcon(imageURL);
		}
		else
		{
			System.err.println("Could not load resource: " + image);
		}
		
		return icon;
	}

}
