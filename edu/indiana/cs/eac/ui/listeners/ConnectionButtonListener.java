package edu.indiana.cs.eac.ui.listeners;

import java.awt.event.*;
import javax.swing.*;

import edu.indiana.cs.eac.ui.*;


public class ConnectionButtonListener implements ActionListener
{
	private DevicePanelManager dpm;
//	private JButton button;
	
	public ConnectionButtonListener(DevicePanelManager dpm)
	{
		this.dpm    = dpm;
//		this.button = button;
	}
	

	public void actionPerformed(ActionEvent e)
	{
		dpm.processButtonEvent(button);
	}
}
