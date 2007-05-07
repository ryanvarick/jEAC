/**
 * FileFilter.java - Filter for configuration saving.
 * 
 * Copyright (C) 2006.  All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 * 
 * @see http://www.javaworld.com/javaworld/javatips/jw-javatip85.html
 * 
 */

package edu.indiana.cs.eac.ui.deprecated;

import java.io.File;

public class FileFilter extends javax.swing.filechooser.FileFilter
{
	private String extension = "eac";
	
	public String getExtension()
	{
		return "." + extension;
	}
	
	public boolean accept(File f)
	{
		// browse directories
		if (f.isDirectory()) return true;
		  
		// check extension
		if(getExtension(f).equals(extension)) return true;
		else return false;
	}
	    
	public String getDescription()
	{
		return "Analog configurations (" + getExtension() + ")";
	}
	
	// convert the extension to lowercase
	private String getExtension(File f)
	{
		String s = f.getName();
		int i = s.lastIndexOf('.');
		if (i > 0 &&  i < s.length() - 1) return s.substring(i+1).toLowerCase();
		return "";
	}
}