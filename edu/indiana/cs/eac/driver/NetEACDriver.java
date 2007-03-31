package edu.indiana.cs.eac.driver;

import java.io.*;
import java.net.*;
import java.text.*;
import javax.swing.*;

import edu.indiana.cs.eac.*;
import edu.indiana.cs.eac.gradient.*;
import edu.indiana.cs.eac.ui.JEACNode;
import edu.indiana.cs.eac.ui.NodeMap;
import edu.indiana.cs.eac.exceptions.*;
import edu.indiana.cs.math.*;

public class NetEACDriver implements Device, Serializable
{
	private static final boolean DEBUG = false;
	
	// HACK: flags used by getAllLLA*Values
	public static final int LLA_IN_VALUE  = 100;
	public static final int LLA_OUT_VALUE = 200;
	
	private static final int SOURCE_OFFSET = 0;
	private static final int SINK_OFFSET = 8;
	private static final int SOURCE_CHANNELS = 8;
	private static final int SINK_CHANNELS = 8;
	private static final int LLA_CHANNELS = 6;
	private static final int MAP_ROWS = 13;
	private static final int REPORTING_ROWS = 7;
	private static final int MAP_COL = 7;
	private static final int REPORTING_COL = 5;
	private static final int EAC_PORT = 17000;
	private static final int READABLE_PINS = 35;
	public static final int LLA_IN = 0;
	public static final int LLA_OUT = 1;
	private static final int NUM_CONNECTION_TYPES = 4;
	private static final int NUM_OF_LLA_FUNCTIONS = 27;
	
	private static final int CURRENT_MIN = 0;
	private static final int CURRENT_MAX = 200;

	private NodeMap nodemap;
	private transient Socket sock;
	private String URL;
	private transient InputStream in;
	private transient OutputStream out;
	private double[][] sheetvalues;
	private JEACNode nodes[] = new JEACNode[MAP_ROWS * MAP_COL];
	private ChannelList channels;
	//private boolean connected = false;
	
	public NetEACDriver(String URL) {
		this.URL = URL;
		channels = new ChannelList();
	
		for (int i = 0; i < MAP_ROWS * MAP_COL; i++) {
			nodes[i] = new JEACNode();
		}
		
		nodemap = new NodeMap(this);
	}
	
	// API-mapped (added 4/6 -RV)
	public void reset() { 
		
		for (int i = SOURCE_OFFSET; i < SOURCE_CHANNELS; i++) {
			setBoardValue(0.0, i, JEACNode.SOURCE);
		}
		
		for (int i = SINK_OFFSET; i < SINK_CHANNELS + SINK_OFFSET; i++) {
			setBoardValue(0.0, i, JEACNode.SINK);
		}
		
		for (int i = 0; i < LLA_CHANNELS; i++) {
			System.out.println(i);
			setBoardValue(1.0, i, JEACNode.LLA);
		}
		
		channels = new ChannelList();
	}
	
	// API-mapped (added 3/28 -RV)
	public String getCurrentUnit() {
		return "uA";
	}
	
	public int getMaxCurrent() {
		return CURRENT_MAX;
	}
	
	public int getMinCurrent() {
		return CURRENT_MIN;
	}
	
	public int getNumLLAFunctions() {
		return NUM_OF_LLA_FUNCTIONS;
	}
	
	public int getNumConnectionTypes() {
		return NUM_CONNECTION_TYPES;
	}
	
	public int getNumRows() {
		return MAP_ROWS;
	}
	
	public int getNumReportingRows() {
		return REPORTING_ROWS;
	}
	
	public int getNumCols() {
		return MAP_COL;
	}
	
	public int getNumReportingCols() {
		return REPORTING_COL;
	}
	
	public String getDeviceName() {
		return URL;
	}
	
	public int getNumLLAs() {
		return LLA_CHANNELS;
	}
	
	public int[] getNodeIndex(JEACNode node) throws NodeNotFoundException{
		int temp[] = new int[2];
		
		for (int i = 0; i < nodes.length; i++)
			if (nodes[i] == node) {
				temp[0] = ((int) Math.floor(i/getNumRows())) + 1;
				temp[1] = (i%getNumRows()) + 1;
				return temp;
			}
		
		throw new NodeNotFoundException("getNodeIndex");
	}

