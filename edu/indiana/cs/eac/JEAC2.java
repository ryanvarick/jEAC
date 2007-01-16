//based on http://www.javaworld.com/javaworld/jw-05-2001/jw-0525-mdi.html

package edu.indiana.cs.eac;

import javax.swing.*;

import com.sun.java.swing.SwingUtilities2;

import java.awt.event.*;
import java.awt.*;

import edu.indiana.cs.eac.testing.ui.*;
import ec.display.*;

public class JEAC2 extends JFrame
{
	private MDIDesktopPane desktop;

	private JMenuBar menuBar;

	private JMenu fileMenu;

	private JMenuItem newMenu;

	private JScrollPane scrollPane;

	public JEAC2() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Unable to load native look and feel.");
		}

		desktop = new MDIDesktopPane();
		desktop.setBackground(Color.GRAY);

		menuBar = new JMenuBar();

		// it don't work
		//		menuBar.putClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY, new Boolean(true));

		fileMenu = new JMenu("jEAC");
		JMenuItem newMenu = new JMenuItem("New");
		JScrollPane scrollPane = new JScrollPane();

		menuBar.add(fileMenu);

		JMenuItem ecj = new JMenuItem("ECJ");
		ecj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
//				desktop.add(new Console(new String[0]));
				
				Console c = new Console(new String[0]);
				c.setVisible(true);
				
			}});
		
		JMenuItem jeac = new JMenuItem("jEAC");
		jeac.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
//				desktop.add(new Console(new String[0]));
				JEAC j = new JEAC();
			}});
		
		JMenu tools = new JMenu("Tools");
		tools.add(ecj);
		tools.add(jeac);
		
		
		menuBar.add(tools);

		menuBar.add(new WindowMenu(desktop));
		fileMenu.add(newMenu);

		menuBar.add(new JMenu("Help"));

		setJMenuBar(menuBar);
		this.setLocation(new Point(50,50));
		setTitle("jEAC - An integrated EAC development suite");
		scrollPane.getViewport().add(desktop);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// this how we register with the MDI manager
		newMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				desktop.add(new TextFrame());
			}
		});

	}

	public static void main(String[] args)
	{
		JEAC2 jeac = new JEAC2();
		jeac.setSize(600, 400);
		jeac.setVisible(true);
	}

}