/*
 * This file is part of jEAC (http://jeac.sf.net/).
 * 
 * Copyright (C) 2006.  All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 * 
 */

package edu.indiana.cs.eac.ui;

import java.awt.*;
import javax.swing.*;

/**
 * Provides UI feedback while jEAC looks for local uEACs.
 * 
 * @author   Ryan R. Varick
 * @since    1.0.0
 * 
 */

// TODO: Split this into ProgressBar and LoadingFrame functionality.
public class LoadingFrame extends JInternalFrame
{
	private static final int PROGRESS_MIN = 0;
	private static final int PROGRESS_MAX = 100;

	private static final String TITLE = "Looking for local uEACs...";
	private static final int FRAME_SIZE_X = 300;
	private static final int FRAME_SIZE_Y = 50;
	
	
	private int stepSize;
	private JProgressBar progress;
	
	
	
	/* -------------------------[ Generic class methods ]------------------------- */
	
	/**
	 * Returns a new <code>LoadingFrame</code> instance.
	 * 
	 * @param steps   Number of times...
	 * 
	 * @author        Ryan R. Varick
	 * @since         1.2.0
	 * 
	 */
	public LoadingFrame(int steps)
	{
		// compute the increment 
		stepSize = PROGRESS_MAX / Math.max(1, steps);
		
		// initialize the progress bar
		progress = new JProgressBar();
	    progress.setMinimum(PROGRESS_MIN);
	    progress.setMaximum(PROGRESS_MAX);
	    progress.setValue(0);
	    
//		frame.setUndecorated(true);
	    add(progress);

	    pack();
	    setSize(FRAME_SIZE_X, FRAME_SIZE_Y);
	    
		// center on screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width - getWidth()) / 2;
		int y = (screenSize.height - getHeight()) / 2;
		setLocation(x, y);
		
		// finalize
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setIconImage(JEAC.getApplicationIcon());
		setResizable(false);
		setTitle(TITLE);
		setVisible(true);

		// start with some progress!
		increment();
	}
	
	/**
	 * Returns a new <code>LoadingFrame</code> instance.
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.0.0
	 * 
	 * @deprecated
	 *
	 */
	public LoadingFrame()
	{
		// TODO: parameterize this (using jEAC constants)
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		

		
		
//	    // This message describes the task that is running
//	    String message = "Description of Task";
//	    
//	    // This string describes a subtask; set to null if not needed
//	    String note = "subtask";
//	    
//	    // Set the title of the dialog if desired
//	    String title = "Task Title";
//	    UIManager.put("ProgressMonitor.progressText", title);
//	    
//	    // Create a progress monitor dialog.
//	    // The dialog will use the supplied component's frame as the parent.
//	    int min = 0;
//	    int max = 100;
//	    ProgressMonitor pm = new ProgressMonitor(this, message, note, min, max);
//		add(pm);
//		
//		JLabel label = new JLabel("Searching for local uEACs...", 
//								new ImageIcon(JEAC.getImage("loading.gif")), JLabel.CENTER);
//		label.setVerticalTextPosition(JLabel.BOTTOM);
//		label.setHorizontalTextPosition(JLabel.CENTER);
//	  
//		getContentPane().add(label, BorderLayout.CENTER);
//		getContentPane().setBackground(Color.WHITE);
//		
//		// screen size
//		setSize(new Dimension(150, 150));
//		
//		// center on screen
//		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//		int x = (screenSize.width - this.getWidth()) / 2;
//		int y = (screenSize.height - this.getHeight()) / 2;
//		setLocation(x, y);
//		
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setIconImage(JEAC.getApplicationIcon());
//		setResizable(false);
//		setTitle("Loading...");
//		setVisible(true);
//		paintAll(getGraphics());
////		
//		pm.setProgress(10);

	}
	
	
	
	/* -------------------------[ LoadingFrame methods ]------------------------- */
	
	/**
	 * Updates the loading frame by a predetermined amount.
	 * 
	 * <p>The increment is determined automatically, given the number of steps
	 * the progress bar is expected to report.
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 *
	 */
	public void increment()
	{
		// TODO: Handle > 100 errors
		int value = progress.getValue() + stepSize;
		progress.setValue(value);
	}
	
}
