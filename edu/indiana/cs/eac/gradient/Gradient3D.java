/**
 * Gradient3D.java - 3D visualization of the voltage gradient.
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

import org.math.plot.*;

import edu.indiana.cs.eac.hardware.Device;


public class Gradient3D extends Plot3DPanel implements Gradient 
{
	private Device driver;
	private double[] xAxis, yAxis;
	private double[][] zData;
	private GradientInterpolator gradient;
	private String label;
	
	
	
	/**
	 * Constructor - Instantiate a new Gradient3D (Swing component).
	 * 
	 * @param driver - active EAC to communicate with
	 * 
	 */
	public Gradient3D(Device driver)
	{
		this.driver = driver;
		this.label  = "";
		
		// set up interpolation
		gradient = new GradientInterpolator();
		gradient.setInterpolationStrategy(GradientInterpolator.BILINEAR);
		gradient.setInterpolationMultiplier(4);
		
//		// populate initial data
//		double[][] rawGradient;	
//		try
//		{
//			rawGradient = driver.getVoltageGradient();
//		}
//		catch (IOException e)
//		{
//			System.err.println("Could not load gradient, using canned values.");
//			rawGradient = new double[][]
//			     {
//					{0.957, 1.064, 1.416, 1.016, 0.986}, 
//					{0.947, 0.947, 0.957, 0.996, 0.967}, 
//					{0.850, 0.820, 0.908, 0.859, 0.859},
//					{0.820, 0.791, 0.244, 0.752, 0.908}, 
//					{0.850, 0.840, 0.752, 0.771, 0.879}, 
//					{0.869, 0.928, 0.879, 0.840, 0.850}, 
//					{0.947, 0.918, 1.103, 0.976, 0.967}, 
//			     };
//		}
		
		// BUGFIX - v1.0.4 - leave driver commands to the timer
		double[][] rawGradient = new double[driver.getNumReportingRows()][driver.getNumReportingCols()];
		for (int i = 0; i < rawGradient.length; i++)
		{
			for (int j = 0; j < rawGradient[i].length; j++)
			{
				rawGradient[i][j] = 0.0;
			}
		}

		xAxis = gradient.interpolateEmptyAxis(driver.getNumReportingCols());
		yAxis = gradient.interpolateEmptyAxis(driver.getNumReportingRows());

		// plot
		setPlotSource(rawGradient);
		plot();
	}
	
	// API-mapped (Gradient)
	public void setPlotSource(double[][] z)
	{
		// we need to flip the data
		this.zData = gradient.interpolateGradient(z);
	}
		
	// API-mapped (Gradient)
	public void plot()
	{
		// remove the x-y-z labels (flickers on and off)
//		setAxeLabel(0, "");
//		setAxeLabel(1, "");
//		setAxeLabel(2, "");

		addGridPlot("", xAxis, yAxis, zData);
	}
	
	
	
	/**
	 * Sets the graph label.
	 * 
	 * @param String label
	 * 
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}
}
