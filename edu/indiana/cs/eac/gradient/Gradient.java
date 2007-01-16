/**
 * Gradient.java - Abstract class that defines a gradient.
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

package edu.indiana.cs.eac.gradient;

public interface Gradient 
{
	/**
	 * Sets the (post-interpolation) double array to be used for plotting.
	 * 
	 * @param z - double[][] - values to use for plotting
	 * 
	 */
	public void setPlotSource(double[][] z);
	
	/**
	 * Plots the gradient.
	 *
	 */
	public void plot();
	
	/**
	 * Remove the gradient.
	 *
	 */
	public void removeAllPlots();
}
