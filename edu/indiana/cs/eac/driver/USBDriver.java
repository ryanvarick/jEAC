/**
 * USBDriver.java - Driver for USB-based uEACs.
 * 
 * @version 1.0.0
 * 
 * @author Adam Miller
 * @author Ryan R. Varick
 * 
 * Copyright (C) 2006.  All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 * 
 */

package edu.indiana.cs.eac.driver;

import java.io.*;
import java.util.*;
import javax.swing.*;

import gnu.io.*;

import edu.indiana.cs.eac.*;
import edu.indiana.cs.eac.exceptions.*;
import edu.indiana.cs.eac.ui.JEACNode;
import edu.indiana.cs.eac.ui.LoadingFrame;
import edu.indiana.cs.eac.ui.NodeMap;

import javax.swing.*;

public class USBDriver implements Device, Serializable
{
	// print useful debugging information when enabled
	private static boolean DEBUG = false;
	
	// driver configuration
	private String portName;
	private int baudRate;
	
	// communication channels
	private transient SerialPort serialPort;
	private transient InputStream inputStream;
	private transient OutputStream outputStream;
	private transient String dataString;				// raw, ready-to-process reply
	public static boolean okFound;						// flow control
	
	// known-bad port strings -- as we test the uEAC on new architectures,
	//  we learn more about what valid USB ports look like.  Right now, we
	//  do not know enough to support known good ports; rather, we are enumerating
	//  ports that cause problems here.
	//
	//  NOTE: these entries are case-insenstive
	private static final String[] IGNORED_PORTS = new String[]
	    {
			"lpt",			// Windows: printer ports
			"modem",		// OSX/Linux: modem devices can cause problems
			"bluetooth",	// OSX: bluetooth devices cause all kinds of problems
			"cu",			// OSX: duplicates tty entries
	    };
	
	// driver parameters
	private static final int NUM_ROWS = 5;
	private static final int NUM_COLS = 5;
	
	private static final int NUM_CONNECTION_TYPES = 4;		// TODO: (DEF) support for LLA_SRC/LLA_SNK starts here
	
	private static final String CURRENT_UNIT = "uA";
	private static final int MIN_CURRENT     = 0;
	private static final int MAX_CURRENT     = 200;
	
	private static final int MAX_LLAS = 10;
	private static final int LLA_ARGS = 8;
	
	private static final int NUM_OF_LLA_FUNCTIONS = 27;		// TODO: (DEF) support for user-defined LLAs starts here
	
	private static final int TIMEOUT_MILLIS = 1000;
	
	// LLA bookkeeping
	private static final int INSUFFICIENT_LLAS = -1;
	private static final int INVALID_INDEX     = -1;
	private static final int NULL_VALUE        = -1;
	
	private int[][] llaArray;								// stores what the driver knows about the LLAs
	private static final int X_IN         = 0;
	private static final int Y_IN         = 1;
	private static final int X_OUT        = 2;
	private static final int Y_OUT        = 3;
	private static final int FUNCTION     = 4;
	private static final int REFRESH_RATE = 5;
	private static final int INPUT_VALUE  = 6;
	private static final int OUTPUT_VALUE = 7;
	
	// node allocation
	private JEACNode nodes[];
	private NodeMap nodemap;
	
	// LED state
	private boolean ledsEnabled;
	
	// cached copy of the gradient
	private double[][] lastGradient;


	
	/**
	 * Constructor - instantiates a new USBDriver.
	 * 
	 * @param String port - name of the port to connect to
	 * 
	 */
	public USBDriver(String portName)
	{		
		this.portName   = portName;
		this.baudRate   = 19200;
		
		// initialize the gradient cache
		lastGradient = getBlankVoltageGradient();
		
		// allocate JEACNodes
		nodes = new JEACNode[NUM_ROWS * NUM_COLS];
		for(int i = 0; i < NUM_ROWS * NUM_COLS; i++)
		{
			nodes[i] = new JEACNode();
		}
		
		// instantiate a NodeMap
		nodemap = new NodeMap(this);
		
		// LLA bookkeeping
		this.llaArray = new int[MAX_LLAS][LLA_ARGS];
		initLLAs();
		
		// according to the uEAC specs, LEDs are enabled by default
		this.ledsEnabled = true;
	}
	