	public JEACNode[] getJEACNodes() {
		return nodes;
	}
	
	public NodeMap getNodeMap() {
		return nodemap;
	}
	
	
	public String[] getAllLLAInputValues()
	{
		return getLLAValues(LLA_IN_VALUE);
	}
	
	public String[] getAllLLAOutputValues()
	{
		return getLLAValues(LLA_OUT_VALUE);
	}
	
	private String[] getLLAValues(int type)
	{
		// Translate the flag into an LLA offset
		int offset = -1;
		if(type == LLA_IN_VALUE) offset = 0;
		else if(type == LLA_OUT_VALUE) offset = 1;
		else
		{
			System.err.println("Illegal flag recieve in getLLAValues(): " + type);
		}
		
		int temp;
		double tempValue;
		String response;
		
		DecimalFormat formatter = new DecimalFormat("0.0");
		String[] values = new String[LLA_CHANNELS];
		for (int i = 0; i < LLA_CHANNELS; i++) {
			try {
    				response = writeSentence("A" + HexString.toHexString(40 + (2*i) + offset) + "000Z\n");
    				temp = HexString.toDecimal(response.substring(3, 6));
    				tempValue = (((double) temp) - 455) * 0.5405;
    				values[i] = formatter.format(tempValue);
    			} catch (IOException e) {
    				values[i] = "ERR"; 
	    		} catch (StringIndexOutOfBoundsException e) {
	    			values[i] = "ERR";
	    		} catch (NumberFormatException e) {
	    			values[i] = "ERR";
	    		} catch (InvalidCommandException e) {
	    			values[i] = "ERR";
	    			
	    			if (DEBUG) System.err.println(e.getMessage());
	    			JOptionPane.showMessageDialog(null, "The message\n" + e.getMessage() + "was sent to the machine.\nError code FxxxxxZ reported from the machine.\nPlease report this issue to the system developers.", "Incorrect Syntax", JOptionPane.ERROR_MESSAGE);
	    		}
	    	}
	    	
		return values;
	}
	
	public void connect() throws ConnectionException {
		//System.out.println("Grabbing connection");
		try {
			sock = new Socket(URL, EAC_PORT);
			
			in = sock.getInputStream();
			out = sock.getOutputStream();
		} catch (IOException e) {
			throw new ConnectionException("IOException");
		} 		
	}
	
	public void reload() throws ConnectionException {
		connect();
		
		if(DEBUG) System.out.println("Debugging reload method\n-----------------------");
		
		for (int i = 0; i < channels.sourceChannelUsed.length; i++) {
			try {
				setBoardValue(channels.sourceChannelUsed[i].getNode().getValue(), i + SOURCE_OFFSET, JEACNode.SOURCE);
				if(DEBUG) System.out.println("SOURCE: " + i + ", " + channels.sourceChannelUsed[i].getNode().getValue());
			} catch (ChannelAssociationException e) {
				// Do nothing
			}
		}
		
		for (int i = 0; i < channels.sinkChannelUsed.length; i++) {
			try {
				setBoardValue(channels.sinkChannelUsed[i].getNode().getValue(), i + SINK_OFFSET, JEACNode.SINK);
				if(DEBUG) System.out.println("SINK: " + i + ", " + channels.sinkChannelUsed[i].getNode().getValue());
			} catch (ChannelAssociationException e) {
				// Do nothing
			}
		}
		
		for (int i = 0; i < channels.llaChannelUsed.length; i++) {
			try {
				setBoardValue(channels.llaChannelUsed[i].getNode().getValue(), i, JEACNode.LLA);
				if(DEBUG) System.out.println("LLA: " + i + ", " + channels.llaChannelUsed[i].getNode().getValue());
			} catch (ChannelAssociationException e) {
				// Do nothing
			}
		}
	}
    
