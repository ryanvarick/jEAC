/**
 * AboutFrame.java - Help > About
 * 
 * Copyright (C) 2006.  All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 * 
 */

package edu.indiana.cs.eac.ui.deprecated;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** @deprecated */
public class AboutFrame extends JFrame
{
	private static final int FRAME_WIDTH  = 340;
	private static final int FRAME_HEIGHT = 340;
	

	
	/**
	 * Constructor - instantiates a new AboutFrame.
	 *
	 */
	public AboutFrame()
	{
		Container container = getContentPane();
		container.setLayout(new GridBagLayout());
		
		int row = 0;
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.gridx   = 0;
		constraints.gridy   = row++;
		constraints.anchor  = GridBagConstraints.FIRST_LINE_START;
		constraints.fill    = GridBagConstraints.BOTH;

		
		/* --------------------[ Title ]-------------------- */
		
		JPanel title = new JPanel();

		JLabel appTitle = new JLabel("jEAC", JLabel.CENTER);
		appTitle.setFont(new Font(null, Font.BOLD, 18));
		title.add(appTitle);
		
		container.add(title, constraints);
		constraints.gridy = row++;
		
		/* --------------------[ Information ]-------------------- */
		
		JPanel info = new JPanel();
		info.setLayout(new GridLayout(8, 1));
		
		JLabel tagline = new JLabel("Real-time 2D/3D EAC Interaction", JLabel.CENTER);
		info.add(tagline);

//		JLabel version = new JLabel("v" + JEAC.getVersion() + ", build " + JEAC.getBuild(), JLabel.CENTER);
//		info.add(version);
		
		JLabel blank1 = new JLabel("", JLabel.CENTER);
		info.add(blank1);

		JLabel drew = new JLabel("Drew Kipfer, lead developer", JLabel.CENTER);
		info.add(drew);

		JLabel ryan = new JLabel("Ryan R. Varick, lead developer", JLabel.CENTER);
		info.add(ryan);

		JLabel blank2 = new JLabel("", JLabel.CENTER);
		info.add(blank2);
		
		JLabel bryce = new JLabel("Bryce Himebaugh, hardware engineer", JLabel.CENTER);
		info.add(bryce);
		
		JLabel adam = new JLabel("Adam Miller, driver support", JLabel.CENTER);
		info.add(adam);
		
		container.add(info, constraints);
		constraints.gridy = row++;

		/* --------------------[ Copyright ]-------------------- */
		
		JPanel copyright = new JPanel();
		copyright.setBorder(BorderFactory.createTitledBorder("License Information"));
				
		JTextArea gpl = new JTextArea(
				"Copyright (C) 2006 Drew Kipfer, Ryan R. Varick.\n\n" +
				"This program is free software; you can redistribute it and/or\n " + 
				"modify it under the terms of the GNU General Public License as\n " +
				"published by the Free Software Foundation; either version 2 of\n " + 
				"the License, or (at your option) any later version.");
		
		gpl.setBackground(title.getBackground());
		gpl.setColumns(40);
		gpl.setEditable(false);
		gpl.setFont(title.getFont());
		gpl.setRows(6);
		
		copyright.add(gpl);

		container.add(copyright, constraints);
		constraints.gridy = row++;
		
		/* --------------------[ Finalize window ]-------------------- */
		
		JButton ok = new JButton("OK");
		ok.addActionListener(new ExitListener());
		container.add(ok, constraints);
		constraints.gridy = row++;
		
		// size and location
		setSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width - getWidth()) / 2;
		int y = (screenSize.height - getHeight()) / 2;
		setLocation(x, y);

//		setIconImage(JEAC.getApplicationIcon());
		setResizable(false);
		setTitle("About jEAC");
		setVisible(true);
	}
	
	/**
	 * Listen for exit events and insures clean shutdown.
	 *
	 */
	private class ExitListener extends WindowAdapter implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			dispose();
		}
		public void windowClosing(WindowEvent e)
		{
			dispose();
		}
	}
}
