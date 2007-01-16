/**
 * GradientInterpolator.java - Tools to interpolate the voltage gradient.
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

public class GradientInterpolator
{
	// strategy flags
	public static final int BILINEAR = 100;
	
	// interpolation bounds
	private static final int MINIMUM_MULTIPLIER = 0;
	private static final int MAXIMUM_MULTIPLIER = 20;
	
	// other private fields
	private int interpolationStrategy;
	private int interpolationMultiplier;
	
	
	
	/**
	 * Given an offset and a local neighborhood, returns the interpolated value. 
	 * 
	 * @param int s00 - upper-left corner of the neighborhood
	 * @param int s01 - upper-right corner of the neighborhood
	 * @param int s10 - lower-left corner of the neighborhood
	 * @param int s11 - lower-right corner of the neighborhood
	 * @param double xoffset - x-distance from s00 (0.0 - 1.0)
	 * @param double yoffset - y-distance from s00 (0.0 - 1.0)
	 * @return double point - interpolated point
	 * 
	 */
	public double interpolatePoint(
			double s00, double s01, double s10, double s11, double xoffset, double yoffset)
	{
		// sanity check offsets
		if(xoffset > 1.0) { xoffset = 1.0; }
		else if(xoffset < 0.0) { xoffset = 0.0; }

		if(yoffset > 1.0) { yoffset = 1.0; }
		else if(yoffset < 0.0) { yoffset = 0.0; }

		// interpolate point
		double interpolated = 
			(1.0 - yoffset) * ((1.0 - xoffset) * s00 + xoffset * s01) +
			yoffset * ((1.0 - xoffset) * s10 + xoffset * s11);
	
		return interpolated;
	}
	
	/**
	 * Given an integer axis length, returns an array equivalent to the length of
	 *  of an interpolated axis (used to expand axes).
	 *  
	 *  @param int - axis length
	 *  @return double[] - axial array of interpolated length 
	 *  
	 */
	public double[] interpolateEmptyAxis(int numRealValues)
	{
		// to avoid complicated edge situations, we return n-1 interpolated positions
		double[] interpolatedAxis = new double[this.getInterpolatedLength(numRealValues) - 1];
		for(int i = 0; i < interpolatedAxis.length; i++) { interpolatedAxis[i] = i; }
		return interpolatedAxis;
	}

	/**
	 * Given a integer length, returns the cooreponding post-interpolation length.
	 * 
	 * @param int - length
	 * @return int - interpolated length
	 * 
	 */
	public int getInterpolatedLength(int length)
	{
		return (length * this.interpolationMultiplier) - 1;
	}
		
	/**
	 * Interpolates an array of values.
	 *
	 * @param double[][] - values to interpolate
	 * @returns double[][] - interpolated values
	 * 
	 */
	public double[][] interpolateGradient(double[][] rawGradient)
	{	
		// Create a blank matrix to hold the eventual interpolation (FIXME: improper matrix allocation)
		int rawXLength = rawGradient[0].length;
		int rawYLength = rawGradient.length;
		
		int newXLength = this.getInterpolatedLength(rawXLength);
		int newYLength = this.getInterpolatedLength(rawYLength);

		double[][] interpolatedGradient = new double[newYLength][newXLength];
		
		// begin bilinear-interpolation (FIXME: break this into a strategy)
		int multiplier = this.getInterpolationMultiplier();
		double spacing = 1.0 / multiplier;
		
		for(int y = 0; y < (rawGradient.length - 1); y++)
			for(int x = 0; x < (rawGradient[0].length - 1); x++)
			{
				// reset
				double xoffset = 0.0;
				double yoffset = 0.0;

				int xanchor = x * multiplier;
				int yanchor = y * multiplier;
				
				// define the local neighborhood
				int s00x   = x;
				int s00y   = y;
				double s00 = rawGradient[s00y][s00x];
				
				int s01x   = x + 1;
				int s01y   = y;
				double s01 = rawGradient[s01y][s01x];
				
				int s10x   = x;
				int s10y   = y + 1;
				double s10 = rawGradient[s10y][s10x];
				
				int s11x   = x + 1;
				int s11y   = y + 1;
				double s11 = rawGradient[s11y][s11x];
				
				// interpolate! (FIXME: generalize this for N interpolation points)
				for(int i = 0; i <= multiplier + 1; i++)
					for(int j = 0; j <= multiplier + 1; j++)  // set j=0 to interpolate at all points
					{
						xoffset = j * spacing;
						yoffset = i * spacing;
						
						double ival = this.interpolatePoint(s00, s01, s10, s11, xoffset, yoffset);
						//System.out.println("interpolated point (" + (yanchor+j) + "," + (xanchor+i) +") = " + ival);
						interpolatedGradient[yanchor + i][xanchor + j] = ival;
					}

			}
		
		return interpolatedGradient;
	}
	
	/**
	 * Returns the horizontal mirror of a given matrix (kind of a HACK).
	 * 
	 * @param double[][] - matrix to mirror
	 * @return double[][] - mirrored matrix
	 * 
	 */
	public static double[][] mirrorMatrixHorizontally(double[][] m)
	{
		double[][] mirrored = new double[m.length][m[0].length];
		for(int y = 0; y < m.length; y++)
		{
			for(int x = 0; x < m[0].length; x++)
			{
				int x_length = m[0].length - 1;
				mirrored[y][x] = m[y][x_length - x];
			}
		}
		return mirrored;
	}
	
	/**
	 * Returns a 90° rotation of the given matrix (kind of a HACK).
	 * 
	 * @param double[][] - matrix to rotate
	 * @return double[][] - rotate matrix
	 * 
	 */
	public static double[][] rotateMatrix(double[][] m)
	{
		double[][] rotated = new double[m[0].length][m.length];
		for(int y = 0; y < rotated.length; y++)
		{
			for(int x = 0; x < rotated[0].length; x++)
			{
				rotated[y][x] = m[x][y];
			}
		}
		return rotated;
	}
	
	
	
	/* ===============[ GET/SET METHODS ]=============== */
	
	/**
	 * Sets the interpolation strategy to use.
	 * 
	 * @param int flag - constant defined in GradientInterpolator
	 *
	 */
	public void setInterpolationStrategy(int flag)
	{
		this.interpolationStrategy = flag;
	}
	
	/**
	 * Sets the current interpolation multiplier.
	 * 
	 * @param int - multiplier
	 * 
	 */
	public void setInterpolationMultiplier(int value)
	{
		if (value < MINIMUM_MULTIPLIER) { this.interpolationMultiplier = MINIMUM_MULTIPLIER; }
		else if(value > MAXIMUM_MULTIPLIER) { this.interpolationMultiplier = MAXIMUM_MULTIPLIER; }
		else { this.interpolationMultiplier = value; }
	}
	
	/**
	 * Returns the current interpolation multiplier.
	 * 
	 * @return int - multiplier
	 * 
	 */
	public int getInterpolationMultiplier()
	{
		return this.interpolationMultiplier;
	}
}
