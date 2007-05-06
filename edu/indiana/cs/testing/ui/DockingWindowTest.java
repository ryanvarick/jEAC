
package edu.indiana.cs.testing.ui;

import java.awt.*;

import javax.swing.*;
import javax.swing.text.*;

import net.infonode.docking.*;
import net.infonode.docking.View;
import net.infonode.docking.util.*;

public class DockingWindowTest
{

	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch(Exception e)
		{
			System.err.println("Could not load platform-native look-and-feel.");
		}

		
		
		View[] views = new View[5];
		ViewMap viewMap = new ViewMap();
		for(int i = 0; i < views.length; i++)
		{
			views[i] = new View("View " + i, null, new JLabel("This is view "
					+ i + "!"));
//			viewMap.addView(i, views[i]);
		}
		viewMap.addView(0, views[2]);
		viewMap.addView(1, views[3]);
		viewMap.addView(2, views[4]);
		
		ViewMap v2 = new ViewMap();
		v2.addView(0, views[0]);
		v2.addView(1, views[1]);

		RootWindow r2 = DockingUtil.createRootWindow(v2, true);
		r2.setWindow(new SplitWindow(false, 0.4f, views[0], views[1]));
		
		View v = new View("Yo!", null, r2);
		
//				new TabWindow(new DockingWindow[] { views[1], views[2] }))); 
				
		RootWindow rootWindow = DockingUtil.createRootWindow(viewMap, true);
		rootWindow.setWindow(
				new SplitWindow(true, 0.25f, 
						v,
						new SplitWindow(false, views[3], views[4])));
		


		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());
//		frame.add(rootWindow, BorderLayout.CENTER);
		frame.add(rootWindow);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
