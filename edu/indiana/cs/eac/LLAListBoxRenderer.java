/**
 * LLAListBoxRenderer - Renders images for the LLA dropbox.
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

public class LLAListBoxRenderer extends JLabel implements ListCellRenderer
{
	public LLAListBoxRenderer()
	{
		setOpaque(true);
		setHorizontalAlignment(CENTER);
		setVerticalAlignment(CENTER);
	}
	
	/**
	 * Make the LLAs all nice and pretty-like.
	 * 
	 * @see http://java.sun.com/docs/books/tutorial/uiswing/components/combobox.html
	 * 
	 */
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{		
		// handle highlighting
		if(isSelected)
		{
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		}
		else
		{
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		
		// index isn't always valid, so we have to use value
		//  NOTE: the linked reference does not work with Java 1.5, we have to do it this way
		String raw    = value.toString();
		Integer i     = new Integer(raw);
		int selectedIndex = i.intValue();

		// adjust 0-based index
		selectedIndex += 1;
		
		// add the leading-zero, if necessary
		String suffix;
		if(selectedIndex < 10) { suffix = "0" + selectedIndex; }
		else { suffix = "" + selectedIndex; }
		
		// use the index to select the appropriate image
		java.net.URL imageURL = JEAC.class.getResource("images/lla_" + suffix + ".gif");
		if(imageURL != null)
		{
		    setIcon(new ImageIcon(imageURL));
			setText(suffix + "  ");
			setFont(list.getFont());
		}
		else
		{
			System.err.println("Could not load LLA function image.");
		}
		
		return this;
	}
	
}