	public double[][] getVoltageGradient() throws IOException {
    		int rows = getNumReportingRows();
    		int cols = getNumReportingCols();
		String pinNumber, response, command;
		int temp;
		double tempValue;
		DecimalFormat formatter = new DecimalFormat("0.000");
		
		sheetvalues = new double[rows][cols];
		
		try {
			for (int i = 0; i < READABLE_PINS; i++) {
				pinNumber = HexString.toHexString(i);
			
			
				command = new String("A" + pinNumber + "000Z" + "\n");
				response = writeSentence(command);
				temp = HexString.toDecimal(response.substring(3, 6));
				tempValue = (((double) temp) - 470) * 0.009765;
				String numberString = formatter.format(tempValue);
			
				sheetvalues[(int) Math.floor(i/cols)][i%cols] = Double.parseDouble(numberString);
			}
		} catch (InvalidCommandException e) {
			if (DEBUG) System.err.println(e.getMessage());
			JOptionPane.showMessageDialog(null, "The message\n" + e.getMessage() + "was sent to the machine.\nError code FxxxxxZ reported from the machine.\nPlease report this issue to the system developers.", "Incorrect Syntax", JOptionPane.ERROR_MESSAGE);
		}
		
		sheetvalues = GradientInterpolator.mirrorMatrixHorizontally(sheetvalues);
		
		return sheetvalues;
    }
	
	public boolean changeNode(String type, double value, JEACNode node) {
		boolean success = false;
		
		// Four cases:
		// 	OFF -> Some ON state
		// 	Some ON state -> Different ON state
		// 	Some ON state -> OFF
		//	Change node value
		if (node.isType(JEACNode.OFF)) {
			// To handle case 1
			try {
				channels.assignChannel(node, type);
				success = true;
			} catch (NoChannelFoundException e) {
				JOptionPane.showMessageDialog(null, "No " + type + " Channels Open", "NO OPEN CHANNELS", JOptionPane.ERROR_MESSAGE);
				success = false;
			} catch (ChannelAssociationException e) {	
				success = false;
			}
		} else if (!node.isType(type)) { 
			// Handles cases 2 and 3
			//System.out.println("Changing node type");
			try {
				success = channels.changeAssignedChannel(type, node);
			} catch (ChannelAssociationException e) {
				success = false;
			} catch (NoChannelFoundException e) {
				JOptionPane.showMessageDialog(null, "No " + type + " Channels Open", "NO OPEN CHANNELS", JOptionPane.ERROR_MESSAGE);
				success = false;
			}
		} else if (node.getValue() != value) {
			//System.out.println("Changing node value");
			// This is to take care of the last case
			
			success = channels.changeChannelValue(node, value);
		}
		
		return success;
    }
	
	/**
	 * Sets a pin.
	 * 
	 * @param double value - New pin value
	 * @param int index - Channel index
	 * @param String type - Pin type (JEACNode constant)
	 * @return boolean status
	 */
	public boolean setBoardValue(double value, int index, String type) {
		String command, hexString, indexString;
		
		if (type.equals(JEACNode.LLA)) {
			command = "L";
		} else {
			command = "D";
		}
		
		indexString = HexString.toHexString(index);
//		System.out.println(indexString);
		command = command + indexString;
		if (!type.equals(JEACNode.LLA)) {
			// modify the value accordingly to adjust from the 0-200 range we're given
			// to the 0.0-0.2 range we apparently need to use
			value = value/1000;
			
			// Bryce does this for some reason
			value *= 1023;
		}
		
		hexString = HexString.toHexString((int) value);
		
		while (hexString.length() < 3)
			hexString = '0' + hexString;
//		System.out.println(hexString);
		command = command + hexString + "Z\n";

		try {
			writeSentence(command);
		} catch (IOException e) {
			// Nothing here
		} catch (InvalidCommandException e) {
			if (DEBUG) System.err.println(e.getMessage());
			JOptionPane.showMessageDialog(null, "The message\n" + e.getMessage() + "was sent to the machine.\nError code FxxxxxZ reported from the machine.\nPlease report this issue to the system developers.", "Incorrect Syntax", JOptionPane.ERROR_MESSAGE);
		}
			
		return true;
	}
	
	public synchronized String writeSentence(String command) throws IOException, InvalidCommandException{

		if(DEBUG) System.out.println("Command: " + command);
		String response;
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		out.write(command.getBytes());
		out.flush();
		response = br.readLine();
		if(DEBUG) System.out.println("Response: " + response);
		
		if (!(response.charAt(0) == 'F'))
			return response;
		else throw new InvalidCommandException(command);
	}
	
