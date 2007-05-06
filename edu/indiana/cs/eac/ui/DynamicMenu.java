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

import javax.swing.*;
import javax.swing.event.*;

/**
 * Abstract definition of a menu that is built dynamically.
 * 
 * <p>Often, menus contain elements that change depending on the state of the
 * program.  This class wraps Swing's <code>JMenu</code> class and adds an
 * internal menu listener which will rebuild the menu each time it is shown.
 * Subclasses need only to fill in the abstract <code>buildMenu()</code> method.
 * 
 * @author   Ryan R. Varick
 * @since    2.0.0
 * 
 *
 */
public abstract class DynamicMenu extends JMenu
{
	/**
	 * Returns a new <code>DynamicMenu</code> object.
	 *
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 * 
	 */
	public DynamicMenu()
	{
        addMenuListener(new MenuListener()
        {
            public void menuCanceled   (MenuEvent e) {}
            public void menuDeselected (MenuEvent e) { removeAll(); }
            public void menuSelected   (MenuEvent e) { buildMenu(); }
        });
		
	}	
	
	/**
	 * Builds (or re-builds) the menu.
	 * 
	 * <p>Note that this method is called automatically by the menu listener
	 * defined in the constructor.
	 * 
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 *
	 */
	protected abstract void buildMenu();
	
}
