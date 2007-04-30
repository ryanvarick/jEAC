package edu.indiana.cs.eac.ui.listeners;

import java.awt.event.*;

import javax.swing.*;
import javax.swing.tree.*;

public class DeviceTreeMouseListener extends MouseAdapter
{
	private JTree tree;
	
	public DeviceTreeMouseListener(JTree tree)
	{
		this.tree = tree;
	}
	
	public void mousePressed(MouseEvent e)
	{
		maybeShowPopup(e);
	}

	public void mouseReleased(MouseEvent e)
	{
		maybeShowPopup(e);
	}

	// TODO: work in updateSelectedDriver(d), somewhere
	private void maybeShowPopup(MouseEvent e)
	{
		if(e.isPopupTrigger())
		{
			// select the active node on right click
	         int selRow = tree.getRowForLocation(e.getX(), e.getY());
	         TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
	         
	         tree.setSelectionPath(selPath);
	         
//	         if(selRow != -1) {
//	             if(e.getClickCount() == 1) {
//	                 mySingleClick(selRow, selPath);
//	             }
//	             else if(e.getClickCount() == 2) {
//	                 myDoubleClick(selRow, selPath);
//	             }
//	         }
			
			
			JPopupMenu p  = new JPopupMenu();
			JMenuItem jmi = new JMenuItem("Test");
			JMenuItem jmi2 = new JMenuItem("Test");
			JMenuItem jmi3 = new JMenuItem("Test");
			p.add(jmi);
			p.add(jmi2);
			p.add(jmi3);
			  
//			p.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}