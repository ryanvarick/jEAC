/**
 * LoadingFrame.java - Rudimentary loading window for UI feedback.
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

public class LoadingFrame extends JFrame
{
	public LoadingFrame()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch(Exception e)
		{
			System.err.println("Unable to load native look and feel.");
		}
		
		JLabel label = new JLabel("Looking for EACs...", new ImageIcon(JEAC.getImage("loading.gif")), JLabel.CENTER);
		label.setVerticalTextPosition(JLabel.BOTTOM);
		label.setHorizontalTextPosition(JLabel.CENTER);
	  
		getContentPane().add(label, BorderLayout.CENTER);
		getContentPane().setBackground(Color.WHITE);
		
		// screen size
		setSize(new Dimension(150, 150));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width - this.getWidth()) / 2;
		int y = (screenSize.height - this.getHeight()) / 2;
		setLocation(x, y);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(JEAC.getApplicationIcon());
		setResizable(false);
		setTitle("Loading...");
		setVisible(true);
		paintAll(getGraphics());
	}
}
