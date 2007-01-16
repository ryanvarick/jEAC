/**
 * JEACNode.java - Individual Nodemap objects.
 * 
 * @version 1.0.0
 * 
 * Copyright (C) 2006.  All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 * 
 */

package edu.indiana.cs.eac;

import edu.indiana.cs.eac.exceptions.*;
import java.io.*;

public class JEACNode implements Serializable{
	public static final String SOURCE = "SRC";
	public static final String SINK = "SNK";
	public static final String LLA = "LLA";
	public static final String OFF = "OFF";
	private static final String types[] = {OFF, SOURCE, SINK, LLA};
	private static final int SQUARE_TYPE = 1;
	private String nodeType;
    private double nodeValue;
    private boolean changing;
    
    public JEACNode() {
        nodeType = "OFF";
        nodeValue = 0;
    }
    
    public JEACNode(String type) {
    		if (this.contains(type))
    			nodeType = type;
    		nodeValue = 0;
    }
    
    public void setChanging(boolean mode) {
    		changing = mode;
    }
    
    public boolean isChanging() {
    		return changing;
    }
    
    public String getType() {
        return nodeType;
    }
    
    public double getValue() {
        return nodeValue;
    }
    
    public void setType(String Type) throws InvalidTypeException{
        if (contains(Type))
            nodeType = Type;
        //else
        	//	throw new InvalidTypeException(Type + " is not a valid type");
    }
  
    public boolean isType(String type) {
    		return nodeType.equals(type);
    }
    
    public boolean contains(String condString) {
        for (int i = 0; i < types.length; i++) {
            if (types[i].equals(condString))
                return true;
        }
        return false;
    }
    
    public void setValue(double Value) {
        nodeValue = Value;
    } 
}
