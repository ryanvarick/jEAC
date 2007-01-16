/*
 * TestComm.java
 * Description: Testing serial port communication using rxtx package
 * Change Log:
 *   3/31/2006:  Added LLA functions, changed getPortName() to
 *               throw Exception on port not being set
 */

package edu.indiana.cs.eac.testing.driver;

import gnu.io.*;
import java.util.*;
import java.io.*;

public class TestComm 
{
    static SerialController controller;
    static boolean okReached;
   

    public static void main(String[] args)
    {
	controller = new SerialController();
	boolean isOpen = controller.openPort("COM5", 19200);
	String portName;
	try {
	    portName = controller.getPortName();
		System.out.println("Status of " + controller.getPortName() + isOpen);
	} catch (Exception e) {
	    System.out.println(e.getMessage());
	}
	try{
	    Thread.sleep(200);
	}
	catch (InterruptedException e)
	    {
		System.err.println("Interrupted in Main()");
	    }
//	double[][] numbers = controller.readAll();
//	System.out.println("readAll:");
//	for(int i = 0; i < numbers.length; i++) {
//	    for(int j = 0; j < numbers[i].length; j++) {
//		System.out.print(numbers[i][j] + " ");
//	    }
//	    System.out.println();
//	}
//	try {
//	    Thread.sleep(10);
//	} catch (InterruptedException e) {
//	}
	boolean writeOk = controller.setCurrent(2, 3, 100);
	System.out.println("SetCurrent:");
	System.out.println(writeOk);
	try {
	    Thread.sleep(10);
	} catch (InterruptedException e) {
	}
	double currentNumber = controller.readCurrent(2, 2);
	System.out.println("readCurrent:");
	System.out.println(currentNumber);
	try {
	    Thread.sleep(10);
	} catch (InterruptedException e) {
	}
	double voltNumber = controller.readVoltage(2, 2);
	System.out.println("readVoltage:");
	System.out.println(voltNumber);
	controller.sendReset();
	System.out.println("readVoltage:" + controller.readVoltage(2, 2));
    }
}

class SerialController implements SerialPortEventListener
{
    private SerialPort port = null;
    private InputStream input;
    private OutputStream output;
    public static boolean okFound = false;
    private String dataString = null;

    /* listPorts()
     * input: null
     * output: String
     * Description: Lists available ports that RXTX knows about 
     */
    
    public String listPorts() {
        Enumeration ports = CommPortIdentifier.getPortIdentifiers();
        StringBuffer buffer = new StringBuffer();
        while (ports.hasMoreElements()) {
            CommPortIdentifier port = (CommPortIdentifier)ports.nextElement();
            buffer.append(port.getName() + "\n");
        }
        return buffer.toString();
    }
    
    /* openPort()
     * input: String portName, int baudRate
     * output: boolean
     * Description: Take a portName and baudRate and attempts to open a port.
     * 		    Returns true if successful, false otherwise
     */

    public boolean openPort(String portName, int baudRate)
    {
	try
	    {
		CommPortIdentifier portID = CommPortIdentifier.getPortIdentifier(portName);
		port = (SerialPort)portID.open("Serial Port" + portName, 2000);
		try
		    {
			port.setSerialPortParams(baudRate,
						 SerialPort.DATABITS_8,
						 SerialPort.STOPBITS_1,
						 SerialPort.PARITY_NONE);
			port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		    }
		catch (UnsupportedCommOperationException e)
		    {
			System.err.println("Error setting serial port parameters");
		    }
		try
		    {
			input = port.getInputStream();
			output = port.getOutputStream();
		    }
		catch (IOException e)
		    {
			System.err.println("Error setting up I/O streams");
		    }
		try
		    {
			port.addEventListener(this);
			port.notifyOnDataAvailable(true);
		    }
		catch (TooManyListenersException e)
		    {
			System.err.println("Error adding Listeners");
		    }
	    }
	catch (Exception e)
	    {
		System.err.println("Error on openPort(), serial port in use");
		return false;
	    }
	return true;
    }
    
    /*
     * closePort()
     * input: null
     * output: boolean
     * Description: Attempts to close an open serial port.  Returns true if
     * 		    successful, false otherwise
     */

    public boolean closePort()
    {
	if (port != null) {
	    try {
		input.close();
		output.close();
	    } catch (IOException e) {
		System.err.println("Error closing input and output streams");
	    }
	    port.close();
	    port = null;
	}
	return true;
    }
    
