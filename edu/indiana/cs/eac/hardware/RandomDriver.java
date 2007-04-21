/*
 * This file is part of jEAC (http://jeac.sf.net/).
 * 
 * Copyright (C) 2007.  All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 * 
 */

package edu.indiana.cs.eac.hardware;

import java.io.*;
import java.util.*;
import java.net.*;

// TODO: Remove superfluous references
import edu.indiana.cs.eac.exceptions.*;
import edu.indiana.cs.eac.ui.JEACNode;
import edu.indiana.cs.eac.ui.NodeMap;

public class RandomDriver extends Driver
{

//	// API-mapped (added 03/29/2007, rvarick)
	public Device returnDeviceFromIdentifier(String identifier)
	{
		return new NullDevice(identifier);
	}
//
	public Device[] getDevices()
	{
    	return new Device[] 
		{ 
    		returnDeviceFromIdentifier("Random (offline test)"),
    	};
	}
   
	
	
	
	private class NullDevice implements Device
	{
		public String toString()
		{
			return getTitle();
		}
		

		public String getTitle()
		{
			return title; 
		}
		
		 private String title;
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
		
		public NullDevice(String name) 
		{
			title = name;
			
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
		
		public double[][] getVoltageGradient() throws IOException
		{
			Random r = new Random();
				    		
			return new double[][]
			{
				{ r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble() }, 
				{ r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble() }, 
				{ r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble() }, 
				{ r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble() }, 
				{ r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble() }, 
				{ r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble() }, 
				{ r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble() }, 
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
	    

	    
	    public boolean isValid()
	    {
	    	try
	    	{
	    		Thread.sleep(1000);
	    	}
	    	catch(Exception e)
	    	{
	    		
	    	}
	    	return true;
	    	
	    }
	}
	
	
	
	
	
	
	
	
}
