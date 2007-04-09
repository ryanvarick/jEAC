package edu.indiana.cs.testing.ui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.tree.*;

import net.infonode.docking.FloatingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.util.DockingUtil;
import net.infonode.tabbedpanel.*;
import net.infonode.tabbedpanel.theme.ShapedGradientTheme;
import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.infonode.tabbedpanel.titledtab.TitledTabProperties;
import net.infonode.util.*;

import edu.indiana.cs.eac.ui.*;

public class DockTest
{
	private TitledTabProperties titledTabProperties = new TitledTabProperties();

	public DockTest()
	{
			try
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} 
			catch(Exception e)
			{
				System.err.println("Could not load platform-native look-and-feel.");
			}
		JFrame frame = new JFrame();
		
		
		TabbedPanel tp = new TabbedPanel();
		ShapedGradientTheme theme = new ShapedGradientTheme();
		
		JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new MDIDesktopPane(), tp);
//		jsp.setOneTouchExpandable(true);

//	    tp.setTabAreaComponents(new JComponent[]{createCloseAllTabsButton(tp)});
	    
		JSplitPane jsp2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getDevicePanel(), new JPanel());
	    
	    TitledTab tab = new TitledTab("Device Manager", null, jsp2, null); 
	    tab.getProperties().addSuperObject(titledTabProperties);
	    tp.addTab(tab);
	    
	    tp.getProperties().setTabAreaOrientation(Direction.RIGHT);
        titledTabProperties.getNormalProperties().setDirection(Direction.DOWN);
   	    titledTabProperties.addSuperObject(theme.getTitledTabProperties());

   	    // put it all together
   	    frame.getContentPane().add(jsp);
	    titledTabProperties.addSuperObject(theme.getTitledTabProperties());

   	    // add a menu, for now
		MenuManager menu = MenuManager.getInstance();
		frame.setJMenuBar(menu.getMenu());
		
//		// let's try to create a new window
//		RootWindow rootWindow = DockingUtil.createRootWindow(null, null, true);
//
//        FloatingWindow fw = rootWindow.createFloatingWindow(new Point(50, 50),
//                new Dimension(300, 200),
//                getDynamicView(getDynamicViewId()));
//
//        // Show the window
//        fw.getTopLevelAncestor().setVisible(true);
		
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(640, 480));
		frame.setTitle("Infonode docking test");
		frame.setVisible(true);
	}
	
	
	public static void main(String[] args)
	{
		DockTest docktest = new DockTest();
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
		  
		  panel.add(tools, BorderLayout.SOUTH);
		  
		  JTree jt = getTree();
		  panel.add(jt, BorderLayout.CENTER);

		  return panel;  
	  }
	
	
//	private JButton createCloseAllTabsButton(final TabbedPanel tabbedPanel) {
//		    final JButton closeButton = createXButton();
//		    closeButton.addActionListener(new ActionListener() {
//		      public void actionPerformed(ActionEvent e) {
//		        // Iterate over all tabs and remove them.
//		        int tabCount = tabbedPanel.getTabCount();
//		        for (int i = 0; i < tabCount; i++)
//		          tabbedPanel.removeTab(tabbedPanel.getTabAt(0));
//		      }
//		    });

}