    /*
     * serialEvent()
     * input: SerialPortEvent event
     * output: null
     * Description: Is called anytime there is communication from the serial
     *		    port.  Deals with that input accordingly
     */

    public void serialEvent(SerialPortEvent event)
    {
	dataString = null;
	okFound = false;
	String readString = "";
	byte[] buffer = new byte[10000000];
	int bufferSize = 0;
	if (event.getEventType() != SerialPortEvent.DATA_AVAILABLE)
	    {
		return;
	    }
	try
	    {
		while(input.available() > 0){
		    int amountToBeRead = input.available();
		    if (amountToBeRead > buffer.length) 
			amountToBeRead = buffer.length;
		    bufferSize = input.read(buffer, 0, amountToBeRead);
		    try {
			Thread.sleep(20);
		    } catch (InterruptedException e) {
			System.err.println("Someone woke up SerialEvent, now he's killing everyone");
		    }
		}
		if (readString == null) {
		    readString = new String(buffer, 0, bufferSize);
		} else {
		    readString = readString.concat(new String(buffer, 0, bufferSize));
		}
	    }
	catch (IOException e)
	    {
		System.err.println("Error in serialEvent");
		System.err.println(e);
		port.close();
	    }
	if (readString.lastIndexOf('>') == (bufferSize - 2)) {
	    okFound = true;
	    dataString = readString.substring(0, (bufferSize - 7));
	}
	return;
    }
    
    /* send()
     * input: char outputByte
     * output: null
     * Description:
     */

    public void send(char outputByte)
    {
	try
	    {
		output.write(outputByte);
		output.flush();
	    }
	catch (IOException e)
	    {
		System.err.println("Error in send(), could not send byte");
	    }
    }
    
    public void send(String message)
    {
	message = message + "\n";
	char[] bytes = message.toCharArray();
	try
	    {
		
		for(int i = 0; i<bytes.length; i++) 
		    {
			this.send(bytes[i]);
		    }
		output.flush();
	    }
	catch (IOException e)
	    {
		System.err.println("Error sending String");
	    }
    }

    /*
     * getPortName()
     * input: null
     * output: String portName
     * Description: Returns the portName that has been setup in openPort()
     */
     
    public String getPortName() throws Exception {
	if (port == null) {
	    throw new Exception("No port has been open");
	} else {
	    return port.getName();
	}
    }
    
    /*
     * readAll()
     * input: null
     * output: double[][] grid
     * Description: Returns the grid of voltages from the sheet
     */
    
    public double[][] readAll() {
	send("p,v");
	while(!okFound) {
	    //spin baby spin
	}
	//Ok found
	double [][] numbers = new double[5][5];
	StringTokenizer tokenizer = new StringTokenizer(dataString, ",");
	for (int i = 0; i < numbers.length; i++) {
	    for (int j = 0; j < numbers[i].length; j++) {
		numbers[i][j] = Double.parseDouble(tokenizer.nextToken());
	    }
	}
	dataString = null;
	okFound = false;
	return numbers;
    }
    
    /*
     * setCurrent()
     * input: int xLocation, int yLocation, int amount
     * output: boolean commandSuccess
     * Description: Sets the current at x,y to the amount, if successful
     * 		    returns true
     */
    
    public boolean setCurrent(int xLocation, int yLocation, int amount) {
	send("w,i," + xLocation + "," + yLocation + "," + amount);
	while (!okFound) {
	    // spin baby spin
	}
	okFound = false;
	dataString = null;
	return true;
    }
    
    /*
     * readCurrent()
     * input: int xLocation, int yLocation
     * output: int currentAmount
     * Description: Reads, and returns, the amount of current at x,y
     */
    
    public double readCurrent(int xLocation, int yLocation) {
	String sendString = "r,i," + xLocation + "," + yLocation + ",100";
	send(sendString);
	while(!okFound) {
	    //spin baby spin
	}
	StringTokenizer tokenizer = new StringTokenizer(dataString, ",");
	double returnNumber = Double.parseDouble(tokenizer.nextToken());
	dataString = null;
	okFound = false;
	return returnNumber;
    }
    
    /*
     * readVoltage()
     * input: int xLocation, int yLocation
     * output: int voltageAmount
     * Description: Reads, and returns, the amount of voltage at x,y
     */
    
