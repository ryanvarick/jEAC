package edu.indiana.cs.eac.ui.listeners;

import java.awt.event.*;
import javax.swing.*;

import edu.stanford.ejalbert.*;

public class LauncherListener implements ActionListener
{
	private String url;

	public LauncherListener(String url)
	{
		this.url = url;
	}

	public void actionPerformed(ActionEvent arg0)
	{
		try
		{
			BrowserLauncher b = new BrowserLauncher();
			b.openURLinBrowser(url);
		}
		catch(Exception e)
		{
			JOptionPane.showInternalConfirmDialog(null, "Yo", "Yo2", JOptionPane.OK_OPTION);
		}
		
	}

}
