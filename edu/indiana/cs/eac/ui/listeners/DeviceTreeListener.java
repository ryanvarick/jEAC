package edu.indiana.cs.eac.ui.listeners;

import javax.swing.event.*;
import javax.swing.tree.*;

import edu.indiana.cs.eac.hardware.*;

public class DeviceTreeListener implements TreeSelectionListener
{
	public void valueChanged(TreeSelectionEvent e)
	{
//	        DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.getNewLeadSelectionPath().getLastPathComponent();
		  
		if(node == null)
		{
			System.out.println("Null: "); return;
		}
		Object nodeInfo = node.getUserObject();
		  
		if (node.isLeaf())
		{
			Device d = (Device)nodeInfo; // now we rock
			
			
			System.out.println("Leaf: " + node.toString() + "; name: " + d.getDeviceName());
		}
		else
		{
			System.out.println("Branch: " + node.toString()); 
		}
	}
}