	/**
	 * INTERNAL - Constructor - for internal use only.
	 *
	 */
	public USBDriver() { /* Do nothing */ }
	

	
	/**
	 * Debugging method.
	 * 
	 * @param String[] args - command line arguemnts are ignored
	 * 
	 */
	public static void main(String args[])
	{
		String[] drivers = USBDriver.getDeviceList();
		
		System.out.println("Connecting to: " + drivers[0]);
		USBDriver driver = new USBDriver(drivers[0]);
		
		try
		{
			driver.connect();
			driver.reset();
			driver.disconnect();
		}
		catch(Exception e)
		{
			System.out.println("Connection failed: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
 	/* ===============[ LAYER 0: LOW-LEVEL (internal) COMMUNICATION ]=============== */

	/**
	 * Writes a command to the uEAC.
	 * 
	 * @param String message - sentence to write
	 * @return boolean - status
	 * 
	 */
	private synchronized boolean writeSentence(String message)
	{
		return writeSentence(message, false);
	}

	/**
	 * (unsafe) Writes a command to the uEAC, bypassing normal timeout checking.
	 * 
	 * @param String message - sentence to write
	 * @param boolean waitForever - whether to bypass timeout checking or not
	 * @return boolean - status
	 * 
	 */
	private synchronized boolean writeSentence(String message, boolean waitForever)
	{
		// these two print statements are good for debugging
		if(DEBUG) { System.out.println("Writing: " + message); }
		
		message = message + "\n";
		char[] bytes = message.toCharArray();
		try
		{
			for(int i = 0; i < bytes.length; i++)
			{
				this.writeChar(bytes[i]);
			}
			outputStream.flush();
		}
		catch (IOException e)
		{
			System.err.println("Error sending sentence.");
		}		
		
		/*
		 * Here's our complicated little spinlock -- Normally, the event listener is
		 * assumed to prepare data for us in a certain time window (TIMEOUT_MILLIS).
		 * When waitForever is specified, however, we will wait an indefinte period of time
		 * for the command to complete.  Further, we DO NOT assume that the response is
		 * going to be well-formed, hence the conditional based on the content of dataString.
		 * 
		 */
		long start = System.currentTimeMillis();
		while((!okFound && System.currentTimeMillis() - start < TIMEOUT_MILLIS) || (waitForever && dataString == null))
		{
			// spin baby spin
		}
		
		// okFound should be true by now, or the command timed out
		if(!okFound)
		{
			if(DEBUG) { System.out.println("Command timed out."); }
			return false;
		}
		else 
		{
			if(DEBUG) {  System.out.println("Response: " + dataString); }
			
			okFound = false;
			return true;
		}
	}
	
	/**
	 * (internal only) Write an individual character to the uEAC.
	 *  Adam tells me that this is the reason life sucks... I believe him.
	 * 
	 * @param outputByte - character to write
	 * 
	 */
	private synchronized void writeChar(char outputByte)
	{
		try
		{
			outputStream.write(outputByte);
			outputStream.flush();
		}
		catch (IOException e)
		{
			System.err.println("Error sending byte.");
		}
	}
	
	
	
	
	
	
	
	/* ===============[ LAYER 1: CONNECT/DISCONNECT ]===============*/
	
	// API-mapped
	public void connect() throws ConnectionException
	{		
		try
		{
			CommPortIdentifier portID = CommPortIdentifier.getPortIdentifier(portName);
			serialPort = (SerialPort) portID.open("Serial Port" + portName, 2000);
			
			// set up communication parameters
			try
			{
				serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, 
						SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			}
			catch (UnsupportedCommOperationException e)
			{
				System.err.println("Error setting serial port parameters.");
				throw new IOException();
			}
			
			// create communication channels
			try
			{
				inputStream  = serialPort.getInputStream();
				outputStream = serialPort.getOutputStream();
			}
			catch (IOException e)
			{
				throw new ConnectionException("Error setting up I/O streams.");
			}
			
			// add communication listeners
			try
			{
				serialPort.addEventListener(new USBEventListener());
				serialPort.notifyOnDataAvailable(true);
			}
			catch (TooManyListenersException e)
			{
				throw new ConnectionException("Error adding event listeners.");
			}
		}
		catch (Exception e)
		{
			throw new ConnectionException("Error connecting; serial port in use.");
		}
	}

	// API-mapped
	public void disconnect()
	{
		if(serialPort != null)
		{
			try
			{
				inputStream.close();
				outputStream.close();
			}
			catch (IOException e)
			{
				System.err.println("Error closing I/O streams.");
			}
			serialPort.close();
			serialPort = null;
		}
		else 
		{
			System.err.println("Tried to disconnect a null serial port.");
		}
	}
	
	// API-mapped
	public void reload() throws ConnectionException
	{
		// de-allocate nodes
		for(int i = 0; i < nodes.length; i++)
		{
			try
			{
				nodes[i].setType(JEACNode.OFF);
				nodes[i].setValue(0.0);
			}
			catch(Exception e)
			{
				System.err.println("Could not reset node.");
			}
		}
		reset();
	}
	
	// API-mapped
    public void reset()
    {
    	// use the unsafe write method since RST takes a while
		writeSentence("RST", true);
		
		// BUGFIX: (1473311) reset LLA bookkeeping
		initLLAs();
    }
    
	
	
	
	
	
	
	/* ===============[ LAYER 2: BASIC uEAC INTERACTION ]=============== */
    
    /**
     * Returns a blank voltage gradient (all values = 0.0).  Useful when
     *  a driver call is not necessary or desired.
     * 
     * @return double[][] - blank voltage gradient
     * 
     */
    private double[][] getBlankVoltageGradient()
    {
    	double[][] numbers = new double[NUM_ROWS][NUM_COLS];
		for (int i = 0; i < numbers.length; i++)
		{
			for (int j = 0; j < numbers[i].length; j++)
			{
				numbers[i][j] = 0.0;
			}
		}
		return numbers;
    }

	// API-mapped
	public double[][] getVoltageGradient() throws IOException
	{
		double[][] numbers = new double[NUM_ROWS][NUM_COLS];
		
		if(writeSentence("p,v"))
		{	
			try
			{
				StringTokenizer tokenizer = new StringTokenizer(dataString, ",");
			
				for (int i = 0; i < numbers.length; i++)
				{
					for (int j = 0; j < numbers[i].length; j++)
					{
						String s = tokenizer.nextToken();
						numbers[j][i] = Double.parseDouble(s);		// BUGFIX: (1477024) reversed gradient representation
					}
				}
			}
			
			// if something happens, and we can't process the gradient, return the cached copy
			catch(Exception e)
			{
				System.err.println("Processing gradient failed, returning previous gradient.");
				numbers = lastGradient;
			}
		}
		else
		{
			System.err.println("Could not retrieve voltage gradient.");
		}
		
		// cache the gradient and return
		lastGradient = numbers;
		return numbers;
	}

	/**
	 * Write current to the EAC.
	 * 
	 * @param int y - x-coordinate of the channel to write
	 * @param int x - y-coordinate of the channel to write
	 * @param int amount - amount of current to write
	 * 		(postive for sources, negative for sinks)
	 * 
	 */
	public void setCurrent(int xLocation, int yLocation, int amount)
	{
		writeSentence("w,i," + xLocation + "," + yLocation + "," + amount);
	}

	/**
	 * Read current from the EAC.
	 * 
	 * @param int x - x-coordinate of the point to read
	 * @param int y - y-coordinate of the point to read
	 * 
	 * @return double - current at the point
	 * 
	 */
	public double readCurrent(int xLocation, int yLocation)
	{
		writeSentence("r,i," + xLocation + "," + yLocation + ",100");
		StringTokenizer tokenizer = new StringTokenizer(dataString, ",");
		double value = Double.parseDouble(tokenizer.nextToken());

		return value;
	}

	/**
	 * Read voltage from the EAC.
	 * 
	 * @param int x - x-coordinate of the point to read
	 * @param int y - y-coordiante of the point to read
	 * 
	 * @return double - voltage at the point
	 * 
	 */
	public double readVoltage(int xLocation, int yLocation)
	{
		writeSentence("r,v," + xLocation + "," + yLocation + ",100");
		StringTokenizer tokenizer = new StringTokenizer(dataString, ",");
		double value = Double.parseDouble(tokenizer.nextToken());
		return value;
	}
	
	/**
	 * Toggle the LED display.  If the LEDs are off, they will be turned on;
	 *  otherwise, they will be disabled.
	 *  
	 *  TODO: This should probably be setLEDStatus(boolean), or something...
	 * 
	 */
	public void toggleLEDs()
	{
		String sentence = "";
		if(ledsEnabled) sentence = "LOF";
		else sentence = "LON";
	
		boolean success = writeSentence(sentence);
		
		if(success) ledsEnabled = !ledsEnabled;
		else
		{
			JOptionPane.showMessageDialog(null, "Could not toggle uEAC LEDs; command \"" + sentence + "\"failed.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Returns the status of the LED display.
	 * 
	 * @return boolean status - true = on, false = off
	 * 
	 */
	public boolean getLEDStatus()
	{
		return ledsEnabled;
	}
	
	/**
	 * Toggle debug state.  When enabled, driver debug information (mainly the I/O command trace)
	 *  will be sent to stdout.
	 *  
	 *  @param boolean - debug status
	 *  
	 */
	public static void setDebug(boolean status)
	{
		DEBUG = status;
	}
	
	/**
	 * Returns whether debugging is enabled or disabled
	 * 
	 * @return boolean - debug status
	 * 
	 */
	public static boolean getDebug()
	{
		return DEBUG;
	}
	
	
	
	/* --------------------[ LLA HARDWARE CONTROL (API) ]-------------------- */
	
	//
	// NOTE: These methods manage LLAs in hardware.
	//
	
	/**
	 * Adds an LLA to the uEAC.
	 * 
     * @param int x_in - input coordinates
     * @param int y_in
     * @param int x_out - output coordinates (0,0 for none)
     * @param int y_out
     * @param int function - LLA function (1-27)
     * @param int refresh - refresh rate (1-255)
     *
     * @return boolean - status
	 * 
	 */
	public boolean addLLA(int x_in, int y_in, int x_out, int y_out, int function, int refresh)
    {
		// try to allocate an LLA
		int id = allocateLLA();
		if(id == INSUFFICIENT_LLAS)
		{
			// BUGFIX: (1476439) No feedback when allocation fails
			JOptionPane.showMessageDialog(null, "Insufficient LLAs: the uEAC supports a maximum of 10 LLAs.", "Insufficient LLAs", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		// map to hardware, convert the 0-based function to 1-based
		mapLLA(id, x_in, y_in, x_out, y_out, function + 1, refresh);
				
		return writeLLACommand(id, 'A');
    }

    /**
     * Enables an existing LLA on the uEAC.
     *
     * @param int id - LLA to enable
     * 
     * @return boolean - status
     * 
     */
    public boolean enableLLA(int id)
    {
		return writeLLACommand(id, 'E');
    }    

	/**
	 * Disables an existing LLA on the uEAC.
	 *
	 * @param int id - LLA to disable
	 * 
	 * @return boolean - status
	 * 
	 */
    public boolean disableLLA(int id)
    {
    	// first remove from hardware, then de-allocate
		boolean status = writeLLACommand(id, 'D');
    	freeLLA(id);
    	return status;
    }

    /**
     * Reads the values of an existing LLA on the uEAC.
     * 
     * @param int id - LLA to report
     * 
     * @return int[] values - LLA values
     * 
     */
    public int[] reportLLA(int id)
    {
    	boolean status = writeLLACommand(id, 'R');
    	
    	String[] pieces =
    		{
    			"-1", "-1", "NOK"
    		};
    	
    	// the results are stored in the dataString, split on commas
    	//  and convert the first two values (third is OK)
    	try
    	{
    		pieces = dataString.split(",");
    	}
    	catch(NullPointerException e)
    	{
    		System.err.println("USBDriver caught a null pointer.  Returning dummy data.");
    	}
    	int values[] = new int[2];
    	values[0] = Integer.parseInt(pieces[0]);
    	values[1] = Integer.parseInt(pieces[1]);
    	return values;
    }
    
    /**
     * (internal) Reads each active LLA and stores the results in <code>llaArray</code>.
     * 
     */
    private void readAllLLAs()
    {
    	for(int i = 0; i < llaArray.length; i++)
    	{
    		if(llaArray[i][X_IN] != NULL_VALUE)
    		{
    			int lla_values[] = reportLLA(i);
    			llaArray[i][INPUT_VALUE]  = lla_values[0];
    			llaArray[i][OUTPUT_VALUE] = lla_values[1];
    		}
    	}
    }
    
    /**
     * DON'T USE!!
     * 
     * Need a quick and dirty way to reset all the LLAs.
     * 
     * @author Ryan R. Varick
     * @since 2.0
     *
     */
    public void resetAllLLAs()
    {
    	for(int i = 0; i < llaArray.length; i++)
    	{
    		if(llaArray[i][X_IN] != NULL_VALUE)
    		{
    			disableLLA(i);
    		}
    	}
    }
    
    /**
     * (internal) Writes an LLA command to the uEAC. This method reads
     *  the (assumed to be set) parameters from <code>llaArray</code>.
     * 
     * @param int id - LLA to write
     * @param char instruction - command to issue (one of: A,D,E,R)
     * 
     * @return boolean - status
     * 
     * @see http://www.cs.indiana.edu/~bhimebau/ueac/uEACos/doc/interpreter_doc/index.html
     * 
     */
    private boolean writeLLACommand(int id, char instruction)
    {
    	int params[] = getLLAParams(id);
    	String command = "L," + instruction + "," +
    		params[0] + "," + params[1] + "," +
    		params[2] + "," + params[3] + "," +
    		(id + 1) + "," +							// correct 0-based ID to 1-based
    		params[4] + "," + params[5];
    	return writeSentence(command);
    }
    
    /* --------------------[ LLA HARDWARE CONTROL (Internal) ]------------------- */
    
    //
    // NOTE: These methods are responsible for keeping track of LLAs on the driver side.
    //
    
    //
    // NOTE2: I don't know what I was thinking when I came up with this crap.
    //        Basically, there are three levels at work here:
    //
    //         1) USER/CLIENT - add_, enable_, disablle_ LLAs 
    //         2) BOOK KEEPING - allocate, free, init, disable, map
    //         3) HARDWARE - write
    //
    // I'll clean this up next time around for v2.0.
    //
 
    /**
     * Allocates a new LLA.
     * 
     * @return int id - new, 0-based LLA identifier; 
     * 		or <code>INSUFFICIENT_LLAS</code> if allocation fails 
     * 
     */
    private int allocateLLA()
    {
    	// look for the first open slot
    	for(int i = 0; i < llaArray.length; i++)
    	{
    		if(llaArray[i][X_IN] == NULL_VALUE && llaArray[i][Y_IN] == NULL_VALUE) return i;
    	}
    	return INSUFFICIENT_LLAS;
    }
    
    /**
     * Un-maps and frees (de-allocates) an existing LLA.
     * 
     * @param int id - LLA to de-allocate
     * 
     */
    private void freeLLA(int id)
    {
    	llaArray[id][X_IN]         = NULL_VALUE;
    	llaArray[id][Y_IN]         = NULL_VALUE;
    	llaArray[id][X_OUT]        = NULL_VALUE;
    	llaArray[id][Y_OUT]        = NULL_VALUE;
    	llaArray[id][FUNCTION]     = NULL_VALUE;
    	llaArray[id][REFRESH_RATE] = NULL_VALUE;
    	llaArray[id][INPUT_VALUE]  = NULL_VALUE;
    	llaArray[id][OUTPUT_VALUE] = NULL_VALUE;	
    }

    /**
     * Returns the identifier for the LLA at the given index.
     * 
     * @param int x - coordinates of the LLA
     * @param int y
     * 
     * @return int id - LLA identifier; 
     * 		or <code>INVALID_INDEX</code> if the index was not found
     * 
     */
    public int getLLAIndex(int x, int y)
    {
    	for(int i = 0; i < llaArray.length; i++)
    	{
    		if(llaArray[i][X_IN] == x && llaArray[i][Y_IN] == y) return i;
    	}
    	return INVALID_INDEX;
    }
    
    /**
     * Returns the driver-stored paramaters of an existing LLA.
     * 
     * @param int id - LLA to lookup
     * 
     * @return int[] params - current LLA parameters: 
     * 		x_in, y_in, x_out, y_out, function, refresh rate, input, output
     * 
     */
    private int[] getLLAParams(int id)
    {
    	return llaArray[id];
    }
    
    /**
     * Frees (de-allocates) all LLAs.
     *
     */
    private void initLLAs()
    {
    	for(int i = 0; i < llaArray.length; i++)
    	{
    		freeLLA(i);
    	}
    }
    
    /**
     * Records an LLA as in use.
     * 
     * @param int id - LLA to map to hardware
     * @param int x_in - input coordinates
     * @param int y_in
     * @param int x_out - output coordinates (0,0 for none)
     * @param int y_out
     * @param int function - LLA function (1-27)
     * @param int refresh - refresh rate (1-255)
     * 
     */
    private void mapLLA(int id, int x_in, int y_in, int x_out, int y_out, int function, int refresh)
    {
    	llaArray[id][X_IN]         = x_in;
    	llaArray[id][Y_IN]         = y_in;
    	llaArray[id][X_OUT]        = x_out;
    	llaArray[id][Y_OUT]        = y_out;
    	llaArray[id][FUNCTION]     = function;
    	llaArray[id][REFRESH_RATE] = refresh;
    }
    
	
	
	
	
	
	
    /* ====================[ LAYER 3: GET METHODS ]==================== */

	// API-mapped
	public String getDeviceName()
	{
		// serialPort.getName();
		return this.portName;
	}

	// API-mapped
	public int getNumRows()
	{
		return NUM_ROWS;
	}

	// API-mapped
	public int getNumReportingRows()
	{
		return this.getNumRows();
	}

	// API-mapped
	public int getNumCols()
	{
		return NUM_COLS;
	}

	// API-mapped
	public int getNumReportingCols()
	{
		return this.getNumCols();
	}
	
	// API-mapped
	public String getCurrentUnit()
	{
		return CURRENT_UNIT;
	}

	// API-mapped
	public int getMaxCurrent()
	{
		return MAX_CURRENT;
	}

	// API-mapped
	public int getMinCurrent()
	{
		return MIN_CURRENT;
	}

	// API-mapped
	public int getNumLLAFunctions()
	{
		return NUM_OF_LLA_FUNCTIONS;
	}

	// API-mapped
	public int getNumConnectionTypes()
	{
		return NUM_CONNECTION_TYPES;
	}

	// API-mapped
	public int getNumLLAs()
	{
//		int count = 0;
//		for(int i = 0; i < llaArray.length; i++)
//		{
//			if(llaArray[i][X_IN] != NULL_VALUE && llaArray[i][Y_IN] != NULL_VALUE) count++;
//		}
//		return count;
		
		return MAX_LLAS; 
	}
	
	// API-mapped
	public String[] getAllLLAInputValues()
	{
		String values[] = new String[getNumLLAs()];
		
		//
		// HACK: LLA reporting is one command.  We don't want to duplicate
		//       effort, and we know that getAllInput/getAllOutput methods are
		//       called sequentially, so we'll go ahead and read here.
		//       getAllOutput will read cached values.
		//
		readAllLLAs();
		
		int count = 0;
		for(int i = 0; i < llaArray.length; i++)
		{
			if(llaArray[i][INPUT_VALUE] != NULL_VALUE)
			{
				int in = Math.abs(llaArray[i][INPUT_VALUE]);
				values[count++] = Integer.toString(in);
			}
		}
		return values;
	}
	
	// API-mapped
	public String[] getAllLLAOutputValues()
	{
		String values[] = new String[getNumLLAs()];

		int count = 0;
		for(int i = 0; i < llaArray.length; i++)
		{
			if(llaArray[i][OUTPUT_VALUE] != NULL_VALUE)
			{
				int out = Math.abs(llaArray[i][OUTPUT_VALUE]);
				values[count++] = Integer.toString(out);
			}
		}
		return values;
	}
	
	

	/* ----------------[ JEAC-specifc Interaction ]--------------- */
	
	// API-mapped
	public JEACNode[] getJEACNodes()
	{
		return this.nodes;
	}

	// API-mapped
	public NodeMap getNodeMap()
	{
		return this.nodemap;
	}
	
	// API-mapped
	public boolean changeNode(String newType, double newValue, JEACNode node)
	{
		int index = getNodeIndex(node);
		int coords[] = getNodeCoordinates(index);

		// reset the node off when (1) OFF is request or (2) the type changes
		if(newType.equals(JEACNode.OFF) || !node.getType().equals(newType))
		{
			resetNode(node);
		}
		
		// set or update a source
		if(newType.equals(JEACNode.SOURCE))
		{
			setCurrent(coords[0], coords[1], (int)newValue);
		}
		
		// set or update a sink
		else if(newType.equals(JEACNode.SINK))
		{
			setCurrent(coords[0], coords[1], -1 * (int)newValue);
		}
		
		// set or update an LLA (also BUGFIX for 1473313)
		else if(newType.equals(JEACNode.LLA))
		{
			// BUGFIX: (1473306) look for an existing LLA at this point; if it exists, disable it
			int id = getLLAIndex(coords[0], coords[1]);
			if(id != USBDriver.INVALID_INDEX)
			{
				disableLLA(id);
			}

			addLLA(coords[0], coords[1], 0, 0, (int)newValue, 1);
			enableLLA(getLLAIndex(coords[0], coords[1]));
		}

		// now let's save things locally
		try
		{
			nodes[index].setType(newType);
			nodes[index].setValue(newValue);
		}
		catch(Exception e)
		{
			System.err.println("Driver error: could not update node[" + index + "].");
		}
		
		return true;
	}
	
	/**
	 * Resets a node to the OFF state.
	 * 
	 * @param node - node to reset
	 * 
	 */
	private void resetNode(JEACNode node)
	{
		int index = getNodeIndex(node);
		int coords[] = getNodeCoordinates(index);
		
		if(node.isType(JEACNode.OFF))
		{
			/* Do nothing, already off. */
		}

		// reset sources and sinks to 0 uA
		else if(node.isType(JEACNode.SOURCE) || node.isType(JEACNode.SINK))
		{
			setCurrent(coords[0], coords[1], 0);
		}
		else if(node.isType(JEACNode.LLA))
		{
			disableLLA(getLLAIndex(coords[0], coords[1]));
		}
		else
		{
			System.err.println("Illegal node type: " + node.getType());
		}

		// HACK: We need to set the node's value to 0 here to erase the value
		//		 (I call this a HACK because we should only be reading from nodes
		//		  here, not setting their values)
		node.setValue(0);
	}
	
	/**
	 * Given a node, returns its index in the node array.
	 * 
	 * @param node - node to find
	 * @return int - index of the node (-1 if the node does not exist)
	 */
	private int getNodeIndex(JEACNode node)
	{
		 for(int i = 0; i < nodes.length; i++)
		 {
		      if(node == nodes[i])
		      {
		           return i;
		      }
		 }
		 
		 return -1;
	}

	/**
	 * Given an index into the node array, returns its X-Y mapping.
	 * 
	 * @param index - array index (0-based)
	 * @return int[] - X-Y coordinates (1-based)
	 * 
	 */
	private int[] getNodeCoordinates(int index)
	{
		// BUGFIX: (1477024) reversed the coordinates
		return new int[]
		   {
				index % getNumCols() + 1,
				(int)Math.floor(index / getNumCols()) + 1,
		    };
	}


	
	

	
	
	/* ====================[ UTILITY METHODS ]==================== */
	
    /**
     * Return the list of valid devices for this driver class.
     * 
     * @return String[] - list of devices
     * 
     */
	public static String[] getDeviceList()
	{
		Object[] ports    = USBDriver.getPortList();
		String[] portList = new String[ports.length];
		
		for(int i = 0; i < ports.length; i++)
		{
			portList[i] = ((CommPortIdentifier)ports[i]).getName();
		}

		// OSX on Mills' iBook *only*
//		String[] portList = new String[]
//		    {
//				"/dev/tty.usbserial-191A"
//		    };
		
		return portList;

	}
	
	public static int getNumPorts()
	{
		Enumeration ports   = CommPortIdentifier.getPortIdentifiers();
//		return ports.size();
		return 5;
	}
	
	/**
	 * Lists available ports that RXTX knows about.
	 * 
	 * @return Object[] - list of know ports
	 * 
	 */
	public static Object[] getPortList()
	{
		// retrive the list of known COMM ports
		Enumeration ports   = CommPortIdentifier.getPortIdentifiers();
		
		// now look for at each port
		Vector validPorts = new Vector();
		while(ports.hasMoreElements())
		{
			CommPortIdentifier port = (CommPortIdentifier) ports.nextElement();
			
			// check for ignored ports
			boolean try_port = true;
			for(int i = 0; i < IGNORED_PORTS.length; i++)
			{
				if(port.getName().toLowerCase().contains(IGNORED_PORTS[i].toLowerCase())) 
				{
					if(DEBUG) System.out.println("\n" + port.getName() + " known bad, ignoring...");					
					try_port = false;
				}
			}
			
			// now probe the port
			if(try_port)
			{
				if(DEBUG) System.out.println("\nProbing " + port.getName() + "...");
	
				// try to connect, then probe the driver
				boolean isValid  = false;
				try
				{
					USBDriver driver = new USBDriver(port.getName());
					driver.connect();
					if(driver.writeSentence("NOK_TEST")) { isValid = true; }
					driver.disconnect();
				}
				catch(Exception e) { /* Do nothing. */ }
				
				if(isValid) 
				{ 
					validPorts.add(port);
					if(DEBUG) System.out.println(port.getName() + " is valid.");
				}
				else
				{
					if(DEBUG) System.out.println(port.getName() + " is not valid.");				
				}
			}
		}
		
		return validPorts.toArray();
	}
	
	/**
	 * Sleep for the given amount number of milliseconds.  I noticed a lot
	 *  of sleep statements, so I packaged them into their own static method.
	 * 
	 * @param int - time to sleep, in milliseconds
	 * 
	 */
	private static void sleep(int time)
	{
		try
		{
			Thread.sleep(time);
		}
		catch (InterruptedException e)
		{
			System.err.println("Someone interrupted sleep.");
		}		
	}



	
	
	
	
	/* ====================[ LISTENERS ]==================== */
	
	/**
	 * Listens for communication from the serial port.
	 * 
	 */
	private class USBEventListener implements SerialPortEventListener
	{
		public void serialEvent(SerialPortEvent event)
		{
			dataString        = null;
			okFound           = false;		
			String readString = "";
			byte[] buffer     = new byte[10000000];
			int bufferSize    = 0;
			
			// only respond when data-available events
			if(event.getEventType() != SerialPortEvent.DATA_AVAILABLE)
			{
				return;
			}
			
			try
			{
				while(inputStream.available() > 0)
				{
					int amountToBeRead = inputStream.available();
					if(amountToBeRead > buffer.length)
					{
						amountToBeRead = buffer.length;
					}
					
					bufferSize = inputStream.read(buffer, 0, amountToBeRead);
					USBDriver.sleep(20);
				}
				if(readString == null)
				{
					readString = new String(buffer, 0, bufferSize);
				}
				else
				{
					readString = readString.concat(new String(buffer, 0, bufferSize));
				}
			}
			catch (IOException e)
			{
				System.err.println("Error in USB event, closing down shop.");
				System.err.println(e);
				serialPort.close();
			}
			if(readString.lastIndexOf('>') == (bufferSize - 2))
			{
				okFound = true;
				dataString = readString.substring(0, (bufferSize - 7));
			}
		}
	}
	
	public boolean isValid()
	{
		return true;
	}
}
