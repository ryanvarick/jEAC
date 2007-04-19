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

package edu.indiana.cs.eac.ui;

import java.awt.*;
import java.io.*;

import javax.swing.*;

import edu.indiana.cs.eac.*;

public class AboutWindow extends JInternalFrame
{
	private static final String WINDOW_TITLE = "About jEAC";
	private static final int WINDOW_HEIGHT   = 480;
	private static final int WINDOW_WIDTH    = 320;

	/**
	 * 
	 *
	 */
	public AboutWindow()
	{
//		this.setSize(new Dimension(WINDOW_HEIGHT, WINDOW_WIDTH));
//		this.setVisible(true);
		
//		new AboutFrame();
		
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		java.net.URL helpURL = JEAC.class.getResource("resources/about.html");
		if (helpURL != null) {
		    try {
		        editorPane.setPage(helpURL);
		    } catch (IOException e) {
		        System.err.println("Attempted to read a bad URL: " + helpURL);
		    }
		} else {
		    System.err.println("Couldn't find file: about.html");
		}

//		Put the editor pane in a scroll pane.
		JScrollPane editorScrollPane = new JScrollPane(editorPane);
		editorScrollPane.setVerticalScrollBarPolicy(
		                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		editorScrollPane.setPreferredSize(new Dimension(250, 145));
		editorScrollPane.setMinimumSize(new Dimension(10, 10));
		
//		this.add(editorPane);
		
		JFrame test = new JFrame();
		test.setSize(new Dimension(320, 480));
		test.add(editorPane);
		test.setVisible(true);
		
		InterfaceManager.getInstance().getDesktop().add(this);
		
	}
}
