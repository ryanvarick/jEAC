package edu.indiana.cs.testing.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import edu.indiana.cs.eac.ui.MDIDesktopPane;
import edu.indiana.cs.eac.ui.MenuManager;

import net.infonode.docking.*;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.util.*;
import net.infonode.tabbedpanel.*;
import net.infonode.tabbedpanel.theme.ShapedGradientTheme;
import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.infonode.tabbedpanel.titledtab.TitledTabProperties;
import net.infonode.util.*;

public class NewUI
{

	public NewUI()
	{
		
		// swing component theme
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch(Exception e)
		{
			System.err.println("Could not load platform-native look-and-feel.");
		}
		
		// internal theme
//		ShapedGradientTheme theme = new ShapedGradientTheme();
//		TitledTabProperties titledTabProperties = new TitledTabProperties();
//	    titledTabProperties.addSuperObject(theme.getTitledTabProperties());

	    // jframe container
		JFrame frame = new JFrame();
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(640, 480));
		frame.setTitle("Infonode docking test");

		// views
		ViewMap viewMap = new ViewMap();
		View cm = new View("Workbench", null, new MDIDesktopPane());
		View dm = new View("Device Manager", null, getDevicePanel());
		

		
		viewMap.addView(1, cm);
		viewMap.addView(2, dm);
		
//		View[] views = new View[5];
//		for (int i = 0; i < views.length; i++)
//		{
//			views[i] = new View("View " + i, null, new JLabel("This is view " + i + "!"));
////			views[i].setFont(Font.SANS_SERIF);
//			
//			viewMap.addView(i, views[i]);
//		}
		
		
		RootWindow rootWindow = DockingUtil.createRootWindow(viewMap, true);
		rootWindow.setWindow(new SplitWindow(true, 0.4f, cm, dm));

//		rootWindow.getRootWindowProperties().getDockingWindowProperties().setCloseEnabled(false);
//		rootWindow.getRootWindowProperties().getDockingWindowProperties().setMaximizeEnabled(false);

		// turn off tab window controls (too much clutter)
		rootWindow.getRootWindowProperties().getTabWindowProperties().getCloseButtonProperties().setVisible(false);
		rootWindow.getRootWindowProperties().getTabWindowProperties().getDockButtonProperties().setVisible(false);
		rootWindow.getRootWindowProperties().getTabWindowProperties().getMaximizeButtonProperties().setVisible(false);
		rootWindow.getRootWindowProperties().getTabWindowProperties().getMinimizeButtonProperties().setVisible(false);
		rootWindow.getRootWindowProperties().getTabWindowProperties().getUndockButtonProperties().setVisible(false);
//				
//		rootWindow.setWindow(new SplitWindow(true, 0.4f,
//											new SplitWindow(false, views[0], new SplitWindow(false, views[1], views[2])),
//											new TabWindow(new DockingWindow[]{views[3], views[4]})));

		// theme
		DockingWindowsTheme theme = new ShapedGradientDockingTheme();
		rootWindow.getRootWindowProperties().addSuperObject(theme.getRootWindowProperties());
		
		// add menu bar
		rootWindow.getWindowBar(Direction.RIGHT).setEnabled(true);
//		rootWindow.getWindowBar(Direction.RIGHT).addTab(views[3]);
		
		// hook up the menu manager
		MenuManager menu = MenuManager.getInstance();
//		MenuManager menu = (MenuManager)MenuManager.getInstance(MenuManager.class);
		frame.setJMenuBar(menu.getMenu());
		
		JLabel sb = new JLabel("Status bar: status");
		
		// finalize
		frame.setLayout(new BorderLayout());
	    frame.add(rootWindow, BorderLayout.CENTER);
	    frame.add(sb, BorderLayout.SOUTH);
		frame.setVisible(true);
	}
	
	public static void main(String[] args)
	{
		NewUI ui = new NewUI();
	}
	
	
	
	
	
	
	
	
	public JTree getTree()
	{
		    Object[] hierarchy =
		      { "Available Devices",
		        "Null driver (inactive test)",
		        "Random driver (active test)",
		        new Object[] { "Network EACs",
	                       "eac1.cs.indiana.edu",
	                       "eac3.cs.indiana.edu",
	                       "eac4.cs.indiana.edu" },
	            new Object[] { "Local uEACs",
		    		       "COM5",
		    		       "COM13", }};
		    DefaultMutableTreeNode root = processHierarchy(hierarchy);
		    JTree tree = new JTree(root);
		    
		    for(int i = 0; i < tree.getRowCount(); i++)
		    {
		    	tree.expandRow(i);
		    }
		    return tree;
	}
	
	  private DefaultMutableTreeNode processHierarchy(Object[] hierarchy) {
		    DefaultMutableTreeNode node =
		      new DefaultMutableTreeNode(hierarchy[0]);
		    DefaultMutableTreeNode child;
		    for(int i=1; i<hierarchy.length; i++) {
		      Object nodeSpecifier = hierarchy[i];
		      if (nodeSpecifier instanceof Object[])  // Ie node with children
		        child = processHierarchy((Object[])nodeSpecifier);
		      else
		        child = new DefaultMutableTreeNode(nodeSpecifier); // Ie Leaf
		      node.add(child);
		    }
		    return(node);
		  }
	

	  private JPanel getDevicePanel()
	  {
		  JPanel panel = new JPanel();
		  panel.setLayout(new BorderLayout());

		  JToolBar tools = new JToolBar();
		  tools.setFloatable(false);
		  
		  JButton b = new JButton();
		  b.setText("Rescan");
		  b.setToolTipText("Scans for new devices");
		  JButton c = new JButton();
		  c.setText("Connect");
		  c.setToolTipText("Scans for new devices");
		  JButton d = new JButton();
		  d.setText("Reset");
		  d.setToolTipText("Scans for new devices");
		  tools.add(b);
		  tools.addSeparator();
		  tools.add(c);
		  tools.add(d);
		  
		  panel.add(tools, BorderLayout.NORTH);
		  
		  JTree jt = getTree();
		  panel.add(jt, BorderLayout.CENTER);

		  return panel;  
	  }
	
}
