/**
 * BlankIcon - Class to create a blank icon.
 * 
 * Copyright (C) 2006.  All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 * 
 *
 * 
 */

package edu.indiana.cs.eac.ui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

/** 
 * 
 * @deprecated Move to <code>JEAC_Utilities</code>
 *
 */
public class BlankIcon implements Icon, Serializable
{
	int height, width;
	
	public BlankIcon(int height, int width)
	{
		this.height = height;
		this.width  = width;
	}
	
	public int getIconHeight() { return this.height; }
	public int getIconWidth() { return this.width; }
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		/* Do nothing. */
	}
}