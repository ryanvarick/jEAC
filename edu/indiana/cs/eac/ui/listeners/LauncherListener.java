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
import javax.swing.*;

import edu.stanford.ejalbert.*;



/**
 * Launches a URL in the default browser in response to a UI event.
 * 
 * <p><i>Note that this class is just a wrapper for
 * <a href="http://browserlaunch2.sourceforge.net">BrowserLaunch2</a>, which 
 * can be downloaded from SourceForge.</i>
 * 
 * @author   Ryan R. Varick
 * @since    2.0.0
 *
 */
public class LauncherListener implements ActionListener
{
	private static final String ERROR_TITLE   = "Page not available: "; 
	private static final String ERROR_MESSAGE =
		"Could not show the requested page because the browser failed to load.";
	
	private String url;

	public LauncherListener(String url)
	{
		this.url = url;
	}

	public void actionPerformed(ActionEvent arg0)
	{
		try
		{
			BrowserLauncher b = new BrowserLauncher();
			b.openURLinBrowser(url);
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, ERROR_MESSAGE, ERROR_TITLE + url, JOptionPane.ERROR_MESSAGE);
			System.err.println("Browser error:");
			e.printStackTrace();
		}
	}

}
