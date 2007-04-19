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

package edu.indiana.cs.eac.ui.listeners;

import java.awt.event.*;

import edu.indiana.cs.eac.ui.*;



/**
 * 
 * 
 * @author   Ryan R. Varick
 * @since    2.0.0
 * 
 */
public class AboutMenuItemListener implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
		AboutWindow aboutWindow = new AboutWindow();
	}
}