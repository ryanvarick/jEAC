//based on http://www.javaworld.com/javaworld/jw-05-2001/jw-0525-mdi.html

package edu.indiana.cs.eac.testing.ui;

import javax.swing.*;

//import com.sun.java.swing.SwingUtilities2;

import edu.indiana.cs.eac.ui.WindowMenu;

import java.awt.event.*;
import java.awt.*;

public class Notepad extends JFrame {
	private MDIDesktopPane desktop;

	private JMenuBar menuBar;

	private JMenu fileMenu;

	private JMenuItem newMenu;

	private JScrollPane scrollPane;

	public Notepad() {

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

		menuBar.add(new JMenu("Tools"));

		menuBar.add(new WindowMenu(desktop));
		fileMenu.add(newMenu);

		menuBar.add(new JMenu("Help"));

		setJMenuBar(menuBar);
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

	public static void main(String[] args) {
		Notepad notepad = new Notepad();
		notepad.setSize(600, 400);
		notepad.setVisible(true);
	}

}