	public void disconnect(){	
		try {
			in.close();
			out.close();
			sock.close();
		} catch (IOException e){
			// Do nothing, we don't care
		}
	}
	
	public static void main(String args[]) {
		String machine = "eac2.cs.indiana.edu";
		NetEACDriver test = new NetEACDriver(machine);
	}
	
	private class Channel implements Serializable{
		private boolean used;
		private JEACNode node;
		private String type;
		
		public Channel(String type) {
			this.type = type;
			used = false;
		}
		
		public boolean isUsed() {
			return used;
		}
		
		public JEACNode getNode() throws ChannelAssociationException{
			if (node != null)
				return node;
			throw new ChannelAssociationException("Channel not used");
		}
		
		public void assignNode(JEACNode node) throws ChannelAssociationException{
			int[] temp;
			
			if (!used) {
				this.node = node;
				
				try {
					temp = getNodeIndex(node);
					JOptionPane.showMessageDialog(null, "Wire node (" + temp[0] + "," + temp[1]+ ") to " + type + " channel " + channels.findBoardChannelIndex(node, this.type), "WIRE BOARD", JOptionPane.INFORMATION_MESSAGE);
					used = true;
				} catch (NodeNotFoundException e) {
					System.err.println("changeNode -> associateNode -> NodeNotFoundException");
				} catch (NoChannelFoundException e) {
					System.err.println("changeNode -> associateNode -> ChannelNotFoundException");
				}
			} else
				throw new ChannelAssociationException("Channel in use");
		}
		
		public void removeNode() throws ChannelAssociationException{
			int[] temp;
			
			if (!used || node == null)
				throw new ChannelAssociationException("Channel not used");

			try {
				temp = getNodeIndex(node);
				JOptionPane.showMessageDialog(null, "Remove wire from node (" + temp[0] + "," + temp[1]+ ") to " + type + " channel " + channels.findBoardChannelIndex(node, this.type), "WIRE BOARD", JOptionPane.INFORMATION_MESSAGE);
				used = false;			
			} catch (NodeNotFoundException e) {
				System.err.println("changeNode -> associateNode -> NodeNotFoundException");
			} catch (NoChannelFoundException e) {
				System.err.println("changeNode -> associateNode -> ChannelNotFoundException");
			}
			
			
		}
	}

	private class ChannelList implements Serializable{
		public Channel[] sourceChannelUsed, sinkChannelUsed, llaChannelUsed;
		
		public ChannelList() {
			sourceChannelUsed = new Channel[SOURCE_CHANNELS];
			sinkChannelUsed = new Channel[SINK_CHANNELS];
			llaChannelUsed = new Channel[LLA_CHANNELS];
			
			for (int i = 0; i < SOURCE_CHANNELS; i++)
				sourceChannelUsed[i] = new Channel(JEACNode.SOURCE);
			
			for (int i = 0; i < SINK_CHANNELS; i++)
				sinkChannelUsed[i] = new Channel(JEACNode.SINK);
			
			for (int i = 0; i < LLA_CHANNELS; i++)
				llaChannelUsed[i] = new Channel(JEACNode.LLA);
		}
		
		public void assignChannel(JEACNode node, String type) throws NoChannelFoundException, ChannelAssociationException{
			Channel openChannel;
			
			openChannel = findChannel(type);
			openChannel.assignNode(node);		
		}
		
		private Channel findChannel(String type) throws NoChannelFoundException{
			Channel[] temp;
			int i = 0;
			
			if (type.equals(JEACNode.SINK)) {
				temp = sinkChannelUsed;
			} else if (type.equals(JEACNode.SOURCE)) {
				temp = sourceChannelUsed;
			} else {  // User selected LLA
				temp = llaChannelUsed;
			} 
			
			do {
				if (!temp[i].isUsed())
					return temp[i];
				i++;
			} while(i < temp.length);	
			
			throw new NoChannelFoundException("No Open Channel Was Found");
		}
		
