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
public class LoadingFrame
{
	private static final int PROGRESS_MIN = 0;
	private static final int PROGRESS_MAX = 100;

	private static final String TITLE = "Looking for local uEACs...";
	private static final int FRAME_SIZE_X = 300;
	private static final int FRAME_SIZE_Y = 50;
	
	
	private int stepSize;
	private JProgressBar progress;
	private JInternalFrame frame;
	
	
	
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
	    
	    frame = new JInternalFrame();
	    InterfaceManager.getInstance().getDesktop().add(frame);
	    
//		frame.setUndecorated(true);
	    frame.add(progress);

	    frame.pack();
	    frame.setSize(FRAME_SIZE_X, FRAME_SIZE_Y);
	    
		// center on screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width - frame.getWidth()) / 2;
		int y = (screenSize.height - frame.getHeight()) / 2;
//		frame.setLocation(x, y);
		
		// finalize
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setIconImage(JEAC.getApplicationIcon());
		frame.setResizable(false);
		frame.setTitle(TITLE);
		frame.setVisible(true);

		// start with some progress!
		increment();
	}

	
	
	/* -------------------------[ LoadingFrame methods ]------------------------- */
	
	/**
	 * Updates the loading frame by a predetermined amount.
	 * 
	 * <p>The increment is fixed, determined automatically by the number of steps
	 * passed to the constructor. 
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 *
	 */
	public void increment()
	{
		// TODO: Handle more than 100 steps
		int value = progress.getValue() + stepSize;
		progress.setValue(value);
	}
	
	public void close()
	{
		frame.dispose();
	}
	
	public void setTitle(String title)
	{
		frame.setTitle(title);
	}
	
}
