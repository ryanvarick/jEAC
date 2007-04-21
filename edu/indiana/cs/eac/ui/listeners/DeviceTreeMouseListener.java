package edu.indiana.cs.eac.ui.listeners;

import java.awt.event.*;

import javax.swing.*;

public class DeviceTreeMouseListener extends MouseAdapter {
	public void mousePressed(MouseEvent e)
	{
		maybeShowPopup(e);
	}

	public void mouseReleased(MouseEvent e)
	{
		maybeShowPopup(e);
	}

	private void maybeShowPopup(MouseEvent e)
	{
		if(e.isPopupTrigger())
		{
			JPopupMenu p  = new JPopupMenu();
			JMenuItem jmi = new JMenuItem("Test");
			JMenuItem jmi2 = new JMenuItem("Test");
			JMenuItem jmi3 = new JMenuItem("Test");
			p.add(jmi);
			p.add(jmi2);
			p.add(jmi3);
			  
			p.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}