		private Channel findChannel(JEACNode node) throws NoChannelFoundException{
			Channel[] temp;
			int i = 0;
			
			
			if (node.isType(JEACNode.SINK)) {
				temp = sinkChannelUsed;
			} else if (node.isType(JEACNode.SOURCE)) {
				temp = sourceChannelUsed;
			} else {  // User selected LLA
				temp = llaChannelUsed;
			} 
			
			do {
				try {
					if (temp[i].getNode() == node)
						return temp[i];
					i++;
				} catch (ChannelAssociationException e) {
					// Do nothing.  It's fine.
				}
			} while(i < temp.length);	
			
			throw new NoChannelFoundException("Used Channel Wasn't Found");
		}
		
		private int findBoardChannelIndex(JEACNode node, String type) throws NoChannelFoundException{
			Channel[] temp;
			int i = 0;
			int offset = 0;
			if (type.equals(JEACNode.SINK)) {
				temp = sinkChannelUsed;
				offset = SINK_OFFSET;
			} else if (type.equals(JEACNode.SOURCE)) {
				temp = sourceChannelUsed;
				offset = SOURCE_OFFSET;
			} else {  // User selected LLA
				temp = llaChannelUsed;
			} 
			
			do {
				try {
					if (temp[i].getNode() == node)
						return i;
					i++;
				} catch (ChannelAssociationException e) {
					// Do nothing.  This is fine here.
				}
			} while(i < temp.length);	
			
			throw new NoChannelFoundException("Used Channel Wasn't Found");
		}
		
		private int findChannelIndex(JEACNode node, String type) throws NoChannelFoundException{
			Channel[] temp;
			int i = 0;
			int offset;
			if (type.equals(JEACNode.SINK)) {
				temp = sinkChannelUsed;
			} else if (type.equals(JEACNode.SOURCE)) {
				temp = sourceChannelUsed;
			} else {  // User selected LLA
				temp = llaChannelUsed;
			} 
			
			do {
				try {
					if (temp[i].getNode() == node)
						return i;
					i++;
				} catch (ChannelAssociationException e) {
					// Do nothing.  This is fine here.
				}
			} while(i < temp.length);	
			
			throw new NoChannelFoundException("Used Channel Wasn't Found");
		}
		
		public boolean changeAssignedChannel(String type, JEACNode node) throws ChannelAssociationException, NoChannelFoundException {
			Channel openChannel, nodeChannel;
			int[] temp;
			int index;
			String oldType = node.getType();
			
			// First we need to grab the channel index of the channel associated with the
			// node so we can reset it on the board later.
			
			// Then we grab the channel associated with the given node.
			
			// If either of those operations fail, return false
			
			index = findChannelIndex(node, oldType);
			nodeChannel = findChannel(node);
			
			// Now we need to determine what the state change is and act accordingly
			
			if (!type.equals(JEACNode.OFF)) {
				// 1. Find an open channel
				// 2. Remove the node from the old channel
				// 3. Assign the node to the new channel
				
				openChannel = findChannel(type);
				// TODO: Change removeNode and assignNode to return booleans based on if
				// the user accepts the operation or not.  Return false if it's canceled.
				nodeChannel.removeNode();
				openChannel.assignNode(node);
			} else {
				// 1. Remove the node from the old channel
				nodeChannel.removeNode();
			}
			
			resetChannel(index, oldType);
			return true;
		}
		
		public boolean changeChannelValue(JEACNode node, double value) {
			int nodeChannelIndex;
			
			try {
				nodeChannelIndex = findBoardChannelIndex(node, node.getType());
				setBoardValue(value, nodeChannelIndex, node.getType());
				
			} catch(NoChannelFoundException e) {
				return false;
			}
			
			return true;
		}
	}
	
	public boolean resetChannel(int index, String type) {
		if (type.equals(JEACNode.LLA))
			return setBoardValue(1, index, type);
		else
			return setBoardValue(0, index, type);
	}	
	
    /**
     * Return the list of valid devices for this driver class.
     * 
     * @return String[] - list of devices
     * 
     */
	public static String[] getDeviceList()
	{
		return new String[]
      		{
				"eac1.cs.indiana.edu", 
				"eac2.cs.indiana.edu", 
//				"eac3.cs.indiana.edu", 
				"eac4.cs.indiana.edu"
			};

	}
	
	public boolean isValid()
	{
		return true;
	}
}
