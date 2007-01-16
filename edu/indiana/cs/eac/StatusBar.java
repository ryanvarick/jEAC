/**
 * StatusBar.java - Status bar for JEAC.
 * 
 * @version 1.0.0
 * 
 * Copyright (C) 2006.  All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 */

package edu.indiana.cs.eac;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class StatusBar extends JPanel
{
	private JLabel deviceLabel, statusLabel;
	private Image image;
	
	// connection status codes
	private static int flag = 100;
	public static final int CONNECTED    = flag += 100;
	public static final int CONNECTING   = flag += 100;
	public static final int DISCONNECTED = flag += 100;
	public static final int LOADING      = flag += 100;
	public static final int PLOTTING     = flag += 100;
	public static final int PROBING      = flag += 100;
	public static final int RESETTING    = flag += 100;
	public static final int SAVING       = flag += 100;
	public static final int WARNING      = flag += 100;
	
	
	
	/**
	 * Constructor - instantiates a new status bar.
	 * 
	 */
	public StatusBar()
	{
		deviceLabel = new JLabel();
		statusLabel = new JLabel("", new ImageIcon(), SwingConstants.RIGHT);
		
		this.setLayout(new BorderLayout());
		this.add(deviceLabel, BorderLayout.WEST);
		this.add(statusLabel, BorderLayout.EAST);
		this.setBorder(new BevelBorder(BevelBorder.LOWERED));
	}	
	
	/**
	 * Tells the status bar to update the connection status.
	 * 
	 * @param int status - new status state (expected: StatusBar constant)
	 * 
	 */
	public void setStatus(int status)
	{
		String string = "Status: ";

		// stable states
		if(status == CONNECTED)
		{
			string += "Connected";
			image = JEAC.getImage("status_connected.gif");
		}
		else if(status == DISCONNECTED)
		{
			string += "Disconnected";
			deviceLabel.setText("");
			image = JEAC.getImage("status_disconnected.gif");
		}
		
		// activity states
		else if(status == CONNECTING)
		{
			string += "Connecting to EAC...";
			image = JEAC.getImage("status_warning.gif");
		}
		else if(status == LOADING)
		{
			string += "Loading configuration...";
			image = JEAC.getImage("status_warning.gif");
		}
		else if(status == PLOTTING)
		{
			string += "Updating visualization...";
			image = JEAC.getImage("status_warning.gif");
		}
		else if(status == PROBING)
		{
			string += "Looking for devices...";
			image = JEAC.getImage("status_warning.gif");
		}
		else if(status == RESETTING)
		{
			string += "Resetting EAC...";
			image = JEAC.getImage("status_warning.gif");
		}
		else if(status == SAVING)
		{
			string += "Saving configuration...";
			image = JEAC.getImage("status_warning.gif");
		}

		// invalid state
		else
		{
			System.err.println("Illegal status mode: " + status);
		}
		
		statusLabel.setText(string + " ");
		statusLabel.setIcon(new ImageIcon(image));
		
		// here's the magic command to update the status bar
		paintAll(getGraphics());
	}
	
	/**
	 * Updates the device the status bar reports JEAC is connected to.
	 * 
	 * @param String deviceName - String specifying the device
	 * 
	 */
	public void setConnectedTo(String deviceName)
	{
		String string = "Active EAC: " + deviceName;	
		deviceLabel.setText(" " + string);
	}
}