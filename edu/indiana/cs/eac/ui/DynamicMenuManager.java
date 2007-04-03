package edu.indiana.cs.eac.ui;

import javax.swing.*;
import javax.swing.event.*;

public abstract class DynamicMenuManager extends JMenu
{
	/**
	 * 
	 *
	 */
	public DynamicMenuManager()
	{
        addMenuListener(new MenuListener()
        {
            public void menuCanceled   (MenuEvent e) {}
            public void menuDeselected (MenuEvent e) { removeAll(); }
            public void menuSelected   (MenuEvent e) { buildMenu(); }
        });
		
	}	
	
	/**
	 * 
	 *
	 */
	protected abstract void buildMenu();	
}