    public double readVoltage(int xLocation, int yLocation) {
	String sendString = "r,v," + xLocation + "," + yLocation + ",100";
	send(sendString);
	while(!okFound) {
	    //spin baby spin
	}
	StringTokenizer tokenizer = new StringTokenizer(dataString, ",");
	double returnNumber = Double.parseDouble(tokenizer.nextToken());
	dataString = null;
	okFound = false;
	return returnNumber;
    }
    
    /*
     * sendReset()
     * input: null
     * output: String diagString
     * Description: Sends reset to uEAC resetting components
     */
     
    public String sendReset() {
	String sendString = "rst";
	send(sendString);
	while(!okFound) {
	    //spin baby spin
	}
	System.out.println(dataString);
	dataString = null;
	okFound = false;
	return dataString;
    }
     
    /*
     * addLLA()
     * input: int descriptor,
     *        int xInputLocation,
     *        int yInputLocation,
     *        int xOutputLocation,
     *        int yOutputLocation,
     *        int functionNumber,
     *        int refreshPeriod
     * output: boolean success
     * Description: Adds an LLA to the uEAC
     */
     
    public boolean addLLA(int descriptor,
			  int xInputLocation,
			  int yInputLocation,
			  int xOutputLocation,
			  int yOutputLocation,
			  int functionNumber,
			  int refreshPeriod) {
	String sendString = 
	    "L,A," + 
	    xInputLocation + "," +
	    yInputLocation + "," +
	    xOutputLocation + "," +
	    yOutputLocation + "," +
	    functionNumber + "," +
	    refreshPeriod;
	send(sendString);
	while(!okFound) {
	    // spin baby spin
	}
	dataString = null;
	okFound = false;
	return true;
    }

    /*
     * disableLLA()
     * input: int descriptor,
     *        int xInputLocation,
     *        int yInputLocation,
     *        int xOutputLocation,
     *        int yOutputLocation,
     *        int functionNumber,
     *        int refreshPeriod
     * output: boolean success
     * Description: Disables an already set LLA
     */

    public boolean disableLLA(int descriptor,
			      int xInputLocation,
			      int yInputLocation,
			      int xOutputLocation,
			      int yOutputLocation,
			      int functionNumber,
			      int refreshPeriod) {
	String sendString =
	    "L,D," + 
	    xInputLocation + "," +
	    yInputLocation + "," +
	    xOutputLocation + "," +
	    yOutputLocation + "," +
	    functionNumber + "," +
	    refreshPeriod;
	send(sendString);
	while(!okFound) {
	    // spin baby spin
	}
	dataString = null;
	okFound = false;
	return true;
    }

    /*
     * enableLLA()
     * input: int descriptor,
     *        int xInputLocation,
     *        int yInputLocation,
     *        int xOutputLocation,
     *        int yOutputLocation,
     *        int functionNumber,
     *        int refreshPeriod
     * output: boolean success
     * Description: Enables an already set LLA
     */

    public boolean enableLLA(int descriptor,
			      int xInputLocation,
			      int yInputLocation,
			      int xOutputLocation,
			      int yOutputLocation,
			      int functionNumber,
			      int refreshPeriod) {
	String sendString =
	    "L,E," + 
	    xInputLocation + "," +
	    yInputLocation + "," +
	    xOutputLocation + "," +
	    yOutputLocation + "," +
	    functionNumber + "," +
	    refreshPeriod;
	send(sendString);
	while(!okFound) {
	    // spin baby spin
	}
	dataString = null;
	okFound = false;
	return true;
    }

    /*
     * reportLLA()
     * input: int descriptor,
     *        int xInputLocation,
     *        int yInputLocation,
     *        int xOutputLocation,
     *        int yOutputLocation,
     *        int functionNumber,
     *        int refreshPeriod
     * output: boolean success
     * Description: Reports on an already set LLA
     */

    public boolean reportLLA(int descriptor,
			     int xInputLocation,
			     int yInputLocation,
			     int xOutputLocation,
			     int yOutputLocation,
			     int functionNumber,
			     int refreshPeriod) {
	String sendString =
	    "L,R," + 
	    xInputLocation + "," +
	    yInputLocation + "," +
	    xOutputLocation + "," +
	    yOutputLocation + "," +
	    functionNumber + "," +
	    refreshPeriod;
	send(sendString);
	while(!okFound) {
	    // spin baby spin
	}
	dataString = null;
	okFound = false;
	return true;
    }
}