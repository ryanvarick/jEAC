package edu.indiana.cs.eac.ui.listeners;

import java.awt.event.*;

public class ExitListener extends WindowAdapter implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
//		shutdown();
		
		System.exit(0);
	}
	public void windowClosing(WindowEvent e)
	{
//		shutdown();
		System.exit(0);
	}
}
