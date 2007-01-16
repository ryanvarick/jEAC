package edu.indiana.cs.eac.testing.driver;

import java.io.*;
import javax.swing.*;

public class SerializeTest {
	private static ObjectOutputStream output;
	private static ObjectInputStream input;
	
	public static void main (String args[]) {
		Tester temp = new Tester();
		openFile();
		
		try {
			temp.setYourMom(System.in.read() - 48);
			output.writeObject(temp);
			Tester temp2 = (Tester) input.readObject();
			System.out.println(temp2.getYourMom());
		} catch (IOException e) {
			System.err.println("Error writing/reading file.");
		} catch (ClassNotFoundException e) {
			System.err.println("Error, class not found.");
		}
	}
	
	public static void openFile() {
		JFileChooser fileChooser = new JFileChooser();
		
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		int result = fileChooser.showSaveDialog(null);
		
		if (result == JFileChooser.CANCEL_OPTION)
			return;
		
		File fileName = fileChooser.getSelectedFile();
		
		if (fileName == null || fileName.getName().equals(""))
			JOptionPane.showMessageDialog(null, "Invalid File Name", "Invalid File Name", JOptionPane.ERROR_MESSAGE);
		else
			try {
				output = new ObjectOutputStream(new FileOutputStream(fileName));
				input = new ObjectInputStream(new FileInputStream(fileName));
			} catch (IOException e) {
				System.err.println("Error opening file.");
			}
	}
	
	public static void closeFile() {
		try {
			if (output != null)
				output.close();
			if (input != null);
				input.close();
		} catch (IOException e) {
			System.err.println("Error closing file.");
			System.exit(1);
		}
	}

	private static class Tester implements Serializable{
		private int yourmom;
		
		public Tester() {
			
		}
		
		public void setYourMom(int num){
			yourmom = num;
		}
		
		public int getYourMom() {
			return yourmom;
		}
	}
}
