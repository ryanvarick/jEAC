package edu.indiana.cs.eac.driver;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.indiana.cs.eac.JEACNode;
import edu.indiana.cs.eac.NodeMap;
import edu.indiana.cs.eac.exceptions.*;

public class NullDriver implements HAL, Serializable {
	
	private static final int MAP_ROWS = 7;
	private static final int REPORTING_ROWS = 7;
	private static final int MAP_COL = 5;
	private static final int REPORTING_COL = 5;
	private static final int EAC_PORT = 17000;
	public static final int LLA_IN = 0;
	public static final int LLA_OUT = 1;

	private NodeMap nodemap;
	private Socket sock;
	private String name;
	private InputStream in;
	private OutputStream out;
	private double[][] sheetvalues = new double[REPORTING_ROWS][REPORTING_COL];
	private JEACNode nodes[] = new JEACNode[MAP_ROWS * MAP_COL];
	private String URL = "Test Driver";
	
	public NullDriver() {
		
		for (int i = 0; i < MAP_ROWS * MAP_COL; i++) {
			nodes[i] = new JEACNode();
		}
		
		nodemap = new NodeMap(this);
	}
	
	// API-mapped (added 4/6 - RV)
	public void reset() { }
	
	// API-mapped (added 3/28 -RV)
	public String getCurrentUnit()
	{
		return "uA";
	}
	
	public int getMaxCurrent() {
		return 200;
	}
	
	public int getMinCurrent() {
		return 0;
	}
	
	public int getNumLLAFunctions() {
		return 27;
	}
	
	public int getNumConnectionTypes() {
		return 4;
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
		return MAP_COL;
	}
	
	public String getDeviceName() {
		return URL;
	}

	public JEACNode[] getJEACNodes() {
		return nodes;
	}
	
	public NodeMap getNodeMap() {
		return nodemap;
	}
	
	public int getLLA_IN(){
		return 0;
	}
	
	public int getLLA_OUT(){
		return 1;
	}
	
	public void connect() throws ConnectionException
	{
	}
    
	public void reload() throws ConnectionException {
		
	}
	
	public double[][] getVoltageGradient() throws IOException{
    		
		return new double[][]{
			{0.957, 1.064, 1.416, 1.016, 0.986}, 
			{0.947, 0.947, 0.957, 0.996, 0.967}, 
			{0.850, 0.820, 0.908, 0.859, 0.859},
			{0.820, 0.791, 0.244, 0.752, 0.908}, 
			{0.850, 0.840, 0.752, 0.771, 0.879}, 
			{0.869, 0.928, 0.879, 0.840, 0.850}, 
			{0.947, 0.918, 1.103, 0.976, 0.967}, 
		};
    }
    
	public boolean changeNode(String type, double value, JEACNode node){
		return true;
    }
	
    public void disconnect(){	
    	
	}
    
    public int getNumLLAs() {
    		return 4;
    }
    
    public String[] getAllLLAInputValues()
    {
    	return new String[] {
    							"N/A",
    							"N/A",
    							"N/A",
    							"N/A"
    						};
    }
    public String[] getAllLLAOutputValues()
    {
    	return new String[] {
    							"N/A",
    							"N/A",
    							"N/A",
    							"N/A"
    						};
    }
    
    /**
     * Return the list of valid devices for this driver class.
     * 
     * @return String[] - list of devices
     * 
     */
    public static String[] getDeviceList()
    {
    	return new String[] { "Null driver" };

    }
}
